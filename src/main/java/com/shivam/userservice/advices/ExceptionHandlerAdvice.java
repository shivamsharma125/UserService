package com.shivam.userservice.advices;

import com.shivam.userservice.dtos.ExceptionDto;
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
}
