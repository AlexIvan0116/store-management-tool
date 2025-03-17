package com.project.store_management_tool.controller.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@Builder
public class AddProductToOrderDTO {
    private String email;
    private String quantity;
}
