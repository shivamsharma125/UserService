package com.shivam.userservice.advices;

import com.shivam.userservice.dtos.ExceptionDto;
import com.shivam.userservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(UserAlreadyPresentException.class)
    public ResponseEntity<ExceptionDto> handleUserAlreadyPresentException(UserAlreadyPresentException exception){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setFailureMessage(exception.getMessage());
        exceptionDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<ExceptionDto> handleNoUserFoundException(NoUserFoundException exception){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setFailureMessage(exception.getMessage());
        exceptionDto.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionDto> handleInvalidPasswordException(InvalidPasswordException exception){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setFailureMessage(exception.getMessage());
        exceptionDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionDto> handleInvalidTokenException(InvalidTokenException exception){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setFailureMessage(exception.getMessage());
        exceptionDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleTokenNotFoundException(TokenNotFoundException exception){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setFailureMessage(exception.getMessage());
        exceptionDto.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }
}
