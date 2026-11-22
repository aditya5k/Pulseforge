
package com.aditya.pulseforge.security;

import com.aditya.pulseforge.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    // 1. Generate JWT
    public String generateToken(String email, UserRole role) {

        Date issuedAt = new Date();
        Date expiration = new Date(
                issuedAt.getTime() + expirationMillis
        );

        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    // 2. Extract username/email
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 3. Extract role
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // 4. Validate token
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {
            Claims claims = extractAllClaims(token);

            String username = claims.getSubject();

            return username.equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    // Common method to parse and verify JWT
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public long getExpirationMillis() {
        return expirationMillis;
    }
}
