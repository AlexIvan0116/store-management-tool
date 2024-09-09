package com.project.store_management_tool.util;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.controller.dto.AddProductToOrderDTO;
import com.project.store_management_tool.controller.dto.LoginUserDTO;
import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Util {
    public static List<Order> getOrders() {
        return Arrays.asList(Order.builder().id(UUID.randomUUID()).build(),
                Order.builder().id(UUID.randomUUID()).build(),
                Order.builder().id(UUID.randomUUID()).build());
    }

    public static RegisterUserDTO getRegisterUserDto(PasswordEncoder passwordEncoder) {
        return RegisterUserDTO.builder()
                .email("ex@gmail.com")
                .passwordEncoder(passwordEncoder)
                .password("parola")
                .userRole(UserRoles.USER).build();
    }

    public static List<Product> getProducts() {
        return Arrays.asList(Product.builder().id(UUID.randomUUID()).build(),
                Product.builder().id(UUID.randomUUID()).build(),
                Product.builder().id(UUID.randomUUID()).build());
    }

    public static AddProductDTO getAddProductDTO() {
        return AddProductDTO.builder()
                .description("lalala")
                .name("masina")
                .price(12.5).build();
    }

    public static AddProductToOrderDTO getAddProductToOrderDto(String quantity) {
        return AddProductToOrderDTO.builder()
                .email("ex@gmail.com")
                .quantity(quantity)
                .build();
    }

    public static Order getOrder(String quantity) {
        return Order.builder()
                .id(UUID.randomUUID())
                .productItems(Arrays.asList(getProductItem(quantity))).build();
    }

    /*
        Returns empty order
     */
    public static Order getOrder() {
        return Order.builder()
                .id(UUID.randomUUID())
                .productItems(new ArrayList<>()).build();
    }

    public static Product getProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .description("lalala")
                .name("masina")
                .productItems(new ArrayList<>())
                .price(12.5).build();
    }

    public static Product getProduct(String name, Double price) {
        return Product.builder()
                .id(UUID.randomUUID())
                .description("lalala")
                .name(name)
                .productItems(new ArrayList<>())
                .price(price).build();
    }

    public static ProductItem getProductItem(String quantity) {
        return ProductItem.builder()
                .uuid(UUID.randomUUID())
                .product(getProduct())
                .quantity(Integer.valueOf(quantity)).build();
    }

    public static ProductItem getProductItem() {
        return ProductItem.builder()
                .uuid(UUID.randomUUID())
                .build();
    }

    public static User getUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .email("ex@gmail.com")
                .build();
    }

    public static String getToken(String email, String role) {
        JWTUtil jwtUtil = new JWTUtil();
        return jwtUtil.generateToken(email, role);
    }

    public static LoginUserDTO getLoginUserDto() {
        return LoginUserDTO.builder()
                .email("ex@gmail.com")
                .password("parola").build();
    }
}
