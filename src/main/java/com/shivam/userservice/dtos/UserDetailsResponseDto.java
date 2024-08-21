package com.shivam.userservice.dtos;

import com.shivam.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsResponseDto extends BaseResponseDto {
    private UserDto user;
}
