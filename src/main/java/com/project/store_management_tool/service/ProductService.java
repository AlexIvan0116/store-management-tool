package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.OrderRepository;
import com.project.store_management_tool.repository.ProductItemRepository;
import com.project.store_management_tool.repository.ProductRepository;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.service.exception.ItemNotFoundInOrderException;
import com.project.store_management_tool.service.exception.OrderNotFoundException;
import com.project.store_management_tool.service.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
    private ProductRepository productRepository;
    private ProductItemRepository productItemRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    @Transactional
    public Product addProduct(AddProductDTO addProductDTO) {
        return productRepository.save(addProductDTO.convertToModel());
    }

    @Transactional
    public List<Product> addProducts(List<AddProductDTO> addProductDtoToProductList) {
        List<Product> products = addProductDtoToProductList.stream().map(AddProductDTO::convertToModel)
                .collect(Collectors.toList());
        return productRepository.saveAll(products);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID id) throws ProductNotFoundException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }
        return productOptional.get();
    }

    @Transactional
    public Product changePriceOfProduct(UUID id, Double price) throws ProductNotFoundException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }

        Product product = productOptional.get();
        Double oldPrice = product.getPrice();
        product.setPrice(price);
        productRepository.save(product);

        List<ProductItem> productItems = product.getProductItems();

        List<ProductItem> itemsAfterChangingPrice = productItems.stream().map(item -> {
            item.setPrice(item.getQuantity() * price);
            return item;
        }).collect(Collectors.toList());

        itemsAfterChangingPrice.stream().map(item -> {
            Order order = item.getOrder();
            order.setTotalPrice(order.getTotalPrice() + (price - oldPrice) * item.getQuantity());
            return order;
        }).collect(Collectors.toList());

        return productRepository.findById(id).get();
    }

    @Transactional
    public void deleteProductById(UUID id) throws ProductNotFoundException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }
        Product product = productOptional.get();
        Optional<List<ProductItem>> productItemOptional = productItemRepository.findByProduct(product);
        if (productItemOptional.isPresent()) {
            for (ProductItem productItem : productItemOptional.get()) {
                Optional<Order> optionalOrder = orderRepository.findById(productItem.getOrder().getId());
                if (optionalOrder.isPresent()) {
                    Order order = optionalOrder.get();
                    optionalOrder.get().getProductItems().remove(productItem);
                    orderRepository.saveAndFlush(optionalOrder.get());
                    order.setTotalPrice(order.getTotalPrice() - productItem.getPrice());
                }
                productItem.setProduct(new Product());
                productItemRepository.deleteById(productItem.getUuid());
            }
        }
        productRepository.delete(productOptional.get());
    }

    @Transactional
    public Order addToOrder(UUID id, Integer quantity, String email) throws  ProductNotFoundException, UsernameNotFoundException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }

        Optional<User> optionalUser = userRepository.getByEmail(email);
        if (optionalUser.isEmpty()) {
            log.error("Email not found");
            throw new UsernameNotFoundException("Email not found");
        }

        Product product = productOptional.get();

        List<ProductItem> productItems = product.getProductItems();
        ProductItem productItem = updateProductItem(quantity, email, product, productItems);

        Order order = updateOrder(email, productItem);
        productRepository.save(product);

        return order;
    }

    @Transactional
    private ProductItem updateProductItem(Integer quantity, String email, Product product, List<ProductItem> productItems) {
        ProductItem productItem;
        Optional<ProductItem> optionalProductItem =
                productItems.stream().filter(item -> item.getOrder().getUser().getEmail().equals(email)).findFirst();
        if (optionalProductItem.isEmpty()) {
            productItem = ProductItem.builder().uuid(UUID.randomUUID())
                    .quantity(quantity)
                    .product(product)
                    .price(product.getPrice() * quantity)
                    .build();
            productItems.add(productItem);
        } else {
            productItem = optionalProductItem.get();
            productItem.setQuantity(productItem.getQuantity() + quantity);
            productItem.setPrice(productItem.getPrice() + product.getPrice() * quantity);
        }
        productItemRepository.saveAndFlush(productItem);
        return productItem;
    }

    @Transactional
    private Order updateOrder(String email, ProductItem productItem) throws UsernameNotFoundException {
        Order order;
        User user = userRepository.getByEmail(email).get();
        Optional<Order> optionalOrder = orderRepository.findOrderByUser(user);
        if (optionalOrder.isEmpty()) {
            optionalOrder = Optional.of(Order.builder()
                    .productItems(new ArrayList<>(Arrays.asList(productItem))).
                            id(UUID.randomUUID())
                    .build());

        }
        order = optionalOrder.get();
        order.setUser(user);

        if (order.getProductItems().stream().noneMatch(item -> item.getUuid().equals(productItem.getUuid()))) {
            order.getProductItems().add(productItem);
        }

        orderRepository.saveAndFlush(order);
        AtomicReference<Double> totalPrice = new AtomicReference<>(0.0);
        order.getProductItems().forEach(item -> totalPrice.updateAndGet(v -> v + item.getPrice()));
        order.setTotalPrice(totalPrice.get());

        productItemRepository.saveAndFlush(productItem);
        orderRepository.saveAndFlush(order);
        return order;
    }

    @Transactional
    public void deleteProductFromOrder(UUID orderId, UUID productId)
            throws OrderNotFoundException, ProductNotFoundException, ItemNotFoundInOrderException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalOrder.isEmpty()) {
            throw new OrderNotFoundException("Order not found", orderId);
        }

        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product not found", orderId);
        }

        Order order = optionalOrder.get();
        Product product = optionalProduct.get();

        Optional<ProductItem> optionalItem = order.getProductItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)).findFirst();

        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundInOrderException("Item not found in order", productId, orderId);
        }

        order.setTotalPrice(order.getTotalPrice() - optionalItem.get().getPrice());

        order.getProductItems().remove(optionalItem.get());
        product.getProductItems().remove(optionalItem.get());


        productRepository.saveAndFlush(product);
        orderRepository.saveAndFlush(order);
        productItemRepository.delete(optionalItem.get());
    }
}
