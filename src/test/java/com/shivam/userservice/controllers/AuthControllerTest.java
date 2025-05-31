package com.shivam.userservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.userservice.dtos.LoginRequestDto;
import com.shivam.userservice.dtos.SignUpRequestDto;
import com.shivam.userservice.dtos.ValidateTokenDto;
import com.shivam.userservice.exceptions.PasswordMismatchException;
import com.shivam.userservice.models.User;
import com.shivam.userservice.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Sign up with valid data should return 201 and user info")
    public void test_SignUp_WithValidData_ReturnsCreatedUser() throws Exception {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setName("Shivam Sharma");
        request.setEmail("shivam@gmail.com");
        request.setPassword("shivam123");

        User user = new User();
        user.setId(1L);
        user.setName("Shivam Sharma");
        user.setEmail("shivam@gmail.com");
        user.setRoles(Collections.emptyList());

        when(authService.signUp(anyString(), anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Shivam Sharma")))
                .andExpect(jsonPath("$.email", is("shivam@gmail.com")));

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        verify(authService, times(1)).signUp(anyString(), emailCaptor.capture(), anyString());
        assertEquals("shivam@gmail.com",emailCaptor.getValue());
    }

    @Test
    @DisplayName("Sign up with missing name should return 400")
    public void test_SignUp_MissingName_ReturnsBadRequest() throws Exception {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("shivam@gmail.com");
        request.setPassword("shivam123");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with valid credentials should return JWT token")
    public void test_Login_WithValidCredentials_ReturnsJwtToken() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("shivam@gmail.com");
        request.setPassword("shivam123");

        when(authService.login("shivam@gmail.com", "shivam123"))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-jwt-token"));

        verify(authService, times(1)).login("shivam@gmail.com", "shivam123");
    }

    @Test
    @DisplayName("Login with wrong password should throw exception")
    public void test_Login_InvalidCredentials_ThrowsException() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("shivam@gmail.com");
        request.setPassword("wrong password");

        when(authService.login("shivam@gmail.com", "wrong password"))
                .thenThrow(new PasswordMismatchException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login with missing email should return 400")
    public void test_Login_MissingEmail_ReturnsBadRequest() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setPassword("shivam123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Validate token with correct values returns true")
    public void test_ValidateToken_ValidInput_ReturnsTrue() throws Exception {
        ValidateTokenDto request = new ValidateTokenDto(1L, "token123");

        when(authService.validateToken(1L, "token123")).thenReturn(true);

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(authService, times(1)).validateToken(userIdCaptor.capture(), eq("token123"));
        assertEquals(1L,userIdCaptor.getValue());
    }

    @Test
    @DisplayName("Validate token with incorrect token returns false")
    public void test_ValidateToken_InvalidToken_ReturnsFalse() throws Exception {
        ValidateTokenDto request = new ValidateTokenDto(1L, "invalid-token");

        when(authService.validateToken(1L, "invalid-token")).thenReturn(false);

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("Validate token with missing userId should return 400")
    public void test_ValidateToken_MissingUserId_ReturnsBadRequest() throws Exception {
        ValidateTokenDto request = new ValidateTokenDto();
        request.setToken("token123");

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
