package com.project.store_management_tool.controller.dto;

import com.project.store_management_tool.model.User;
import com.project.store_management_tool.model.UserRoles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@RequiredArgsConstructor
public class RegisterUserDTO {
    private String email;
    private String password;
    private UserRoles userRole;
}
