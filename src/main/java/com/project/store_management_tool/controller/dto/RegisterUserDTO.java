package com.project.store_management_tool.controller.dto;

import com.project.store_management_tool.model.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;

@Getter
@AllArgsConstructor
public class RegisterUserDTO {
    private String email;
    private String password;
    private UserRoles userRole;
}
