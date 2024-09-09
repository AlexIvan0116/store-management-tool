package com.project.store_management_tool.service;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.OrderRepository;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        orderService = new OrderService(orderRepository, userRepository);
    }

    @Test
    public void getOrdersByEmailUser() {
        User user1 = Util.getUser();
        User user2 = Util.getUser();
        User user3 = Util.getUser();
        user1.setEmail("ex1@gmail.com");
        user2.setEmail("ex2@gmail.com");
        user3.setEmail("ex3@gmail.com");

        Order order1 = Util.getOrder();
        Order order2 = Util.getOrder();
        Order order3 = Util.getOrder();
        Order order4 = Util.getOrder();
        Order order5 = Util.getOrder();
        Order order6 = Util.getOrder();
        order1.setUser(user1);
        order6.setUser(user1);
        order2.setUser(user2);
        order3.setUser(user2);
        order4.setUser(user2);
        order5.setUser(user3);

        List<Order> orders = new ArrayList<>(Arrays.asList(order1, order2, order3, order4, order5, order6));
        Mockito.when(userRepository.getByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        Mockito.when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getOrdersByEmailUser(user2.getEmail());

        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.contains(order2));
        Assertions.assertTrue(result.contains(order3));
        Assertions.assertTrue(result.contains(order4));
    }

    @Test
    public void getOrdersByEmailUser_UsernameNotFoundException() {
        Mockito.when(userRepository.getByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> orderService.getOrdersByEmailUser("ex@gmail.com"));
    }
}
