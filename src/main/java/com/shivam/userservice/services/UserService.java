package com.shivam.userservice.services;

import com.shivam.userservice.exceptions.PasswordMismatchException;
import com.shivam.userservice.exceptions.UserAlreadyExistException;
import com.shivam.userservice.exceptions.UserNotFoundException;
import com.shivam.userservice.models.User;

public interface UserService {
    User signUp(String name, String email, String password) throws UserAlreadyExistException;
    String login(String email, String password) throws UserNotFoundException, PasswordMismatchException;
    Boolean validateToken(Long userId, String token);
    String oauthLogin(String email, String name);
}
