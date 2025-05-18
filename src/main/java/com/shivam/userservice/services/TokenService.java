package com.shivam.userservice.services;

import com.shivam.userservice.models.User;

public interface TokenService {
    String generateToken(User user);
    Boolean validateToken(String token);
    Boolean isTokenExpired(String token);
}
