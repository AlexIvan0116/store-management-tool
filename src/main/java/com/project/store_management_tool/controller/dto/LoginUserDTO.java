package com.project.store_management_tool.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginUserDTO {
    private String email;
    private String password;
}
