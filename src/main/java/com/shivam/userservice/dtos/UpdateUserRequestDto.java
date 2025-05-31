package com.shivam.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String name;
    private String password;
    private Boolean isEmailVerified;
}
