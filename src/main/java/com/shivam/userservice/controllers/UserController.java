package com.shivam.userservice.controllers;

import com.shivam.userservice.dtos.*;
import com.shivam.userservice.models.Role;
import com.shivam.userservice.models.User;
import com.shivam.userservice.services.UserService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto requestDto) {
        Pair<User,String> response = userService.login(requestDto.getEmail(), requestDto.getPassword());

        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, response.b);

        return new ResponseEntity<>(from(response.a), headers, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenDto requestDto) {
        Boolean isValidToken = userService.validateToken(requestDto.getUserId(), requestDto.getToken());
        return ResponseEntity.ok(isValidToken);
    }

    private UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setEmailVerified(user.isEmailVerified());

        if(user.getRoles() != null) {
            List<RoleDto> roleDtos = user.getRoles()
                    .stream()
                    .map(this::from)
                    .toList();
            userDto.setRoles(roleDtos);
        }

        return userDto;
    }

    private RoleDto from(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        return roleDto;
    }
}
