package com.vanhuy.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanhuy.user_service.controller.AuthController;
import com.vanhuy.user_service.dto.RegisterRequest;
import com.vanhuy.user_service.dto.RegisterResponse;
import com.vanhuy.user_service.service.AuthService;
import com.vanhuy.user_service.service.PasswordResetService;
import com.vanhuy.user_service.component.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Bỏ qua security
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordResetService passwordResetService;

    // Unit test for register endpoint
    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "thanvanhuyy@gmail.com", "password");
        RegisterResponse registerResponse = new RegisterResponse("User registered successfully");

        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

}