package com.shivam.userservice.exceptions;

public class NoUserFoundException extends RuntimeException {
    public NoUserFoundException(Long id){
        super("No user found with id: " + id);
    }
    public NoUserFoundException(String message){
        super(message);
    }
}
