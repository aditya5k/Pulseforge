
package com.aditya.pulseforge.dto;

import com.aditya.pulseforge.enums.UserRole;

import java.time.LocalDateTime;

public class AuthResponse {

    private Integer id;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;

    public AuthResponse() {
    }

    public AuthResponse(Integer id, String name, String email,
                        UserRole role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
