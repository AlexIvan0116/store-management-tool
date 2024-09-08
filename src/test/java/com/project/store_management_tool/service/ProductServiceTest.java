package com.project.store_management_tool.service;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.OrderRepository;
import com.project.store_management_tool.repository.ProductItemRepository;
import com.project.store_management_tool.repository.ProductRepository;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService(productRepository, productItemRepository, orderRepository, userRepository);
    }


    @Test
    public void addToOrderEmptyProductItem() {
        Optional<Product> optionalProduct = Optional.of(Util.getProduct());
        Product product = optionalProduct.get();
        UUID id = product.getId();
        Optional<User> optionalUser = Optional.of(Util.getUser());
        User user = optionalUser.get();
        String email = "ex@gmail.com";
        Integer quantity = 2;
//        product.setProductItems(Arrays.asList(Util.getProductItem("1"), Util.getProductItem("2")));

        Mockito.when(productRepository.findById(id)).thenReturn(optionalProduct);
        Mockito.when(userRepository.getByEmail(email)).thenReturn(optionalUser);
        Mockito.when(productItemRepository.saveAndFlush(Mockito.any(ProductItem.class))).thenReturn(new ProductItem());
        Mockito.when(orderRepository.saveAndFlush(Mockito.any(Order.class))).thenReturn(new Order());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(new Product());

        Order order = productService.addToOrder(id, quantity, email);

        Assertions.assertNotNull(order);
        Assertions.assertEquals(25.0, order.getTotalPrice());
        Assertions.assertEquals(1, order.getProductItems().size());
    }

    @Test
    public void addToOrder() {
        Optional<Product> optionalProduct = Optional.of(Util.getProduct());
        Product product = optionalProduct.get();
        UUID id = product.getId();
        Optional<User> optionalUser = Optional.of(Util.getUser());
        User user = optionalUser.get();
        String email = "ex@gmail.com";
        Integer quantity = 2;
        product.setProductItems(Arrays.asList(Util.getProductItem("1"), Util.getProductItem("2")));

        // getorder este null

        Mockito.when(productRepository.findById(id)).thenReturn(optionalProduct);
        Mockito.when(userRepository.getByEmail(email)).thenReturn(optionalUser);
        Mockito.when(productItemRepository.saveAndFlush(Mockito.any(ProductItem.class))).thenReturn(new ProductItem());
        Mockito.when(orderRepository.saveAndFlush(Mockito.any(Order.class))).thenReturn(new Order());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(new Product());

        Order order = productService.addToOrder(id, quantity, email);

        Assertions.assertNotNull(order);
        Assertions.assertEquals(25.0, order.getTotalPrice());
        Assertions.assertEquals(3, order.getProductItems().size());
    }
}
