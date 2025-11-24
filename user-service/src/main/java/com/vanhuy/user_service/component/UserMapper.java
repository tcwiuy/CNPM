package com.vanhuy.user_service.component;

import com.vanhuy.user_service.dto.UserDTO;
import com.vanhuy.user_service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .profileImageName(user.getProfileImageName())
                .roles(user.getRoles())
                .isActive(user.isActive())
                .build();
    }

    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        user.setProfileImageName(userDTO.getProfileImageName());
        user.setRoles(userDTO.getRoles());
        user.setActive(userDTO.isActive());
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        return user;
    }
}
