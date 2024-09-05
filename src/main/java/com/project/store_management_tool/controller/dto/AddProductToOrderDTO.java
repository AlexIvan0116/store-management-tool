package com.project.store_management_tool.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
public class AddProductToOrderDTO {
    private String email;
    private String quantity;
}
