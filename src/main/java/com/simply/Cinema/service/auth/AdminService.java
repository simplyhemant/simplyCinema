package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.exception.ResourceNotFoundException;

import java.util.List;

public interface AdminService {

    UserProfileDto manageUser(Long userId) throws ResourceNotFoundException;

    List<UserProfileDto> getAllUsers();

    void toggleUserStatus(Long userId, Boolean isActive) throws ResourceNotFoundException;
}
