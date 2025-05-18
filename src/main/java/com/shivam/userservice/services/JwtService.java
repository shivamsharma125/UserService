package com.shivam.userservice.services;

import com.shivam.userservice.models.Role;
import com.shivam.userservice.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService implements TokenService {
    private final SecretKey secretKey;

    public JwtService(SecretKey secretKey){
        this.secretKey = secretKey;
    }

    public String generateToken(User user) {
        Map<String,Object> payload = new HashMap<>();
        long nowInMillis = System.currentTimeMillis();
        payload.put(Claims.ISSUED_AT,nowInMillis);
        payload.put(Claims.EXPIRATION,nowInMillis + (30L *24*60*60*1000));
        payload.put(Claims.SUBJECT,user.getId());
        payload.put(Claims.ISSUER,"shivam.com");
        payload.put("scope",user.getRoles().stream().map(Role::getName).toList());

        String token = Jwts.builder()
                .claims(payload)
                .signWith(secretKey)
                .compact();

        return token;
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey) // Verifies the signature
                    .build()
                    .parseSignedClaims(token); // Will throw exception if tampered
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean isTokenExpired(String token) {
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Date expiry = claims.getExpiration();

        return expiry.before(new Date());
    }
}
