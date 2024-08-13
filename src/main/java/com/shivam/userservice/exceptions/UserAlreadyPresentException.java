package com.shivam.userservice.exceptions;

public class UserAlreadyPresentException extends Exception {
    public UserAlreadyPresentException(String message){
        super(message);
    }
}
