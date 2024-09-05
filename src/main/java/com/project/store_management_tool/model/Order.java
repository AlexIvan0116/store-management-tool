package com.project.store_management_tool.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order")
public class Order {
    @NonNull
    @Id
    private UUID id;

    private double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductItem> productItems;

    @ManyToOne
    @JoinColumn(name = "user-id", nullable = false)
    private User user;
}
