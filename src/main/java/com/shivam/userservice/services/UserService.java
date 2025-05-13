package com.shivam.userservice.services;

import com.shivam.userservice.exceptions.PasswordMismatchException;
import com.shivam.userservice.exceptions.UserAlreadyExistException;
import com.shivam.userservice.exceptions.UserNotFoundException;
import com.shivam.userservice.models.User;
import org.antlr.v4.runtime.misc.Pair;

public interface UserService {
    User signUp(String name, String email, String password) throws UserAlreadyExistException;
    Pair<User,String> login(String email, String password) throws UserNotFoundException, PasswordMismatchException;
    Boolean validateToken(Long userId, String token);
}
