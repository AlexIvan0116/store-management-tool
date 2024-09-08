package com.project.store_management_tool.controller;

import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllProducts() throws Exception {
        List<Product> productList = getProducts();
        Mockito.when(productService.getAll()).thenReturn(productList);

        mockMvc.perform(get("/api/product/get/all")
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2]").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getProductById() throws Exception {
        List<Product> productList = getProducts();
        UUID id = productList.get(2).getId();
        Mockito.when(productService.getProductById(id)).thenReturn(productList.get(2));

        mockMvc.perform(get("/api/product/get/{id}", id)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    private List<Product> getProducts() {
        return Arrays.asList(Product.builder().id(UUID.randomUUID()).build(),
                Product.builder().id(UUID.randomUUID()).build(),
                Product.builder().id(UUID.randomUUID()).build());
    }
}
