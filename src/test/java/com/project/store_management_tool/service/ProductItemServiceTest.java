package com.project.store_management_tool.service;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.ProductItemRepository;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductItemServiceTest {
    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductItemService productItemService;

    @BeforeEach
    public void setUp() {
        productItemService = new ProductItemService(productItemRepository, userRepository);
    }

    @Test
    public void getItemsByOrder() {
        ProductItem productItem1 = Util.getProductItem();
        ProductItem productItem2 = Util.getProductItem();
        ProductItem productItem3 = Util.getProductItem();
        ProductItem productItem4 = Util.getProductItem();
        ProductItem productItem5 = Util.getProductItem();
        User user1 = Util.getUser();
        user1.setEmail("ex1@gmail.com");
        User user2 = Util.getUser();
        user2.setEmail("ex2@gmail.com");
        User user3 = Util.getUser();
        user3.setEmail("ex3@gmail.com");
        Order order1 = Util.getOrder();
        order1.setUser(user1);
        Order order2 = Util.getOrder();
        order2.setUser(user2);
        Order order3 = Util.getOrder();
        order3.setUser(user3);

        productItem1.setOrder(order1);
        productItem2.setOrder(order1);
        productItem3.setOrder(order2);
        productItem4.setOrder(order3);
        productItem5.setOrder(order3);
        List<ProductItem> productItems = new ArrayList<>(Arrays.asList(productItem1, productItem2, productItem3, productItem4, productItem5));

        Mockito.when(userRepository.getByEmail(Mockito.any(String.class))).thenReturn(Optional.of(user1));
        Mockito.when(productItemRepository.findAll()).thenReturn(productItems);

        List<ProductItem> result =  productItemService.getItemsByUser(user1.getEmail());
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("ex1@gmail.com", result.get(0).getOrder().getUser().getEmail());
    }

    @Test
    public void getItemsByOrder_NoItemPresent() {
        ProductItem productItem1 = Util.getProductItem();
        ProductItem productItem2 = Util.getProductItem();
        ProductItem productItem3 = Util.getProductItem();
        ProductItem productItem4 = Util.getProductItem();
        ProductItem productItem5 = Util.getProductItem();
        User user1 = Util.getUser();
        user1.setEmail("ex1@gmail.com");
        User user2 = Util.getUser();
        user2.setEmail("ex2@gmail.com");
        User user3 = Util.getUser();
        user3.setEmail("ex3@gmail.com");
        Order order1 = Util.getOrder();
        order1.setUser(user1);
        Order order2 = Util.getOrder();
        order2.setUser(user2);
        Order order3 = Util.getOrder();
        order3.setUser(user3);

        productItem1.setOrder(order1);
        productItem2.setOrder(order1);
        productItem3.setOrder(order3);
        productItem4.setOrder(order3);
        productItem5.setOrder(order3);
        List<ProductItem> productItems = new ArrayList<>(Arrays.asList(productItem1, productItem2, productItem3, productItem4, productItem5));

        Mockito.when(userRepository.getByEmail(Mockito.any(String.class))).thenReturn(Optional.of(user2));
        Mockito.when(productItemRepository.findAll()).thenReturn(productItems);

        List<ProductItem> result =  productItemService.getItemsByUser(user2.getEmail());
        Assertions.assertEquals(0, result.size());
    }
}
