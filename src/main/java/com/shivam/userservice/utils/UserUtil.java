package com.shivam.userservice.utils;

import com.shivam.userservice.dtos.RoleDto;
import com.shivam.userservice.dtos.UserDto;
import com.shivam.userservice.models.Role;
import com.shivam.userservice.models.User;

import java.util.List;

public class UserUtil {
    public static UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setEmailVerified(user.isEmailVerified());

        if(user.getRoles() != null) {
            List<RoleDto> roleDtos = user.getRoles()
                    .stream()
                    .map(UserUtil::from)
                    .toList();
            userDto.setRoles(roleDtos);
        }

        return userDto;
    }

    public static RoleDto from(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        return roleDto;
    }
}
