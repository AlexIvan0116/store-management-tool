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
import com.project.store_management_tool.service.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    private AddProductDtoToProduct addProductDtoToProduct;

    public Product addProduct(AddProductDTO addProductDTO) {
        return productRepository.save(addProductDtoToProduct.covertDtoToModel(addProductDTO));
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
            order = optionalOrder.orElseGet(() -> Order.builder().productItems(new ArrayList<>()).id(UUID.randomUUID())
                    .user(optionalUser.get())
                    .build());
            orderRepository.saveAndFlush(order);
            order.getProductItems().add(productItem);
            productItem.setOrder(order);
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

}
