package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.user.dto.OtpDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.response.AuthResponse;
import jakarta.mail.MessagingException;

public interface AuthService {

    //  Normal Signup with Email and Password (no OTP)
    String createUser(UserRegistrationDto req) throws UserException;

    //  Normal Login with Email and Password (returns JWT token)
    AuthResponse loginUser(UserLoginDto req) throws UserException;

    //  Email OTP Signup
     void sendEmailOtpForSignup(String email) throws UserException, MessagingException;
     String verifyEmailOtpAndRegister(UserRegistrationDto req) throws UserException;

    //  Email OTP Login
    void sendEmailOtpForLogin(String email) throws UserException, MessagingException;
    AuthResponse loginWithEmailOtp(OtpDto req) throws UserException;

    //  Phone OTP Signup
     void sendPhoneOtpForSignup(String phone) throws UserException, MessagingException ;
     String verifyPhoneOtpAndRegister(UserRegistrationDto req) throws UserException;

    //  Phone OTP Login
     void sendPhoneOtpForLogin(String phone) throws UserException, MessagingException;
     AuthResponse loginWithPhoneOtp(OtpDto req) throws UserException;

     //logout
     void logout(String token);

    //reset pass

    //forget password

}
