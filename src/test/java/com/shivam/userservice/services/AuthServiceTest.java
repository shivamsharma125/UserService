package com.shivam.userservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.userservice.configs.KafkaProducerClient;
import com.shivam.userservice.dtos.EmailDto;
import com.shivam.userservice.exceptions.*;
import com.shivam.userservice.models.Role;
import com.shivam.userservice.models.Session;
import com.shivam.userservice.models.Status;
import com.shivam.userservice.models.User;
import com.shivam.userservice.repositories.RoleRepository;
import com.shivam.userservice.repositories.SessionRepository;
import com.shivam.userservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private KafkaProducerClient kafkaProducerClient;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    ArgumentCaptor<Session> sessionCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Sign up a new user with valid data")
    void test_SignUp_CalledWithValidData_ReturnSavedUser() throws Exception {
        String name = "Shivam Sharma", email = "Shivam@gmail.com", password = "shivam123";

        when(userRepository.existsByEmailAndStatus(email, Status.ACTIVE)).thenReturn(false);
        when(bCryptPasswordEncoder.encode(password)).thenReturn("hashedPassword");
        when(roleRepository.findByNameAndStatus("Customer", Status.ACTIVE)).thenReturn(Optional.empty());
        Role role = new Role("CUSTOMER");
        when(roleRepository.save(any())).thenReturn(role);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword("hashedPassword");
        user.setRoles(List.of(role));

        when(userRepository.save(any())).thenReturn(user);
        when(objectMapper.writeValueAsString(any(EmailDto.class))).thenReturn("email-data-json");

        User savedUser = authService.signUp(name, email, password);

        assertEquals(name, savedUser.getName());
        assertEquals(email, savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getHashedPassword());
        assertEquals(1, savedUser.getRoles().size());
        verify(kafkaProducerClient, times(1)).sendMessage(eq("signup"), eq("email-data-json"));
    }

    @Test
    @DisplayName("Sign up with already existing user should throw exception")
    void test_SignUp_CalledWithExistingEmail_ThrowUserAlreadyExistException() {
        when(userRepository.existsByEmailAndStatus("Shivam@gmail.com", Status.ACTIVE)).thenReturn(true);
        assertThrows(UserAlreadyExistException.class, () -> authService.signUp("Shivam Sharma", "Shivam@gmail.com", "shivam123"));
    }

    @Test
    @DisplayName("Login with correct credentials should return token")
    void test_Login_CalledWithValidCredentials_ReturnToken() {
        String email = "Shivam@gmail.com", password = "shivam123";
        User user = new User();
        user.setEmail(email);
        user.setHashedPassword("hashedPassword");

        when(userRepository.findByEmailAndStatus(email, Status.ACTIVE)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, "hashedPassword")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("jwt-token");

        String token = authService.login(email, password);
        assertEquals("jwt-token", token);
        verify(sessionRepository, times(1)).save(sessionCaptor.capture());
        assertEquals("jwt-token", sessionCaptor.getValue().getToken());
    }

    @Test
    @DisplayName("Login with incorrect password should throw PasswordMismatchException")
    void test_Login_CalledWithInvalidPassword_ThrowPasswordMismatchException() {
        User user = new User();
        user.setHashedPassword("correctHash");
        when(userRepository.findByEmailAndStatus("Shivam@gmail.com", Status.ACTIVE)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("wrongPassword", "correctHash")).thenReturn(false);

        assertThrows(PasswordMismatchException.class, () -> authService.login("Shivam@gmail.com", "wrongPassword"));
    }

    @Test
    @DisplayName("OAuth login should return token for new user")
    void test_OauthLogin_CalledForNewUser_SignUpAndReturnToken() throws Exception {
        when(userRepository.findByEmailAndStatus("Shivam@gmail.com", Status.ACTIVE)).thenReturn(Optional.empty());
        when(userRepository.existsByEmailAndStatus("Shivam@gmail.com", Status.ACTIVE)).thenReturn(false);
        when(roleRepository.findByNameAndStatus("Customer", Status.ACTIVE)).thenReturn(Optional.empty());
        Role role = new Role("CUSTOMER");
        when(roleRepository.save(any())).thenReturn(role);

        User user = new User();
        user.setName("Shivam Sharma");
        user.setEmail("Shivam@gmail.com");
        user.setRoles(List.of(role));

        when(userRepository.save(any())).thenReturn(user);
        when(tokenService.generateToken(any())).thenReturn("jwt-token");

        String token = authService.oauthLogin("Shivam@gmail.com", "Shivam Sharma");

        assertEquals("jwt-token", token);
    }

    @Test
    @DisplayName("Validate token returns true if valid")
    void test_ValidateToken_CalledWithValidToken_ReturnTrue() {
        Long userId = 1L;
        String token = "valid-token";

        Session session = new Session();
        session.setStatus(Status.ACTIVE);
        session.setToken(token);

        when(tokenService.validateToken(token)).thenReturn(true);
        when(sessionRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.of(session));
        when(tokenService.isTokenExpired(token)).thenReturn(false);

        assertTrue(authService.validateToken(userId, token));
    }

    @Test
    @DisplayName("Validate token throws TokenExpiredException if token expired")
    void test_ValidateToken_CalledWithExpiredToken_ThrowTokenExpiredException() {
        Long userId = 1L;
        String token = "expired-token";

        Session session = new Session();
        session.setStatus(Status.ACTIVE);
        session.setToken(token);

        when(tokenService.validateToken(token)).thenReturn(true);
        when(sessionRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.of(session));
        when(tokenService.isTokenExpired(token)).thenReturn(true);

        assertThrows(TokenExpiredException.class, () -> authService.validateToken(userId, token));
        verify(sessionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Validate token throws InvalidTokenException if token is invalid")
    void test_ValidateToken_CalledWithInvalidToken_ThrowInvalidTokenException() {
        when(tokenService.validateToken("invalid-token")).thenReturn(false);
        assertThrows(InvalidTokenException.class, () -> authService.validateToken(1L, "invalid-token"));
    }

    @Test
    @DisplayName("Validate token throws TokenNotFoundException if session is not found")
    void test_ValidateToken_CalledWithMissingSession_ThrowTokenNotFoundException() {
        when(tokenService.validateToken("some-token")).thenReturn(true);
        when(sessionRepository.findByUserIdAndToken(1L, "some-token")).thenReturn(Optional.empty());
        assertThrows(TokenNotFoundException.class, () -> authService.validateToken(1L, "some-token"));
    }

    @Test
    @DisplayName("Validate token throws TokenExpiredException if token is inactive")
    void test_ValidateToken_CalledWithInactiveToken_ThrowTokenExpiredException() {
        Session session = new Session();
        session.setStatus(Status.INACTIVE);
        session.setToken("token");

        when(tokenService.validateToken("token")).thenReturn(true);
        when(sessionRepository.findByUserIdAndToken(1L, "token")).thenReturn(Optional.of(session));

        assertThrows(TokenExpiredException.class, () -> authService.validateToken(1L, "token"));
    }
}
