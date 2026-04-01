package com.simply.Cinema.service.auth.impl;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.auth.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepo userRepo;

    @Override
    public UserProfileDto manageUser(Long userId) throws ResourceNotFoundException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIsActive(user.getIsActive());

        // 🔑 Get user roles as strings
       dto.setRoles(getUserRoleNames(user));

        return dto;
    }

    @Override
    public List<UserProfileDto> getAllUsers() {
        List<User> users = userRepo.findAll();
        List<UserProfileDto> userDtos = new ArrayList<>();

        for(User user : users){
            UserProfileDto dto = new UserProfileDto();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setIsActive(user.getIsActive());
            dto.setRoles(getUserRoleNames(user));
            userDtos.add(dto);
        }
        return userDtos;
    }

    @Override
    public void toggleUserStatus(Long userId, Boolean isActive) throws ResourceNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        user.setIsActive(isActive);
        userRepo.save(user);
    }

    public List<String> getUserRoleNames(User user) {
        List<String> roleNames = new ArrayList<>();
        for (UserRole role : user.getRoles()) {
            if (Boolean.TRUE.equals(role.getIsActive())) {
                roleNames.add(role.getRole().name());
            }
        }
        return roleNames;
    }
}
