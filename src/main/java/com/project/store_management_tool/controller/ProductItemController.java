package com.project.store_management_tool.controller;


import com.project.store_management_tool.controller.validator.Validator;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.service.ProductItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ProductItem>> getItems() {
        return ResponseEntity.status(HttpStatus.OK).body(productItemService.getAllItems());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteItemById(@PathVariable String id) {
        if (!Validator.UUIDValidator(id)) {
            log.error("Path variable format incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad id format");
        }
        productItemService.deleteItemById(UUID.fromString(id));
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
