package com.shivam.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {
    private String to;
    private String subject;
    private String body;
}
