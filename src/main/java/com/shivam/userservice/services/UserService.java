package com.shivam.userservice.services;

import com.shivam.userservice.models.User;

public interface UserService {
    User getUserById(Long userId);
}
