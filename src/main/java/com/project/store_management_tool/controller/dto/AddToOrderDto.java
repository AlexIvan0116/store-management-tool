package com.project.store_management_tool.controller.dto;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.ProductItem;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToOrderDto {
    private String id;
    private String userId;
    private List<String> productItemsIds;
    private double totalPrice;

    public static AddToOrderDto convertFromModel(Order order) {
        return AddToOrderDto.builder()
                .totalPrice(order.getTotalPrice())
                .id(order.getId().toString())
                .userId(order.getUser().getId().toString())
                .productItemsIds(order.getProductItems().stream()
                        .map(productItem -> productItem.getUuid().toString()).collect(Collectors.toList()))
                .build();
    }
}
