package com.shivam.userservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class SignUpResponseDto {
    private String name;
    private String email;
    private String failureMessage;
    private int status;
}
