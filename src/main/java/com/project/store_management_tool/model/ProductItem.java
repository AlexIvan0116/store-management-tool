package com.project.store_management_tool.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItem {
    @Id
    @NonNull
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "product-id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order-id", nullable = false)
    private Order order;

    private Integer quantity;
    private Double price;
}
