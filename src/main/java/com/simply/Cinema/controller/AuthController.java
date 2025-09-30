package com.simply.Cinema.controller;


import com.simply.Cinema.core.user.dto.OtpDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.service.auth.AuthService;
import com.simply.Cinema.response.AuthResponse;
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
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserRepo userRepo;

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

    @PostMapping("/login/pass")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserLoginDto req) throws UserException {
        logger.info("Attempting login for user: {}", req.getEmail());
        AuthResponse authResponse = authService.loginUser(req);
        logger.info("Login successful for user: {}", req.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/send-otp/email")
    public ResponseEntity<String> sendOtpEmail(@RequestBody OtpDto req) throws UserException, MessagingException {
        String email = req.getEmail();
        logger.info("Request to send OTP via email: {}", email);

        if (email == null || email.isBlank()) {
            logger.error("Email cannot be empty.");
            throw new UserException("Email cannot be empty.");
        }

        if (userRepo.existsByEmail(email)) {
            authService.sendEmailOtpForLogin(email);
            logger.info("OTP sent to email {} for login.", email);
            return ResponseEntity.ok("OTP sent successfully to your email for login.");
        } else {
            authService.sendEmailOtpForSignup(email);
            logger.info("OTP sent to email {} for signup.", email);
            return ResponseEntity.ok("OTP sent successfully to your email for signup.");
        }
    }

    @PostMapping("/signup/verify-otp-register")
    public ResponseEntity<AuthResponse> verifyOtpAndRegister(@RequestBody UserRegistrationDto req) throws UserException {
        logger.info("Verifying OTP for registration with email: {} and phone: {}", req.getEmail(), req.getPhone());

        if ((req.getEmail() == null || req.getEmail().isBlank()) && (req.getPhone() == null || req.getPhone().isBlank())) {
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

    @PostMapping("/login/email-otp")
    public ResponseEntity<AuthResponse> loginWithEmailOtp(@RequestBody OtpDto req) throws UserException {
        logger.info("Attempting login via OTP with email: {} or phone: {}", req.getEmail(), req.getPhone());

        if ((req.getEmail() == null || req.getEmail().isBlank()) && (req.getPhone() == null || req.getPhone().isBlank())) {
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

    @PostMapping("/send-otp/phone")
    public ResponseEntity<String> sendOtpPhone(@RequestBody OtpDto req) throws UserException, MessagingException {
        String phone = req.getPhone();
        logger.info("Request to send OTP via phone: {}", phone);

        if (phone == null || phone.isBlank()) {
            logger.error("Phone cannot be empty.");
            throw new UserException("phone cannot be empty.");
        }

        if (userRepo.existsByPhone(phone)) {
            authService.sendPhoneOtpForLogin(phone);
            logger.info("OTP sent to phone {} for login.", phone);
            return ResponseEntity.ok("OTP sent successfully to your Phone for login.");
        } else {
            authService.sendPhoneOtpForSignup(phone);
            logger.info("OTP sent to phone {} for signup.", phone);
            return ResponseEntity.ok("OTP sent successfully to your Phone for signup.");
        }
    }
}