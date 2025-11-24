package com.vanhuy.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanhuy.user_service.client.NotificationClient;
import com.vanhuy.user_service.dto.AuthResponse;
import com.vanhuy.user_service.dto.LoginRequest;
import com.vanhuy.user_service.dto.RegisterRequest;
import com.vanhuy.user_service.dto.RegisterResponse;
import com.vanhuy.user_service.model.Role;
import com.vanhuy.user_service.model.User;
import com.vanhuy.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private NotificationClient notificationClient;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // Mock notification client to avoid external calls
        doNothing().when(notificationClient).sendWelcomeEmail(any());
    }

    @Test
    void testRegister_Success() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "testuser",
                "test@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        // Verify user was saved in database
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        assertTrue(savedUser.isActive());
        assertTrue(savedUser.getRoles().contains(Role.ROLE_USER.name()));
    }

    @Test
    void testRegister_DuplicateUsername() throws Exception {
        // Given - Create existing user
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setActive(true);
        existingUser.setRoles(Set.of(Role.ROLE_USER.name()));
        userRepository.save(existingUser);

        RegisterRequest registerRequest = new RegisterRequest(
                "existinguser",
                "newemail@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_DuplicateEmail() throws Exception {
        // Given Create existing user
        User existingUser = new User();
        existingUser.setUsername("user1");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setActive(true);
        existingUser.setRoles(Set.of(Role.ROLE_USER.name()));
        userRepository.save(existingUser);

        RegisterRequest registerRequest = new RegisterRequest(
                "newuser",
                "existing@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_InvalidEmail() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "testuser",
                "invalid-email",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_ShortPassword() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "testuser",
                "test@example.com",
                "12345"  // Less than 6 characters
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given - Create user
        User user = new User();
        user.setUsername("loginuser");
        user.setEmail("login@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setActive(true);
        user.setRoles(Set.of(Role.ROLE_USER.name()));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("loginuser", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given - Create user
        User user = new User();
        user.setUsername("loginuser");
        user.setEmail("login@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setActive(true);
        user.setRoles(Set.of(Role.ROLE_USER.name()));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("loginuser", "wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistent", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_InactiveUser() throws Exception {
        // Given - Create inactive user
        User user = new User();
        user.setUsername("inactiveuser");
        user.setEmail("inactive@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setActive(false);
        user.setRoles(Set.of(Role.ROLE_USER.name()));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("inactiveuser", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidateToken_ValidToken() throws Exception {
        // Given - Create user and login to get token
        User user = new User();
        user.setUsername("tokenuser");
        user.setEmail("token@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setActive(true);
        user.setRoles(Set.of(Role.ROLE_USER.name()));
        userRepository.save(user);

        // Login to get token
        LoginRequest loginRequest = new LoginRequest("tokenuser", "password123");
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);
        String token = authResponse.getToken();

        // When & Then - Validate token
        mockMvc.perform(post("/api/v1/auth/validateToken")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void testValidateToken_InvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        mockMvc.perform(post("/api/v1/auth/validateToken")
                        .param("token", invalidToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
}

