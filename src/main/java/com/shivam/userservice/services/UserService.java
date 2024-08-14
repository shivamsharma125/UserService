package com.shivam.userservice.services;

import com.shivam.userservice.exceptions.InvalidPasswordException;
import com.shivam.userservice.exceptions.InvalidTokenException;
import com.shivam.userservice.exceptions.NoUserFoundException;
import com.shivam.userservice.exceptions.UserAlreadyPresentException;
import com.shivam.userservice.models.Token;
import com.shivam.userservice.models.User;

public interface UserService {
    User signUp(String name, String email, String password) throws UserAlreadyPresentException;
    Token login(String email, String password) throws NoUserFoundException, InvalidPasswordException;
    void logout(String token) throws InvalidTokenException;
}
