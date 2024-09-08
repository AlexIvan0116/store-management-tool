package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.controller.dto.converter.AddProductDtoToProduct;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
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

    public Product addProduct(AddProductDTO addProductDTO) {
        return productRepository.save(addProductDTO.convertToModel());
    }

    public List<Product> addProducts(List<AddProductDTO> addProductDtoToProductList) {
        List<Product> products = addProductDtoToProductList.stream().map(AddProductDTO::convertToModel)
                .collect(Collectors.toList());
        return productRepository.saveAll(products);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }
        return productOptional.get();
    }

    public Product changePriceOfProduct(UUID id, Double price) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }

        Product product = productOptional.get();
        product.setPrice(price);
        productRepository.save(product);
        return productRepository.findById(id).get();
    }

    public void deleteProductById(UUID id) {
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

    public Order addToOrder(UUID id, Integer quantity, String email) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }

        Optional<User> optionalUser = userRepository.getByEmail(email);
        if (!optionalUser.isPresent()) {
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

    private Order updateOrder(String email, ProductItem productItem) throws UsernameNotFoundException {
        Order order;
        Optional<User> optionalUser = userRepository.getByEmail(email);
        if (optionalUser.isPresent()) {
            Optional<Order> optionalOrder = orderRepository.findOrderByUser(optionalUser.get());
            if (optionalOrder.isEmpty()) {
                optionalOrder = Optional.of(Order.builder()
                        .productItems(new ArrayList<>(Arrays.asList(productItem))).
                                id(UUID.randomUUID())
                        .build());

            }
            order = optionalOrder.get();
            order.setUser(optionalUser.get());

            orderRepository.saveAndFlush(order);
            AtomicReference<Double> totalPrice = new AtomicReference<>(0.0);
            order.getProductItems().forEach(item -> totalPrice.updateAndGet(v -> v + item.getPrice()));
            order.setTotalPrice(totalPrice.get());
        } else {
            throw new UsernameNotFoundException("Email not found.");
        }
        productItemRepository.saveAndFlush(productItem);
        orderRepository.saveAndFlush(order);
        return order;
    }

    public void deleteProductFromOrder(UUID orderId, UUID productId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalOrder.isEmpty()) {
            log.error("Order not found");
            throw new OrderNotFoundException("Order not found", orderId);
        }

        if (optionalProduct.isEmpty()) {
            log.error("Product not found");
            throw new ProductNotFoundException("Product not found", orderId);
        }

        Order order = optionalOrder.get();
        Product product = optionalProduct.get();

        Optional<ProductItem> optionalItem = order.getProductItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)).findFirst();

        if (optionalItem.isEmpty()) {
            log.error("Item not found in order");
            throw new ItemNotFoundInOrderException("Item not found in order", productId);
        }

        order.setTotalPrice(order.getTotalPrice() - optionalItem.get().getPrice());

        order.getProductItems().remove(optionalItem.get());
        product.getProductItems().remove(optionalItem.get());


        productRepository.saveAndFlush(product);
        orderRepository.saveAndFlush(order);
        productItemRepository.delete(optionalItem.get());
    }
}
