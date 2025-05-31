package com.shivam.userservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.userservice.dtos.UpdateUserRequestDto;
import com.shivam.userservice.exceptions.UserNotFoundException;
import com.shivam.userservice.models.User;
import com.shivam.userservice.services.UserService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Get user by valid ID should return user info")
    public void test_GetUserById_ValidId_ReturnsUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Shivam Sharma");
        user.setEmail("Shivam@gmail.com");
        user.setRoles(Collections.emptyList());

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Shivam Sharma")))
                .andExpect(jsonPath("$.email", is("Shivam@gmail.com")))
                .andExpect(jsonPath("$.roles", hasSize(0)));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Get user by non-existing ID should return 404")
    public void test_GetUserById_InvalidId_ReturnsNotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(99L);
    }

    @Test
    @DisplayName("Update user with valid data should return updated user")
    public void test_UpdateUser_ValidRequest_ReturnsUpdatedUserDto() throws Exception {
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setName("Shivam Sharma");
        request.setPassword("shivam123");
        request.setIsEmailVerified(true);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Shivam Sharma");
        updatedUser.setEmail("Shivam@gmail.com");
        updatedUser.setRoles(Collections.emptyList());

        when(userService.updateUser(eq(1L), any(UpdateUserRequestDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Shivam Sharma")))
                .andExpect(jsonPath("$.email", is("Shivam@gmail.com")));

        ArgumentCaptor<UpdateUserRequestDto> captor = ArgumentCaptor.forClass(UpdateUserRequestDto.class);
        verify(userService).updateUser(eq(1L), captor.capture());
        assert(captor.getValue().getName().equals("Shivam Sharma"));
        assert(captor.getValue().getPassword().equals("shivam123"));
    }

    @Test
    @DisplayName("Update user with empty name should still update if allowed")
    public void test_UpdateUser_EmptyName_UpdatesSuccessfully() throws Exception {
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setName("");
        request.setPassword("shivam123");
        request.setIsEmailVerified(false);

        User updatedUser = new User();
        updatedUser.setId(2L);
        updatedUser.setName("");
        updatedUser.setEmail("Shivam@gmail.com");
        updatedUser.setRoles(Collections.emptyList());

        when(userService.updateUser(eq(2L), any(UpdateUserRequestDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("")));

        verify(userService, times(1)).updateUser(eq(2L), any(UpdateUserRequestDto.class));
    }

    @Test
    @DisplayName("Update user with null fields should return updated user with no changes")
    public void test_UpdateUser_NullFields_ReturnsUserDtoUnchanged() throws Exception {
        UpdateUserRequestDto request = new UpdateUserRequestDto();

        User unchangedUser = new User();
        unchangedUser.setId(3L);
        unchangedUser.setName("Shivam Sharma");
        unchangedUser.setEmail("Shivam@gmail.com");
        unchangedUser.setRoles(Collections.emptyList());

        when(userService.updateUser(eq(3L), any(UpdateUserRequestDto.class))).thenReturn(unchangedUser);

        mockMvc.perform(put("/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Shivam Sharma")))
                .andExpect(jsonPath("$.email", is("Shivam@gmail.com")));

        verify(userService, times(1)).updateUser(eq(3L), any(UpdateUserRequestDto.class));
    }

    @Test
    @DisplayName("Update user with invalid ID should return 404")
    public void test_UpdateUser_InvalidId_ThrowsException() throws Exception {
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setName("Test");

        when(userService.updateUser(eq(99L), any(UpdateUserRequestDto.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(99L), any(UpdateUserRequestDto.class));
    }
}
