package com.shivam.userservice.services;

import com.shivam.userservice.exceptions.UserNotFoundException;
import com.shivam.userservice.models.Status;
import com.shivam.userservice.models.User;
import com.shivam.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("user with id " + userId + " does not exist"));
    }
}
