package com.shivam.userservice.services;

import com.shivam.userservice.exceptions.InvalidPasswordException;
import com.shivam.userservice.exceptions.NoUserFoundException;
import com.shivam.userservice.exceptions.UserAlreadyPresentException;
import com.shivam.userservice.models.Token;
import com.shivam.userservice.models.User;
import com.shivam.userservice.repositories.TokenRepository;
import com.shivam.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;

    UserServiceImpl(UserRepository userRepository,
                    BCryptPasswordEncoder bCryptPasswordEncoder,
                    TokenRepository tokenRepository){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User signUp(String name, String email, String password) throws UserAlreadyPresentException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            // user with email is already present
            throw new UserAlreadyPresentException("a user is already present with email " + email);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }

    @Override
    public Token login(String email, String password) throws NoUserFoundException, InvalidPasswordException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            throw new NoUserFoundException("Invalid email id.");
        }

        User user = optionalUser.get();

        if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())){
            throw new InvalidPasswordException("Either incorrect email or password is entered.");
        }

        Token token = generateToken(user);

        return tokenRepository.save(token);
    }

    private Token generateToken(User user){
        Token token = new Token();
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        token.setUser(user);

        LocalDate currentTime = LocalDate.now();
        LocalDate thirtyDaysFromCurrentTime = currentTime.plusDays(30);

        Date expiryDate = Date.from(thirtyDaysFromCurrentTime.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpiryAt(expiryDate);

        return token;
    }
}