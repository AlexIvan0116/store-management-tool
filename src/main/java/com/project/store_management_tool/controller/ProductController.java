package com.project.store_management_tool.controller;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.controller.validator.Validator;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.service.ProductService;
import com.project.store_management_tool.service.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
@Slf4j
public class ProductController {
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody AddProductDTO addProductDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.addProduct(addProductDTO));
    }

    @PostMapping("/add/multiple")
    public ResponseEntity<List<Product>> addProducts(@RequestBody List<AddProductDTO> addProductDtoToProductList) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.addProducts(addProductDtoToProductList));
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<Product>> getAllProduct() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        if (Validator.UUIDValidator(id)) {
            log.error("Path variable format incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Product());
        }
        Product product;
        try {
            product = productService.getProductById(UUID.fromString(id));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage() + " " + e.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Product());
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PatchMapping("/price/{id}")
    public ResponseEntity<Product> changePriceOfProduct(@PathVariable String id, @RequestBody String price) {
        if (!(Validator.UUIDValidator(id) && Validator.priceValidator(price))) {
            log.error("Path variable format incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Product());
        }

        Product product;
        try {
            product = productService.changePriceOfProduct(UUID.fromString(id), Double.valueOf(price));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage() + " " + e.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Product());
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }
}
