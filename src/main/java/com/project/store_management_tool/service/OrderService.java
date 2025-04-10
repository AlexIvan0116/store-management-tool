package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.order.GetOrderDTO;
import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.OrderRepository;
import com.project.store_management_tool.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "orders", key = "'page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize")
    public Page<GetOrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(Order::convertToGetOrderDTO);
    }

    @Cacheable(value = "ordersByEmail", key = "'page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize")
    public Page<GetOrderDTO> getOrdersByEmail(String email, Pageable pageable) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.getByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Email is not associated with any account.");
        }

        return orderRepository.findByUserEmail(email, pageable).map(Order::convertToGetOrderDTO);
    }
}
