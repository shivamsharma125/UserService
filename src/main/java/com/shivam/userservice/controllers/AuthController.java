package com.shivam.userservice.controllers;

import com.shivam.userservice.dtos.LoginRequestDto;
import com.shivam.userservice.dtos.SignUpRequestDto;
import com.shivam.userservice.dtos.UserDto;
import com.shivam.userservice.dtos.ValidateTokenDto;
import com.shivam.userservice.exceptions.InvalidRequestException;
import com.shivam.userservice.models.User;
import com.shivam.userservice.services.AuthService;
import com.shivam.userservice.utils.IntegerUtils;
import com.shivam.userservice.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shivam.userservice.utils.UserUtil.from;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService userService;

    public AuthController(AuthService userService){
        this.userService = userService;
    }

    // signUp
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto requestDto) {
        if (StringUtils.isEmpty(requestDto.getName())) throw new InvalidRequestException("Invalid name");
        if (StringUtils.isEmpty(requestDto.getEmail())) throw new InvalidRequestException("Invalid email");
        if (StringUtils.isEmpty(requestDto.getPassword())) throw new InvalidRequestException("Invalid password");

        User user = userService.signUp(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );
        return new ResponseEntity<>(from(user), HttpStatus.CREATED);
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        if (StringUtils.isEmpty(requestDto.getEmail())) throw new InvalidRequestException("Invalid email");
        if (StringUtils.isEmpty(requestDto.getPassword())) throw new InvalidRequestException("Invalid password");

        String token = userService.login(requestDto.getEmail(), requestDto.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenDto requestDto) {
        if (!IntegerUtils.isValidId(requestDto.getUserId())) throw new InvalidRequestException("Invalid user id");
        if (StringUtils.isEmpty(requestDto.getToken())) throw new InvalidRequestException("Invalid token");

        Boolean isValidToken = userService.validateToken(requestDto.getUserId(), requestDto.getToken());
        return ResponseEntity.ok(isValidToken);
    }
}
