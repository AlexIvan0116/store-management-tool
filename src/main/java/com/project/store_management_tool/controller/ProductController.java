package com.project.store_management_tool.controller;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.controller.dto.AddProductToOrderDTO;
import com.project.store_management_tool.controller.validator.Validator;
import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.service.ProductService;
import com.project.store_management_tool.service.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody AddProductDTO addProductDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.addProduct(addProductDTO));
    }

    @PostMapping("/add/multiple")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> addProducts(@RequestBody List<AddProductDTO> addProductDtoToProductList) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.addProducts(addProductDtoToProductList));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getAllProduct() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAll());
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        if (!Validator.UUIDValidator(id)) {
            log.error("Path variable format incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Product product;
        try {
            product = productService.getProductById(UUID.fromString(id));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage() + " " + e.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PatchMapping("/price/{id}")
    @PreAuthorize("hasRole('ADMIN)')")
    public ResponseEntity<Product> changePriceOfProduct(@PathVariable String id, @RequestBody String price) {
        if (!(Validator.UUIDValidator(id) && Validator.priceValidator(price))) {
            log.error("Path variable format incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Product product;
        try {
            product = productService.changePriceOfProduct(UUID.fromString(id), Double.valueOf(price));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage() + " " + e.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProductById(@PathVariable String id) {
        if (!Validator.UUIDValidator(id)) {
            log.error("Path variable format incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty");
        }
        try {
            productService.deleteProductById(UUID.fromString(id));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage() + " " + e.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empty");
        }
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @PostMapping("/addToOrder/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Order> addToOrder(@PathVariable String id, @RequestBody AddProductToOrderDTO addProductToOrderDTO) {
        if (!(Validator.UUIDValidator(id) &&
                Validator.quantityValidator(addProductToOrderDTO.getQuantity()) &&
                Validator.emailValidator(addProductToOrderDTO.getEmail()))) {
            log.error("Incorrect input");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Order order;
        try {
            order = productService.addToOrder(UUID.fromString(id),
                    Integer.valueOf(addProductToOrderDTO.getQuantity()), addProductToOrderDTO.getEmail());
        } catch (UsernameNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @DeleteMapping("/deleteFromOrder/{orderId}/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteProductFromOrder(@PathVariable String orderId, @PathVariable String productId) {
        if (!(Validator.UUIDValidator(orderId) && Validator.UUIDValidator(productId))) {
            log.error("Incorrect input");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        productService.deleteProductFromOrder(UUID.fromString(orderId), UUID.fromString(productId));

        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
