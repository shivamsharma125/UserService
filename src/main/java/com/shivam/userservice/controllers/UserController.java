package com.shivam.userservice.controllers;

import com.shivam.userservice.dtos.LoginRequestDto;
import com.shivam.userservice.dtos.SignUpRequestDto;
import com.shivam.userservice.dtos.UserDto;
import com.shivam.userservice.dtos.ValidateTokenDto;
import com.shivam.userservice.models.User;
import com.shivam.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shivam.userservice.utils.UserUtil.from;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    // signUp
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto requestDto) {
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
        String token = userService.login(requestDto.getEmail(), requestDto.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenDto requestDto) {
        Boolean isValidToken = userService.validateToken(requestDto.getUserId(), requestDto.getToken());
        return ResponseEntity.ok(isValidToken);
    }
}
