package com.shivam.userservice.dtos;

import com.shivam.userservice.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponseDto {
    private String username;
    private String email;
    private String token;
    private List<Role> roles;
}
