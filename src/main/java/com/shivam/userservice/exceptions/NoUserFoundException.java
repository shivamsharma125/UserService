package com.shivam.userservice.exceptions;

public class NoUserFoundException extends Exception {
    public NoUserFoundException(String message){
        super(message);
    }
}
