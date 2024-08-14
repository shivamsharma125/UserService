package com.shivam.userservice.advices;

import com.shivam.userservice.dtos.ExceptionDto;
import com.shivam.userservice.exceptions.InvalidPasswordException;
import com.shivam.userservice.exceptions.InvalidTokenException;
import com.shivam.userservice.exceptions.NoUserFoundException;
import com.shivam.userservice.exceptions.UserAlreadyPresentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(UserAlreadyPresentException.class)
    public ResponseEntity<ExceptionDto> handleUserAlreadyPresentException(UserAlreadyPresentException exception){
        return new ResponseEntity<>(new ExceptionDto(exception.getMessage()), HttpStatus.OK);
    }
    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<ExceptionDto> handleNoUserFoundException(NoUserFoundException exception){
        return new ResponseEntity<>(new ExceptionDto(exception.getMessage()), HttpStatus.OK);
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionDto> handleInvalidPasswordException(InvalidPasswordException exception){
        return new ResponseEntity<>(new ExceptionDto(exception.getMessage()), HttpStatus.OK);
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionDto> handleInvalidTokenException(InvalidTokenException exception){
        return new ResponseEntity<>(new ExceptionDto(exception.getMessage()), HttpStatus.OK);
    }
}
