package com.vanhuy.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanhuy.user_service.component.JwtUtil;
import com.vanhuy.user_service.dto.UserDTO;
import com.vanhuy.user_service.model.Role;
import com.vanhuy.user_service.model.User;
import com.vanhuy.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private User adminUser;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Create regular user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setActive(true);
        testUser.setRoles(new HashSet<>(Set.of(Role.ROLE_USER.name())));
        testUser.setAddress("Test Address");
        testUser = userRepository.save(testUser);
        userToken = jwtUtil.generateToken(testUser);

        // Create admin user
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("password123"));
        adminUser.setActive(true);
        adminUser.setRoles(new HashSet<>(Set.of(Role.ROLE_ADMIN.name())));
        adminUser = userRepository.save(adminUser);
        adminToken = jwtUtil.generateToken(adminUser);
    }

    @Test
    void testGetUserByUsername_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/username/{username}", "testuser")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testGetUserByUsername_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/username/{username}", "nonexistent")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers_AsAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("includeInactive", "false")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetAllUsers_AsUser_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserById_AsAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/{id}", testUser.getUserId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetUserById_AsUser_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/{id}", testUser.getUserId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Given
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUsername("newuser");
        newUserDTO.setEmail("newuser@example.com");
        newUserDTO.setPassword("password123");
        newUserDTO.setActive(true);
        newUserDTO.setRoles(new HashSet<>(Set.of(Role.ROLE_USER.name())));

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.active").value(true));

        // Verify user was saved
        User savedUser = userRepository.findByUsername("newuser").orElse(null);
        assertNotNull(savedUser);
        assertEquals("newuser@example.com", savedUser.getEmail());
    }

    @Test
    void testCreateUser_DuplicateUsername() throws Exception {
        // Given
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUsername("testuser"); // Already exists
        newUserDTO.setEmail("different@example.com");
        newUserDTO.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_AsAdmin_Success() throws Exception {
        // Given
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("updateduser");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setAddress("Updated Address");

        // When & Then
        mockMvc.perform(put("/api/v1/users/{id}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.address").value("Updated Address"));

        // Verify update in database
        User updatedUser = userRepository.findById(testUser.getUserId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void testUpdateUser_UserNotFound() throws Exception {
        // Given
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("updateduser");
        updateDTO.setEmail("updated@example.com");

        // When & Then
        mockMvc.perform(put("/api/v1/users/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_AsAdmin_Success() throws Exception {
        // Create a user to delete
        User userToDelete = new User();
        userToDelete.setUsername("todelete");
        userToDelete.setEmail("todelete@example.com");
        userToDelete.setPassword(passwordEncoder.encode("password123"));
        userToDelete.setActive(true);
        userToDelete.setRoles(new HashSet<>(Set.of(Role.ROLE_USER.name())));
        userToDelete = userRepository.save(userToDelete);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}", userToDelete.getUserId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify user was deleted
        assertFalse(userRepository.findById(userToDelete.getUserId()).isPresent());
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}", 99999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeactivateUser_AsAdmin_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/users/{id}/deactivate", testUser.getUserId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify user was deactivated
        User deactivatedUser = userRepository.findById(testUser.getUserId()).orElse(null);
        assertNotNull(deactivatedUser);
        assertFalse(deactivatedUser.isActive());
    }

    @Test
    void testReactivateUser_AsAdmin_Success() throws Exception {
        // Given - Deactivate user first
        User userToDeactivate = userRepository.findById(testUser.getUserId()).orElse(null);
        assertNotNull(userToDeactivate);
        userToDeactivate.setActive(false);
        // Ensure roles is mutable
        if (userToDeactivate.getRoles() != null) {
            userToDeactivate.setRoles(new HashSet<>(userToDeactivate.getRoles()));
        }
        userRepository.save(userToDeactivate);

        // When & Then
        mockMvc.perform(post("/api/v1/users/{id}/reactivate", testUser.getUserId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        // Verify user was reactivated
        User reactivatedUser = userRepository.findById(testUser.getUserId()).orElse(null);
        assertNotNull(reactivatedUser);
        assertTrue(reactivatedUser.isActive());
    }

    @Test
    void testGetAllUsers_IncludeInactive() throws Exception {
        // Given - Create inactive user
        User inactiveUser = new User();
        inactiveUser.setUsername("inactive");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPassword(passwordEncoder.encode("password123"));
        inactiveUser.setActive(false);
        inactiveUser.setRoles(new HashSet<>(Set.of(Role.ROLE_USER.name())));
        userRepository.save(inactiveUser);

        // When & Then - Get all users including inactive
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("includeInactive", "true")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3)); // 2 active + 1 inactive

        // When & Then - Get only active users
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("includeInactive", "false")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2)); // Only active users
    }

    @Test
    void testUpdateUserPassword() throws Exception {
        // Given
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("testuser");
        updateDTO.setEmail("test@example.com");
        updateDTO.setPassword("newpassword123");

        // When
        mockMvc.perform(put("/api/v1/users/{id}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Then - Verify password was updated
        User updatedUser = userRepository.findById(testUser.getUserId()).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches("newpassword123", updatedUser.getPassword()));
    }
}

