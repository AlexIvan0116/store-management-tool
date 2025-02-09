package com.project.store_management_tool.model;

import com.project.store_management_tool.controller.dto.GetOrderDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order-table")
public class Order {
    @NonNull
    @Id
    private UUID id;

    private double totalPrice;

    @OneToMany(mappedBy = "order", orphanRemoval = true)
    private List<ProductItem> productItems;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public GetOrderDTO convertToGetOrderDTO() {
        return GetOrderDTO.builder()
                .orderId(id.toString())
                .userEmail(user.getEmail())
                .userId(user.getId().toString())
                .productsAndQuantity(productItems.stream()
                        .collect(Collectors.toMap(
                                productItem -> productItem.getProduct().getId().toString(),
                                ProductItem::getQuantity))
                )
                .build();
    }
}
