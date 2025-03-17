package com.project.store_management_tool.controller.dto.item;

import com.project.store_management_tool.model.ProductItem;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemDto {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;

    public static ProductItemDto convertFromModel(ProductItem productItem) {
        return ProductItemDto.builder()
                .id(productItem.getUuid().toString())
                .productId(productItem.getProduct().getId().toString())
                .productName(productItem.getProduct().getName())
                .quantity(productItem.getQuantity())
                .price(productItem.getPrice())
                .build();
    }
}
