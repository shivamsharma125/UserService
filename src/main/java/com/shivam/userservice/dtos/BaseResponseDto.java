package com.shivam.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseResponseDto {
    private String failureMessage;
    private int statusCode;
}
