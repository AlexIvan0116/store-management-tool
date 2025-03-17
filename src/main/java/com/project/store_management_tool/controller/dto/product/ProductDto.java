package com.project.store_management_tool.controller.dto.product;

import com.project.store_management_tool.model.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private String id;
    private String name;
    private Double price;

    public static ProductDto convertFromModel(Product product) {
        return ProductDto.builder()
                .id(product.getId().toString())
                .price(product.getPrice())
                .name(product.getName())
                .build();
    }
}
