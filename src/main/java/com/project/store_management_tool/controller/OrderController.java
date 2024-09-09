package com.project.store_management_tool.controller;

import com.project.store_management_tool.controller.validator.Validator;
import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
@Slf4j
public class OrderController {
    private OrderService orderService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Order>> getOrderByEmail(@PathVariable String email) {
        if (!Validator.emailValidator(email)) {
            log.error("Incorrect input");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByEmailUser(email));
    }
}
