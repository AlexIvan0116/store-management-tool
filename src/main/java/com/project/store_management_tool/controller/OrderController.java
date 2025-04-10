package com.project.store_management_tool.controller;

import com.project.store_management_tool.controller.dto.order.GetOrderDTO;
import com.project.store_management_tool.controller.validator.Validator;
import com.project.store_management_tool.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
@Slf4j
public class OrderController {
    private OrderService orderService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<GetOrderDTO>> getAllOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders(PageRequest.of(page, size)));
    }

    @GetMapping("/get/{email}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<GetOrderDTO>> getOrdersByEmail(@PathVariable String email,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) throws UsernameNotFoundException {
        if (!Validator.emailValidator(email)) {
            log.error("Incorrect input");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByEmail(email, PageRequest.of(page, size)));
    }
}
