package com.shivam.userservice.controllers;

import com.shivam.userservice.dtos.UpdateUserRequestDto;
import com.shivam.userservice.dtos.UserDto;
import com.shivam.userservice.models.User;
import com.shivam.userservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shivam.userservice.utils.UserUtil.from;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId){
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(from(user));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequestDto request){
        User user = userService.updateUser(userId, request);
        return ResponseEntity.ok(from(user));
    }
}
