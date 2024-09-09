package com.project.store_management_tool.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.model.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@Setter
public class RegisterUserDTO {
    private String email;
    private String password;
    private UserRoles userRole;

    @Autowired
    @JsonIgnore
    private PasswordEncoder passwordEncoder;

    public User convertToModel() {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .role(userRole)
                .password(passwordEncoder.encode(password))
                .build();
    }
}
