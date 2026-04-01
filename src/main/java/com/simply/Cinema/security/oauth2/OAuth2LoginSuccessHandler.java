package com.simply.Cinema.security.oauth2;

import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.security.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final UserRepo userRepo;
    private final JwtProvider jwtProvider;

    // Hardcode for now as per plan, move to properties later
    @Value("${app.frontend.oauth2.redirect-url:http://localhost:3000/index.html}")
    private String frontendUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String profilePicture = oauth2User.getAttribute("picture");

        logger.info("OAuth2 login success for email: {}", email);

        if (email == null) {
            logger.error("OAuth2 Failed: No email returned from provider.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No email returned from OAuth2 provider");
            return;
        }

        // 1. Check if user exists. If not, auto-register them
        Optional<User> optionalUser = userRepo.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            logger.info("Existing user logged in via OAuth2: {}", email);
        } else {
            logger.info("New user logging in via OAuth2, creating account for: {}", email);
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setProfilePictureUrl(profilePicture);
            user.setIsEmailVerified(true); // Coming straight from Google
            user.setIsActive(true);

            // Assign default CUSTOMER role
            UserRole role = new UserRole();
            role.setRole(UserRoleEnum.ROLE_CUSTOMER);
            role.setUser(user);
            role.setIsActive(true);
            user.getRoles().add(role);

            user = userRepo.save(user); // Persistence
        }

        // 2. Generate a valid JWT token
        String jwtToken = jwtProvider.generateToken(user);
        logger.info("JWT Token generated successfully for OAuth2 user.");

        // 3. Issue HTTP Redirect back to Frontend with the Token
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .queryParam("token", jwtToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
