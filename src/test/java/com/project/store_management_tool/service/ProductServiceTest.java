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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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


    /*
        Adding a new item to the shopping cart that is empty. The result needs to be 12.5 (price) * 2 (quantity) = 25.0
     */
    @Test
    public void addToOrder_InEmptyOrder() {
        Product product = Util.getProduct("masina", 12.5);
        UUID id = product.getId();
        User user = Util.getUser();
        String email = "ex@gmail.com";
        Integer quantity = 2;

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(userRepository.getByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(orderRepository.findOrderByUser(user)).thenReturn(Optional.empty());
        Mockito.when(productItemRepository.saveAndFlush(Mockito.any(ProductItem.class))).thenReturn(new ProductItem());
        Mockito.when(orderRepository.saveAndFlush(Mockito.any(Order.class))).thenReturn(new Order());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(new Product());

        Order order = productService.addToOrder(id, quantity, email);

        Assertions.assertNotNull(order);
        Assertions.assertEquals(25.0, order.getTotalPrice());
        Assertions.assertEquals(1, order.getProductItems().size());
    }

    /*
        Adding 2 "masina" of price 12.5 (total of 25) to already existing cart consisting of
        two items: 3 "banane" and 2 "castraveti" (total of 3 * 4.5 + 2 * 6.5 = 26.5).
        The total price of the cart needs to be of 25 + 26.5 = 51.5
     */
    @Test
    public void addToOrder_ProductItemNotPresentInNonEmptyOrder() {
        Product product = Util.getProduct("masina", 12.5);
        UUID id = product.getId();
        User user = Util.getUser();

        Order order = Util.getOrder();
        Product product1 = Util.getProduct("banane", 4.5);
        Product product2 = Util.getProduct("castraveti", 6.5);
        ProductItem productItem1 = Util.getProductItem();
        ProductItem productItem2 = Util.getProductItem();
        product1.setProductItems(Arrays.asList(productItem1));
        product2.setProductItems(Arrays.asList(productItem2));
        productItem1.setProduct(product1);
        productItem1.setQuantity(3);
        productItem2.setQuantity(2);
        productItem1.setPrice(3 * product1.getPrice());
        productItem2.setPrice(2 * product2.getPrice());
        productItem1.setOrder(order);
        productItem2.setOrder(order);
        order.setProductItems(new ArrayList<>(Arrays.asList(productItem1, productItem2)));

        String email = "ex@gmail.com";
        Integer quantity = 2;

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(userRepository.getByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(orderRepository.findOrderByUser(user)).thenReturn(Optional.of(order));
        Mockito.when(productItemRepository.saveAndFlush(Mockito.any(ProductItem.class))).thenReturn(new ProductItem());
        Mockito.when(orderRepository.saveAndFlush(Mockito.any(Order.class))).thenReturn(new Order());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(new Product());

        Order result = productService.addToOrder(id, quantity, email);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(51.5, result.getTotalPrice());
        Assertions.assertEquals(3, result.getProductItems().size());
    }

    /*
        Increasing the quantity of an item that is already present in the cart.
        Initially, the order has 1 "banane", each of 4.5 price, and 2 "masina" of price 15.5 each.
        By increasing the quantity of the first product with 2, we result in a total price of:
        4.5 * (1 + 2) + 2 * 15.5 = 44.5
     */
    @Test
    public void addToOrder_IncreaseQuantityOfItemInTheOrder() {

        Product product1 = Util.getProduct("banane", 4.5);
        Product product2 = Util.getProduct("masina", 15.5);
        UUID id = product1.getId();

        ProductItem productItem1 = Util.getProductItem("1");
        ProductItem productItem2 = Util.getProductItem("2");

        User user = Util.getUser();

        // empty order
        Order order = Util.getOrder();
        order.setUser(user);

        order.getProductItems().addAll(new ArrayList<>(Arrays.asList(productItem1, productItem2)));

        productItem1.setOrder(order);
        productItem1.setProduct(product1);
        productItem2.setOrder(order);
        productItem2.setProduct(product2);
        productItem1.setPrice(product1.getPrice() * productItem1.getQuantity());
        productItem2.setPrice(product2.getPrice() * productItem2.getQuantity());

        product1.setProductItems(Arrays.asList(productItem1));
        product2.setProductItems(Arrays.asList(productItem2));

        String email = "ex@gmail.com";
        Integer quantity = 2;

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        Mockito.when(userRepository.getByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(orderRepository.findOrderByUser(user)).thenReturn(Optional.of(order));
        Mockito.when(productItemRepository.saveAndFlush(Mockito.any(ProductItem.class))).thenReturn(new ProductItem());
        Mockito.when(orderRepository.saveAndFlush(Mockito.any(Order.class))).thenReturn(new Order());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(new Product());

        Order result = productService.addToOrder(id, quantity, email);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(44.5, result.getTotalPrice());
        Assertions.assertEquals(2, result.getProductItems().size());
        Assertions.assertEquals(3, result.getProductItems().get(0).getQuantity());
    }
}
