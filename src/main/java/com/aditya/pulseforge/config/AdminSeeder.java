package com.aditya.pulseforge.config;

import com.aditya.pulseforge.entity.User;
import com.aditya.pulseforge.enums.UserRole;
import com.aditya.pulseforge.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminPassword) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {

        // Not configured? Skip silently
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }

        String email = adminEmail.trim().toLowerCase();

        // Already exists? Don't create twice
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User admin = new User();
        admin.setName("Admin");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);
    }
}