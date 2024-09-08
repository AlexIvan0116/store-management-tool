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
@Table(name = "product")
public class Product {
    public Product(Product product) {
        id = product.getId();
        name = product.getName();
        price = product.getPrice();
        description = product.getDescription();
    }

    @Id
    @NonNull
    private UUID id;

    private String name;

    private Double price;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<ProductItem> productItems;
}
