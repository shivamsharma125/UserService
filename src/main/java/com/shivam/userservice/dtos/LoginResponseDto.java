package com.shivam.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto extends BaseResponseDto {
    private String username;
    private String email;
    private String token;
}
