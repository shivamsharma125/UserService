package com.shivam.userservice.services;

import com.shivam.userservice.dtos.UpdateUserRequestDto;
import com.shivam.userservice.exceptions.UserNotFoundException;
import com.shivam.userservice.models.Status;
import com.shivam.userservice.models.User;
import com.shivam.userservice.repositories.UserRepository;
import com.shivam.userservice.utils.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("user with id " + userId + " does not exist"));
    }

    @Override
    public User updateUser(Long userId, UpdateUserRequestDto request) {
        User user = userRepository.findByIdAndStatus(userId,Status.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("user does not exist or deleted"));

        if (!StringUtils.isEmpty(request.getName()))
            user.setName(request.getName());

        if (!StringUtils.isEmpty(request.getPassword()))
            user.setHashedPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getIsEmailVerified() != null)
            user.setEmailVerified(request.getIsEmailVerified());

        return userRepository.save(user);
    }
}
