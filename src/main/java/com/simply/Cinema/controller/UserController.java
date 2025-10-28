package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.ChangeEmailOrPhoneDto;
import com.simply.Cinema.core.user.dto.UserPreferencesDto;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.service.UserService.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @RequestHeader("Authorization") String jwt) throws UserException {

        log.info("Fetching user profile from JWT token");
        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        log.debug("User profile fetched: {}", user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @RequestBody UserProfileDto dto,
            @RequestHeader("Authorization") String jwt) {

        log.info("Updating user profile");
        UserProfileDto loggedInUser = userService.findUserBYJwtToken(jwt);
        User updatedUser = userService.updateUserProfile(loggedInUser.getId(), dto);

        UserProfileDto updatedDto = new UserProfileDto();
        updatedDto.setId(updatedUser.getId());
        updatedDto.setEmail(updatedUser.getEmail());
        updatedDto.setFirstName(updatedUser.getFirstName());
        updatedDto.setLastName(updatedUser.getLastName());
        updatedDto.setPhone(updatedUser.getPhone());
        updatedDto.setDateOfBirth(updatedUser.getDateOfBirth() != null
                ? updatedUser.getDateOfBirth().toString() : null);
        updatedDto.setGender(updatedUser.getGender());
        updatedDto.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        updatedDto.setPreferredLanguage(updatedUser.getPreferredLanguage());
        updatedDto.setPreferredCityId(updatedUser.getPreferredCityId());
        updatedDto.setMoodPreferences(updatedUser.getMoodPreferences());

        log.debug("User profile updated: {}", updatedDto);
        return ResponseEntity.ok(updatedDto);
    }

    @PostMapping("/change-email/request")
    public ResponseEntity<String> requestEmailChangeOtp(@RequestBody ChangeEmailOrPhoneDto request,
                                                        @RequestHeader("Authorization") String jwt) throws MessagingException {

        log.info("Requesting email change OTP for new email: {}", request.getNewEmail());
        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        userService.sendEmailChangeOtp(request.getNewEmail());
        return ResponseEntity.ok("📨 OTP sent to new email.");
    }

    @PostMapping("/change-email/confirm")
    public ResponseEntity<String> confirmEmailChange(@RequestBody ChangeEmailOrPhoneDto request,
                                                     @RequestHeader("Authorization") String jwt) {

        log.info("Confirming email change");
        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        userService.verifyAndChangeEmail(user.getId(), request);
        log.debug("Email changed successfully for userId: {}", user.getId());
        return ResponseEntity.ok("Email changed successfully. Please log in again.");
    }

    @PutMapping("/preferences/update")
    public ResponseEntity<String> updateUserPreferences(@RequestBody UserPreferencesDto preferencesDto,
                                                        @RequestHeader("Authorization") String jwt) {
        try {
            log.info("Updating user preferences");
            UserProfileDto user = userService.findUserBYJwtToken(jwt);
            userService.updateUserPreferences(user.getId(), preferencesDto);
            log.debug("Preferences updated for userId: {}", user.getId());
            return ResponseEntity.ok("Preferences updated successfully.");
        } catch (UserException e) {
            log.warn("Failed to update preferences: {}", e.getMessage());
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> getUserPreferences(
            @RequestHeader("Authorization") String jwt) {

        log.info("Fetching user preferences");
        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        UserPreferencesDto preferences = userService.getUserPreferences(user.getId());
        log.debug("User preferences fetched: {}", preferences);
        return ResponseEntity.ok(preferences);
    }

    @DeleteMapping("/me/delete")
    public ResponseEntity<String> deleteOwnAccount(@RequestHeader("Authorization") String jwt) throws UserException {

        log.info("Deleting user account");
        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        userService.deleteUser(user.getId());
        log.debug("User account deleted: userId={}", user.getId());
        return ResponseEntity.ok("Your account has been deleted successfully.");
    }

}
