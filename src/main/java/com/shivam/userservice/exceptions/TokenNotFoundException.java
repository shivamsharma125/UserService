package com.shivam.userservice.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenNotFoundException extends Exception {
    public TokenNotFoundException(String message){
        super(message);
    }
}
