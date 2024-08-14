package com.shivam.userservice.controllers;

import com.shivam.userservice.dtos.*;
import com.shivam.userservice.exceptions.InvalidPasswordException;
import com.shivam.userservice.exceptions.InvalidTokenException;
import com.shivam.userservice.exceptions.NoUserFoundException;
import com.shivam.userservice.exceptions.UserAlreadyPresentException;
import com.shivam.userservice.models.Token;
import com.shivam.userservice.models.User;
import com.shivam.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    // signUp
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto requestDto) throws UserAlreadyPresentException {
        User user = userService.signUp(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );
        return new ResponseEntity<>(UserDto.from(user), HttpStatus.OK);
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) throws NoUserFoundException, InvalidPasswordException {
        Token token = userService.login(
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setEmail(token.getUser().getEmail());
        responseDto.setUsername(token.getUser().getName());
        responseDto.setToken(token.getValue());
        responseDto.setRoles(token.getUser().getRoles());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // logOut
    @PostMapping("/logout/{token}")
    public ResponseEntity<LogoutResponseDto> logout(@PathVariable("token") String token) throws InvalidTokenException {
        userService.logout(token);
        return new ResponseEntity<>(
                new LogoutResponseDto("user has been logged out successfully."),
                HttpStatus.OK
        );
    }
}
