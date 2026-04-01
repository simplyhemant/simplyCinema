package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.OtpDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.service.auth.AuthService;
import com.simply.Cinema.response.AuthResponse;
import com.simply.Cinema.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication APIs", description = "APIs for user registration, login, and OTP authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserRepo userRepo;

    @Operation(summary = "Register User with Password", description = "Creates a new user using email/password and returns JWT token")
    @PostMapping("/signup/pass")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegistrationDto req) throws UserException {
        logger.info("Registering new user with email: {}", req.getEmail());
        String jwt = authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");

        List<UserRoleEnum> roles = new ArrayList<>();
        roles.add(UserRoleEnum.ROLE_CUSTOMER);
        res.setRoles(roles);

        logger.info("User registered successfully with email: {}", req.getEmail());
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "Login with Email & Password", description = "Authenticates user using email/password and returns JWT token")
    @PostMapping("/login/pass")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserLoginDto req) throws UserException {
        logger.info("Attempting login for user: {}", req.getEmail());
        AuthResponse authResponse = authService.loginUser(req);
        logger.info("Login successful for user: {}", req.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Send OTP to Email", description = "Sends OTP to email for login or signup based on user existence")
    @PostMapping("/send-otp/email")
    public ResponseEntity<ApiResponse> sendOtpEmail(@RequestBody OtpDto req) throws UserException, MessagingException {
        String email = req.getEmail();
        logger.info("Request to send OTP via email: {}", email);

        if (email == null || email.isBlank()) {
            logger.error("Email cannot be empty.");
            throw new UserException("Email cannot be empty.");
        }

        if (userRepo.existsByEmail(email)) {
            authService.sendEmailOtpForLogin(email);
            logger.info("OTP sent to email {} for login.", email);
            return ResponseEntity.ok(new ApiResponse("OTP sent successfully to your email for login.", true));
        } else {
            authService.sendEmailOtpForSignup(email);
            logger.info("OTP sent to email {} for signup.", email);
            return ResponseEntity.ok(new ApiResponse("OTP sent successfully to your email for signup.", true));
        }
    }

    @Operation(summary = "Verify OTP and Register User", description = "Verifies email/phone OTP and completes user registration")
    @PostMapping("/signup/verify-otp-register")
    public ResponseEntity<AuthResponse> verifyOtpAndRegister(@RequestBody UserRegistrationDto req)
            throws UserException {
        logger.info("Verifying OTP for registration with email: {} and phone: {}", req.getEmail(), req.getPhone());

        if ((req.getEmail() == null || req.getEmail().isBlank())
                && (req.getPhone() == null || req.getPhone().isBlank())) {
            logger.error("Email or Phone is required for OTP verification.");
            throw new UserException("Email or Phone is required.");
        }

        String token;
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            token = authService.verifyEmailOtpAndRegister(req);
            logger.info("Email OTP verified and user registered for email: {}", req.getEmail());
        } else if (req.getPhone() != null && !req.getPhone().isBlank()) {
            token = authService.verifyPhoneOtpAndRegister(req);
            logger.info("Phone OTP verified and user registered for phone: {}", req.getPhone());
        } else {
            logger.error("Invalid input for OTP verification.");
            throw new UserException("Invalid input.");
        }

        AuthResponse res = new AuthResponse();
        res.setMessage("register success");
        res.setRoles(List.of(UserRoleEnum.ROLE_CUSTOMER));

        return ResponseEntity.ok(res);
    }

    @Operation(summary = "Login with OTP", description = "Login using Email OTP or Phone OTP")
    @PostMapping("/login/email-otp")
    public ResponseEntity<AuthResponse> loginWithEmailOtp(@RequestBody OtpDto req) throws UserException {
        logger.info("Attempting login via OTP with email: {} or phone: {}", req.getEmail(), req.getPhone());

        if ((req.getEmail() == null || req.getEmail().isBlank())
                && (req.getPhone() == null || req.getPhone().isBlank())) {
            logger.error("Email or Phone is required for OTP login.");
            throw new UserException("Email or Phone is required.");
        }

        AuthResponse authResponse;
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            authResponse = authService.loginWithEmailOtp(req);
            logger.info("Login successful with email OTP for: {}", req.getEmail());
        } else if (req.getPhone() != null && !req.getPhone().isBlank()) {
            authResponse = authService.loginWithPhoneOtp(req);
            logger.info("Login successful with phone OTP for: {}", req.getPhone());
        } else {
            logger.error("Invalid input for OTP login.");
            throw new UserException("Invalid input.");
        }

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Send OTP to Phone", description = "Sends OTP to phone number for login or signup based on user existence")
    @PostMapping("/send-otp/phone")
    public ResponseEntity<ApiResponse> sendOtpPhone(@RequestBody OtpDto req) throws UserException, MessagingException {
        String phone = req.getPhone();
        logger.info("Request to send OTP via phone: {}", phone);

        if (phone == null || phone.isBlank()) {
            logger.error("Phone cannot be empty.");
            throw new UserException("phone cannot be empty.");
        }

        if (userRepo.existsByPhone(phone)) {
            authService.sendPhoneOtpForLogin(phone);
            logger.info("OTP sent to phone {} for login.", phone);
            return ResponseEntity.ok(new ApiResponse("OTP sent successfully to your Phone for login.", true));
        } else {
            authService.sendPhoneOtpForSignup(phone);
            logger.info("OTP sent to phone {} for signup.", phone);
            return ResponseEntity.ok(new ApiResponse("OTP sent successfully to your Phone for signup.", true));
        }
    }

    // --- Phase 1 Specific Endpoints ---
    @Operation(summary = "Step 1: Register User (Sends OTP)")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserRegistrationDto req)
            throws UserException, MessagingException {
        authService.registerUserInitiate(req);
        return ResponseEntity.ok(new ApiResponse("OTP sent to email. Please verify.", true));
    }

    @Operation(summary = "Step 2: Verify OTP and Complete Registration")
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpDto req) throws UserException {
        String token = authService.verifyOtpFinalize(req.getEmail(), req.getOtp());
        AuthResponse res = new AuthResponse();
        res.setJwt(token);
        res.setMessage("register success");
        res.setRoles(List.of(UserRoleEnum.ROLE_CUSTOMER));
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "Resend OTP for Registration")
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestBody OtpDto req) throws UserException, MessagingException {
        authService.resendOtp(req.getEmail());
        return ResponseEntity.ok(new ApiResponse("OTP resent.", true));
    }

    @Operation(summary = "Login Endpoint Alias")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginDto req) throws UserException {
        return loginUser(req);
    }

    @Operation(summary = "Forgot Password (Send Reset Token)")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody OtpDto req)
            throws UserException, MessagingException {
        authService.forgotPassword(req.getEmail());
        return ResponseEntity.ok(new ApiResponse("Password reset link generated and sent.", true));
    }

    @Operation(summary = "Reset Password Using Token")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam(name = "token") String token, @RequestParam(name = "newPassword") String newPassword) throws UserException {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(new ApiResponse("Password reset successfully.", true));
    }

    @Operation(summary = "Logout (Blacklist JWT)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader(name = "Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(new ApiResponse("Logged out successfully.", true));
    }
}
