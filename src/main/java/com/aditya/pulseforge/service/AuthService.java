package com.aditya.pulseforge.service;

import com.aditya.pulseforge.dto.AuthResponse;
import com.aditya.pulseforge.dto.LoginRequest;
import com.aditya.pulseforge.dto.LoginResponse;
import com.aditya.pulseforge.dto.RegisterRequest;
import com.aditya.pulseforge.entity.User;
import com.aditya.pulseforge.enums.UserRole;
import com.aditya.pulseforge.repository.UserRepository;
import com.aditya.pulseforge.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Constructor injection
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Registration (unchanged)
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.DEVELOPER);

        User savedUser = userRepository.save(user);

        return toAuthResponse(savedUser);
    }

    // Login -> now returns a JWT
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        // Step 1: verify email + password (throws if wrong)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // Step 2: load the user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user was not found"));

        // Step 3: generate the token (NEW)
        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(
                token,
                jwtService.getExpirationMillis(),
                toAuthResponse(user)
        );
    }

    // Small helper so we don't repeat the same constructor call
    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}