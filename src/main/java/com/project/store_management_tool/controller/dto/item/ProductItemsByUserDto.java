package com.project.store_management_tool.controller.dto.item;

import com.project.store_management_tool.model.ProductItem;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemsByUserDto {
    private String userId;
    private String userEmail;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private String orderId;

    public static ProductItemsByUserDto convertFromModel(ProductItem productItem, String userId, String userEmail) {
        return ProductItemsByUserDto.builder()
                .userId(userId)
                .userEmail(userEmail)
                .productId(productItem.getProduct().getId().toString())
                .productName(productItem.getProduct().getName())
                .quantity(productItem.getQuantity())
                .price(productItem.getPrice())
                .orderId(productItem.getOrder().getId().toString())
                .build();
    }
}
