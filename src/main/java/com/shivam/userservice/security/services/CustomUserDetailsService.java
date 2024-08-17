package com.shivam.userservice.security.services;

import com.shivam.userservice.models.User;
import com.shivam.userservice.repositories.UserRepository;
import com.shivam.userservice.security.models.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmailAndDeleted(username, false);

        if (optionalUser.isEmpty()){
            throw new UsernameNotFoundException("User with email: " + username + " does not exist.");
        }

        User user = optionalUser.get();
        return new CustomUserDetails(user);
    }
}
