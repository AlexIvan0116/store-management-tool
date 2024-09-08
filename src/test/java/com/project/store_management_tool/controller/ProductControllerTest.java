package com.project.store_management_tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.controller.dto.AddProductToOrderDTO;
import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.service.ProductService;
import com.project.store_management_tool.util.Util;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.store_management_tool.util.Util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addProduct() throws Exception {
        AddProductDTO productDTO = getAddProductDTO();
        Product product = productDTO.convertToModel();
        Mockito.when(productService.addProduct(Mockito.any(AddProductDTO.class))).thenReturn(product);
        byte[] inputBody = objectMapper.writeValueAsBytes(productDTO);

        mockMvc.perform(post("/api/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addMultiple() throws Exception {
        List<AddProductDTO> productDTOList = Arrays.asList(getAddProductDTO(), getAddProductDTO());
        List<Product> productList = Arrays.asList(productDTOList.get(0).convertToModel(), productDTOList.get(1).convertToModel());
        Mockito.when(productService.addProducts(Mockito.anyList())).thenReturn(productList);
        byte[] inputBody = objectMapper.writeValueAsBytes(productDTOList);

        mockMvc.perform(post("/api/product/add/multiple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void changePriceOfProduct() throws Exception {
        Product product = getProduct();
        UUID id = product.getId();
        Double newPrice = 15.0;
        Product changedPriceProduct = new Product(product);
        changedPriceProduct.setPrice(newPrice);
        Mockito.when(productService.changePriceOfProduct(id, newPrice)).thenReturn(changedPriceProduct);

        mockMvc.perform(patch("/api/product/price/{id}", id)
                .contentType(MediaType.TEXT_PLAIN)
                .content("15.0")
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.price").value(newPrice.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteProductById() throws Exception {
        Product product = getProduct();
        UUID id = product.getId();

        Mockito.doNothing().when(productService).deleteProductById(id);

        mockMvc.perform(delete("/api/product/delete/{id}", id)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(id.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addToOrder() throws Exception {
        String quantity = "2";
        AddProductToOrderDTO addProductToOrderDTO = getAddProductToOrderDto(quantity);
        Order order = getOrder(quantity);
        UUID id = order.getProductItems().get(0).getProduct().getId();

        Mockito.when(productService.addToOrder(id, Integer.valueOf(quantity), addProductToOrderDTO.getEmail())).thenReturn(order);

        mockMvc.perform(post("/api/product/addToOrder/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(addProductToOrderDTO))
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteProductFromOrder() throws Exception {
        Order order = getOrder("3");
        UUID id = order.getId();
        Product product = order.getProductItems().get(0).getProduct();
        UUID productId = product.getId();

        Mockito.doNothing().when(productService).deleteProductFromOrder(id, productId);

        mockMvc.perform(delete("/api/product/deleteFromOrder/{orderId}/{productId}", id, productId)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Success"));
    }


}
