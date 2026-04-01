package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.user.dto.UserRoleDto;
import com.simply.Cinema.exception.UserException;

import java.util.List;

public interface RoleManagementService {

    // admin roles
    void assignRole(Long userId, String roleName) throws UserException;
    List<UserRoleDto> getRolesByUser(Long userId);
    void deleteRole(Long roleId);


    //theatre owner roles



}
