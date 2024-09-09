package com.project.store_management_tool.controller;


import com.project.store_management_tool.controller.validator.Validator;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.service.ProductItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/item")
@AllArgsConstructor
@Slf4j
public class ProductItemController {
    private ProductItemService productItemService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductItem>> getItems() {
        return ResponseEntity.status(HttpStatus.OK).body(productItemService.getAllItems());
    }
}
