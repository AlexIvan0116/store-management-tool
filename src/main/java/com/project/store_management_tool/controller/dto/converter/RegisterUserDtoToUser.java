package com.project.store_management_tool.controller.dto.converter;


import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterUserDtoToUser {
    private PasswordEncoder passwordEncoder;

    public User covertDtoToModel(RegisterUserDTO registerUserDTO) {
        return User.builder()
                .email(registerUserDTO.getEmail())
                .role(registerUserDTO.getUserRole())
                .password(passwordEncoder.encode(registerUserDTO.getPassword()))
                .build();
    }
}