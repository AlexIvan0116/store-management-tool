package com.project.store_management_tool.service;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.OrderRepository;
import com.project.store_management_tool.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByEmailUser(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.getByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Email is not associated with any account.");
        }

        List<Order> orders = getAllOrders();

        Map<User, List<Order>> groupByUser = orders.stream().collect(Collectors.groupingBy(Order::getUser));

        return groupByUser.get(optionalUser.get());
    }
}
