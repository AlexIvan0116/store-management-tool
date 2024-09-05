package com.project.store_management_tool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
}
