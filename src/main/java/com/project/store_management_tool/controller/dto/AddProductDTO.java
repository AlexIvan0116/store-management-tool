package com.project.store_management_tool.controller.dto;

import com.project.store_management_tool.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddProductDTO {
    private final String name;
    private final String description;
    private final Double price;

    public Product convertToModel() {
        return Product.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .price(price)
                .build();
    }
}
