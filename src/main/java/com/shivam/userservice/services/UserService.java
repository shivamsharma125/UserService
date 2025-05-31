package com.shivam.userservice.services;

import com.shivam.userservice.dtos.UpdateUserRequestDto;
import com.shivam.userservice.models.User;

public interface UserService {
    User getUserById(Long userId);
    User updateUser(Long userId, UpdateUserRequestDto request);
}
