package com.shivam.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.userservice.configs.KafkaProducerClient;
import com.shivam.userservice.dtos.SendEmailDto;
import com.shivam.userservice.exceptions.*;
import com.shivam.userservice.models.Role;
import com.shivam.userservice.models.Session;
import com.shivam.userservice.models.Status;
import com.shivam.userservice.models.User;
import com.shivam.userservice.repositories.RoleRepository;
import com.shivam.userservice.repositories.SessionRepository;
import com.shivam.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final KafkaProducerClient kafkaProducerClient;
    private final ObjectMapper objectMapper;
    private final SecretKey secretKey;
    private final SessionRepository sessionRepository;

    UserServiceImpl(UserRepository userRepository,
                    RoleRepository roleRepository,
                    BCryptPasswordEncoder bCryptPasswordEncoder,
                    KafkaProducerClient kafkaProducerClient,
                    ObjectMapper objectMapper,
                    SecretKey secretKey,
                    SessionRepository sessionRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaProducerClient = kafkaProducerClient;
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public User signUp(String name, String email, String password) throws UserAlreadyExistException {
        boolean isUserExist = userRepository.existsByEmailAndStatus(email,Status.ACTIVE);
        if (isUserExist)
            throw new UserAlreadyExistException("User is already present with email " + email);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        Role role = roleRepository.findByNameAndStatus("Customer",Status.ACTIVE)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName("CUSTOMER");
                            return roleRepository.save(newRole);
                        });

        user.setRoles(List.of(role));

        User savedUser = userRepository.save(user);

        // Once user has signed up, send a message to Kafka for sending a welcome email to the user
        sendWelcomeEmail(savedUser);

        return savedUser;
    }

    @Override
    public Pair<User, String> login(String email, String password) throws UserNotFoundException, PasswordMismatchException {
        User user = userRepository.findByEmailAndStatus(email,Status.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("Invalid email id."));

        if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())){
            throw new PasswordMismatchException("Either incorrect email or password is entered.");
        }

        //Generating JWT
        Map<String,Object> payload = new HashMap<>();
        long nowInMillis = System.currentTimeMillis();
        payload.put(Claims.ISSUED_AT,nowInMillis);
        payload.put(Claims.EXPIRATION,nowInMillis + (30L *24*60*60*1000));
        payload.put(Claims.SUBJECT,user.getId().toString());
        payload.put(Claims.ISSUER,"shivam.com");
        payload.put("scope",user.getRoles().stream().map(Role::getName).toList());

        String token = Jwts.builder()
                .claims(payload)
                .signWith(secretKey)
                .compact();

        // Saving this token to maintain multiple sessions for user
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        return new Pair<>(user,token);
    }

    @Override
    public Boolean validateToken(Long userId, String token) {
        Session session = sessionRepository.findByUserIdAndToken(userId,token)
                .orElseThrow(() -> new TokenNotFoundException("token is not associated to the user or expired."));

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Date expiry = claims.getExpiration();
        if (expiry.before(new Date())) {
            session.setStatus(Status.INACTIVE);
            sessionRepository.save(session);
            throw new TokenExpiredException("token is expired");
        }

        return true;
    }

    private void sendWelcomeEmail(User user) {
        SendEmailDto sendEmailDto = new SendEmailDto();
        sendEmailDto.setTo(user.getEmail());
        sendEmailDto.setSubject("Welcome Email");
        sendEmailDto.setBody("Have a great learning experience!!");


        try {
            kafkaProducerClient.sendMessage("SendEmail", objectMapper.writeValueAsString(sendEmailDto));
        } catch (JsonProcessingException e) {
            System.out.println("Something went wrong while sending a message to Kafka");
        }
    }
}