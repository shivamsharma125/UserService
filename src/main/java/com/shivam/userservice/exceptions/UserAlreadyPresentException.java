package com.shivam.userservice.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAlreadyPresentException extends RuntimeException {
    public UserAlreadyPresentException(String message){
        super(message);
    }
}
