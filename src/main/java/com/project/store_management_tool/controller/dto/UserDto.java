package com.project.store_management_tool.controller.dto;

import com.project.store_management_tool.model.UserRoles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID id;

    private String email;

    private String role;

}
