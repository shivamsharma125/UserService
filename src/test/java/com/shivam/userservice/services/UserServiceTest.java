package com.shivam.userservice.services;

import com.shivam.userservice.dtos.UpdateUserRequestDto;
import com.shivam.userservice.exceptions.UserNotFoundException;
import com.shivam.userservice.models.Status;
import com.shivam.userservice.models.User;
import com.shivam.userservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("getUserById with valid id should return user")
    public void test_GetUserById_ValidId_ReturnsUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Shivam Sharma");
        user.setEmail("Shivam@gmail.com");

        when(userRepository.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals("Shivam Sharma", result.getName());
        assertEquals("Shivam@gmail.com", result.getEmail());

        verify(userRepository, times(1)).findByIdAndStatus(1L, Status.ACTIVE);
    }

    @Test
    @DisplayName("getUserById with invalid id should throw exception")
    public void test_GetUserById_InvalidId_ThrowsUserNotFoundException() {
        when(userRepository.findByIdAndStatus(2L, Status.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L));

        verify(userRepository, times(1)).findByIdAndStatus(2L, Status.ACTIVE);
    }

    @Test
    @DisplayName("updateUser with valid input should update fields and save")
    public void test_UpdateUser_ValidInput_UpdatesAndSavesUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Shivam");
        user.setEmail("Shivam@gmail.com");
        user.setEmailVerified(false);

        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setName("Shivam Sharma");
        request.setPassword("shivam123");
        request.setIsEmailVerified(true);

        when(userRepository.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("shivam123")).thenReturn("hashed_shivam123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, request);

        assertEquals("Shivam Sharma", updatedUser.getName());
        assertEquals("hashed_shivam123", updatedUser.getHashedPassword());
        assertTrue(updatedUser.isEmailVerified());

        verify(userRepository, times(1)).findByIdAndStatus(1L, Status.ACTIVE);
        verify(passwordEncoder, times(1)).encode("shivam123");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("Shivam Sharma", userCaptor.getValue().getName());
        assertEquals("hashed_shivam123", userCaptor.getValue().getHashedPassword());
        assertTrue(userCaptor.getValue().isEmailVerified());
    }

    @Test
    @DisplayName("updateUser with null fields should not modify existing data")
    public void test_UpdateUser_NullFields_NoModification() {
        User user = new User();
        user.setId(1L);
        user.setName("Original Name");
        user.setHashedPassword("Original Password");
        user.setEmailVerified(false);

        UpdateUserRequestDto request = new UpdateUserRequestDto();

        when(userRepository.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, request);

        assertEquals("Original Name", result.getName());
        assertEquals("Original Password", result.getHashedPassword());
        assertFalse(result.isEmailVerified());

        verify(userRepository).save(user);
        verify(passwordEncoder, times(0)).encode(anyString());
    }

    @Test
    @DisplayName("updateUser with invalid user should throw exception")
    public void test_UpdateUser_InvalidUser_ThrowsUserNotFoundException() {
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setName("Shivam Sharma");

        when(userRepository.findByIdAndStatus(99L, Status.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, request));

        verify(userRepository, times(1)).findByIdAndStatus(99L, Status.ACTIVE);
    }
}
