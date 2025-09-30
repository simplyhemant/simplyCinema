package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.UserService.UserService;
import com.simply.Cinema.service.auth.AdminService;
import com.simply.Cinema.service.auth.RoleManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final RoleManagementService roleManagementService;
    private final UserService userService;

    @GetMapping("/all/users")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        logger.info("Fetching all users");
        List<UserProfileDto> users = adminService.getAllUsers();
        logger.debug("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable long userId) {
        logger.info("Fetching user with ID: {}", userId);
        UserProfileDto user = userService.getUserById(userId);
        logger.debug("Fetched user details: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user-role/{userId}")
    public ResponseEntity<Set<String>> getUserRole(@PathVariable long userId) {
        logger.info("Fetching roles for user with ID: {}", userId);
        Set<String> roles = roleManagementService.getRolesByUser(userId);
        logger.debug("User ID {} has roles: {}", userId, roles);
        return ResponseEntity.ok(roles);
    }

    // ------------Role management--------------
    @PostMapping("/assign")
    public ResponseEntity<String> assignRole(
            @RequestParam Long userId,
            @RequestParam String roleName) throws BusinessException, ResourceNotFoundException {

        logger.info("Assigning role '{}' to user ID {}", roleName, userId);
        try {
            roleManagementService.assignRole(userId, roleName);
            logger.info("Successfully assigned role '{}' to user ID {}", roleName, userId);
            return ResponseEntity.ok("Role '" + roleName + "' assigned to user " + userId);
        } catch (BusinessException | ResourceNotFoundException e) {
            logger.error("Failed to assign role '{}' to user ID {}: {}", roleName, userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteUserRole(@PathVariable Long roleId) {
        logger.info("Deleting (soft) role with ID: {}", roleId);
        roleManagementService.deleteRole(roleId);  // soft delete
        logger.info("Role ID {} soft deleted successfully", roleId);
        return ResponseEntity.ok("Role deactivated (soft deleted) successfully.");
    }

}

