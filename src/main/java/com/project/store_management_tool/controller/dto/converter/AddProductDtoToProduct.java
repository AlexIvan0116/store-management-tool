package com.project.store_management_tool.controller.dto.converter;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.model.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddProductDtoToProduct {
    public Product covertDtoToModel(AddProductDTO addProductDTO) {
        return Product.builder()
                .id(UUID.randomUUID())
                .name(addProductDTO.getName())
                .description(addProductDTO.getDescription())
                .price(addProductDTO.getPrice())
                .build();
    }
}
