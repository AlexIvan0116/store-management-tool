package com.project.store_management_tool.service;

import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.repository.ProductItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductItemService {
    private final ProductItemRepository productItemRepository;

    public void deleteItemById(UUID id) {
        productItemRepository.deleteById(id);
    }

    public List<ProductItem> getAllItems() {
        return productItemRepository.findAll();
    }
}
