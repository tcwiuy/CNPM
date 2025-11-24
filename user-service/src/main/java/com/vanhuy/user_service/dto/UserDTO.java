package com.vanhuy.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Integer userId;
    private String username;
    private String email;
    private String password;
    private String address;
    private String profileImageName;
    private Set<String> roles;
    private boolean isActive;
}
