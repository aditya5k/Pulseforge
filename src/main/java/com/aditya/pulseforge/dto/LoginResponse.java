package com.aditya.pulseforge.dto;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;      // milliseconds
    private AuthResponse user;   // reuse your existing user DTO

    public LoginResponse(String accessToken, long expiresIn, AuthResponse user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public AuthResponse getUser() {
        return user;
    }
}