package com.aditya.pulseforge.security;

import com.aditya.pulseforge.entity.User;
import com.aditya.pulseforge.enums.UserRole;
import com.aditya.pulseforge.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional   // each test's DB changes are rolled back afterwards
class SecurityIntegrationTest {

    private static final String PASSWORD = "Test@12345";

    private static final String VALID_INCIDENT = """
            {
              "title": "Test incident",
              "severity": "HIGH",
              "status": "OPEN",
              "environment": "PRODUCTION",
              "startedAt": "2026-11-21T10:00:00",
              "serviceId": 1
            }
            """;

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // ---------- helpers ----------

    private void createUser(String email, UserRole role) {
        User user = new User();
        user.setName("Test " + role);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setRole(role);
        userRepository.save(user);
    }

    private String loginAndGetToken(String email) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "%s" }
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(body, "$.accessToken");
    }

    private String tokenFor(UserRole role) throws Exception {
        String email = role.name().toLowerCase() + "@test.com";
        createUser(email, role);
        return loginAndGetToken(email);
    }

    // ---------- authentication (401) ----------

    @Test
    void noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withValidCredentials_returnsBearerToken() throws Exception {
        createUser("dev@test.com", UserRole.DEVELOPER);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "dev@test.com", "password": "%s" }
                                """.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        createUser("dev@test.com", UserRole.DEVELOPER);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "dev@test.com", "password": "WrongPass123" }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validToken_returns200() throws Exception {
        String token = tokenFor(UserRole.VIEWER);

        mockMvc.perform(get("/api/incidents")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void tamperedToken_returns401() throws Exception {
        String token = tokenFor(UserRole.DEVELOPER);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        mockMvc.perform(get("/api/incidents")
                        .header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
    }

    // ---------- authorization (403) ----------

    @Test
    void viewer_cannotCreateIncident_returns403() throws Exception {
        String token = tokenFor(UserRole.VIEWER);

        mockMvc.perform(post("/api/incidents")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_INCIDENT))
                .andExpect(status().isForbidden());
    }

    @Test
    void developer_cannotDeleteIncident_returns403() throws Exception {
        String token = tokenFor(UserRole.DEVELOPER);

        mockMvc.perform(delete("/api/incidents/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canDelete_passesSecurity() throws Exception {
        String token = tokenFor(UserRole.ADMIN);

        // Incident 999 doesn't exist → 404 proves the admin got PAST security
        mockMvc.perform(delete("/api/incidents/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
    @Test
    void admin_canDelete_failsSecurity() throws Exception {
        String token = tokenFor(UserRole.ADMIN);

    }
}