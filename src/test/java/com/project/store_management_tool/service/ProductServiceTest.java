package com.project.store_management_tool.service;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.OrderRepository;
import com.project.store_management_tool.repository.ProductItemRepository;
import com.project.store_management_tool.repository.ProductRepository;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.service.exception.ProductNotFoundException;
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

    @Test
    public void addToOrder_AssertProductNotFoundException() {
        Mockito.when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class, () -> productService.addToOrder(UUID.randomUUID(), 1, ""));
        Assertions.assertTrue(thrown.getMessage().contains("Product not found"));
    }

    @Test
    public void addToOrder_AssertUsernameNotFoundException() {
        Mockito.when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(new Product()));
        Mockito.when(userRepository.getByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = Assertions.assertThrows(UsernameNotFoundException.class, () -> productService.addToOrder(UUID.randomUUID(), 1, "ex@gmail.com"));
        Assertions.assertTrue(thrown.getMessage().contains("Email not found"));
    }

    /*
        Changing the price of an item that is present in multiple carts.
        3 carts:
        1 -> 2 "masina" of price 15.0
             5 "banana" of price 4.5
             total of 52.5
        2 -> 1 "banana" of price 4.5
             total 4.5
        3 -> 4 "castraveti" of price 6.5
             3 "masina" of price 15.0
             total of 71
        By giving the new price of 12.5 to "masina", the total values of 1 and 3 carts ar going to be changed as follows:
        1 -> 46.5
        3 -> 62
     */
    @Test
    public void changePriceOfProduct() {
        Order order1 = Util.getOrder();
        Order order2 = Util.getOrder();
        Order order3 = Util.getOrder();
        Product product1 = Util.getProduct("masina", 15.0);
        Product product2 = Util.getProduct("banane", 4.5);
        Product product3 = Util.getProduct("castraveti", 6.5);
        ProductItem productItem1 = Util.getProductItem();
        ProductItem productItem2 = Util.getProductItem();
        ProductItem productItem3 = Util.getProductItem();
        ProductItem productItem4 = Util.getProductItem();
        ProductItem productItem5 = Util.getProductItem();
        product1.setProductItems(new ArrayList<>(Arrays.asList(productItem1, productItem5)));
        productItem1.setProduct(product1);
        productItem5.setProduct(product1);
        product2.setProductItems(new ArrayList<>(Arrays.asList(productItem2, productItem3)));
        productItem2.setProduct(product2);
        productItem3.setProduct(product2);
        product3.setProductItems(new ArrayList<>(Arrays.asList(productItem4)));
        productItem4.setProduct(product3);
        productItem1.setQuantity(2);
        productItem1.setPrice(2 * product1.getPrice());
        productItem1.setOrder(order1);
        productItem2.setQuantity(5);
        productItem2.setPrice(5 * product2.getPrice());
        productItem2.setOrder(order1);
        productItem3.setQuantity(1);
        productItem3.setPrice(1 * product2.getPrice());
        productItem3.setOrder(order2);
        productItem4.setQuantity(4);
        productItem4.setPrice(4 * product3.getPrice());
        productItem4.setOrder(order3);
        productItem5.setQuantity(3);
        productItem5.setPrice(3 * product1.getPrice());
        productItem5.setOrder(order3);
        order1.setProductItems(new ArrayList<>(Arrays.asList(productItem1, productItem2)));
        order1.setTotalPrice(productItem1.getPrice() + productItem2.getPrice());
        order2.setProductItems(new ArrayList<>(Arrays.asList(productItem3)));
        order2.setTotalPrice(productItem3.getPrice());
        order3.setProductItems(new ArrayList<>(Arrays.asList(productItem4, productItem5)));
        order3.setTotalPrice(productItem4.getPrice() + productItem5.getPrice());

        Mockito.when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(product1));
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(new Product());

        Double newPrice = 12.0;

        Assertions.assertTrue(order1.getTotalPrice() == 52.5);
        Assertions.assertTrue(order2.getTotalPrice() == 4.5);
        Assertions.assertTrue(order3.getTotalPrice() == 71);

        Product result = productService.changePriceOfProduct(product1.getId(), newPrice);

        Assertions.assertTrue(order1.getTotalPrice() == 46.5);
        Assertions.assertTrue(order2.getTotalPrice() == 4.5);
        Assertions.assertTrue(order3.getTotalPrice() == 62);
    }

    @Test
    public void changePriceOfProduct_ThrowsProductNotFoundException() {
        Mockito.when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class, () -> productService.changePriceOfProduct(UUID.randomUUID(), 5.0));
        Assertions.assertTrue(thrown.getMessage().contains("Product not found"));
    }
}
