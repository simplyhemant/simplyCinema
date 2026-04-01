package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.dto.UserRoleDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.UserService.UserService;
import com.simply.Cinema.service.auth.AdminService;
import com.simply.Cinema.service.auth.RoleManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for Admin user and role management")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final RoleManagementService roleManagementService;
    private final UserService userService;

    @Operation(summary = "Get All Users", description = "Fetch all registered users in the system")
    @GetMapping("/all/users")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        logger.info("Fetching all users");
        List<UserProfileDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get User By ID", description = "Fetch user details using user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable(name = "userId") long userId) {
        logger.info("Fetching user with ID: {}", userId);
        UserProfileDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get User Roles", description = "Fetch all roles assigned to a specific user")
    @GetMapping("/user-role/{userId}")
    public ResponseEntity<List<UserRoleDto>> getUserRole(@PathVariable(name = "userId") long userId) {
        logger.info("Fetching roles for user with ID: {}", userId);
        List<UserRoleDto> roles = roleManagementService.getRolesByUser(userId);
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Toggle User Active Status", description = "Activate or deactivate a user account")
    @PatchMapping("/user/{userId}/toggle-status")
    public ResponseEntity<ApiResponse> toggleUserStatus(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "isActive") Boolean isActive) throws ResourceNotFoundException {
        logger.info("Toggling status for user ID: {} to {}", userId, isActive);
        adminService.toggleUserStatus(userId, isActive);
        return ResponseEntity.ok(new ApiResponse("User status updated successfully", true));
    }



    @Operation(summary = "Assign Role To User", description = "Assign a specific role to a user")
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse> assignRole(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "roleName") String roleName)
            throws BusinessException, ResourceNotFoundException {

        roleManagementService.assignRole(userId, roleName);
        return ResponseEntity.ok(
                new ApiResponse("Role '" + roleName + "' assigned to user " + userId, true));
    }

    @Operation(summary = "Soft Delete Role", description = "Deactivate a role using role ID (Soft Delete)")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse> deleteUserRole(@PathVariable(name = "roleId") Long roleId) {
        roleManagementService.deleteRole(roleId);
        return ResponseEntity.ok(
                new ApiResponse("Role deactivated (soft deleted) successfully.", true));
    }
}
