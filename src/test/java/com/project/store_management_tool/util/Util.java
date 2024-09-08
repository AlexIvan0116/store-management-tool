package com.project.store_management_tool.util;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.controller.dto.AddProductToOrderDTO;
import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Util {
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
        return "0".equals(quantity) ? Order.builder()
                .id(UUID.randomUUID())
                .productItems(new ArrayList<>()).build() :
                Order.builder()
                .id(UUID.randomUUID())
                .productItems(Arrays.asList(getProductItem(quantity))).build();
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
                .name("masina")
                .productItems(new ArrayList<>())
                .price(price).build();
    }

    public static ProductItem getProductItem(String quantity) {
        return ProductItem.builder()
                .uuid(UUID.randomUUID())
                .product(getProduct())
                .quantity(Integer.valueOf(quantity)).build();
    }

    public static User getUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .email("ex@gmail.com")
                .build();
    }
}
