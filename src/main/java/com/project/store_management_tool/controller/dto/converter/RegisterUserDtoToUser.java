package com.project.store_management_tool.controller.dto.converter;

import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegisterUserDtoToUser {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User covertDtoToModel(RegisterUserDTO registerUserDTO) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(registerUserDTO.getEmail())
                .role(registerUserDTO.getUserRole())
                .password(passwordEncoder.encode(registerUserDTO.getPassword()))
                .build();
    }
}