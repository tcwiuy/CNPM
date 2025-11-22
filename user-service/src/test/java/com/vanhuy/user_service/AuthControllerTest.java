package com.vanhuy.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanhuy.user_service.dto.RegisterRequest;
import com.vanhuy.user_service.dto.RegisterResponse;
import com.vanhuy.user_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testRegisterSuccess() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "username",
                "thanvanhuyy@gmail.com",
                "password123");

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("User registered successfully");
        registerResponse.setUsername(registerRequest.getUsername());
        registerResponse.setEmail(registerRequest.getEmail());

        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.email").value("thanvanhuyy@gmail.com"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

}