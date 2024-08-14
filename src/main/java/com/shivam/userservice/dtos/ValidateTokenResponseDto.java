package com.shivam.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenResponseDto extends BaseResponseDto {
    private UserDto userDto;
}
