package com.project.store_management_tool.controller;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.service.OrderService;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllOrders() throws Exception {
        List<Order> orders = Util.getOrders();
        Mockito.when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/order/all")
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2]").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getOrderByEmail() throws Exception {
        List<Order> orders = Util.getOrders();
        String email = "ex@gmail.com";
        Mockito.when(orderService.getOrdersByEmailUser(Mockito.any(String.class))).thenReturn(orders);

        mockMvc.perform(get("/api/order/get/{email}", email)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2]").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getOrderByEmail_IncorrectEmailFormatBadRequest() throws Exception {
        List<Order> orders = Util.getOrders();
        String email = "ex!gmail.com";

        mockMvc.perform(get("/api/order/get/{email}", email)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
