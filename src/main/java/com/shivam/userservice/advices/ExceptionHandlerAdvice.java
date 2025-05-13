package com.shivam.userservice.advices;

import com.shivam.userservice.exceptions.PasswordMismatchException;
import com.shivam.userservice.exceptions.TokenNotFoundException;
import com.shivam.userservice.exceptions.UserAlreadyExistException;
import com.shivam.userservice.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<String> handleConflict(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFound(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({PasswordMismatchException.class, TokenNotFoundException.class})
    public ResponseEntity<String> handleUnAuthorizedCase(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.UNAUTHORIZED);
    }
}
