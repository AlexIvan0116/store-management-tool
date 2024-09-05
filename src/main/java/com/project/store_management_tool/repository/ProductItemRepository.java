package com.project.store_management_tool.repository;

import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {
    Optional<ProductItem> findByProduct(Product product);
}
