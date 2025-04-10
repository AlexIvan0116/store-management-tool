package com.project.store_management_tool.controller;

import com.project.store_management_tool.controller.dto.order.GetOrderDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.Collectors;

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
        List<GetOrderDTO> orders = Util.getOrders().stream().map(Order::convertToGetOrderDTO).collect(Collectors.toList());

        Page<GetOrderDTO> pageMock = new PageImpl<>(orders, PageRequest.of(0, 20), 4);

        Mockito.when(orderService.getAllOrders(Mockito.any(Pageable.class))).thenReturn(pageMock);

        mockMvc.perform(get("/api/order/all?page=0&size=4")
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(4))
                .andExpect(jsonPath("$.content[2]").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getOrderByEmail() throws Exception {
        List<GetOrderDTO> orders = Util.getOrders().stream().map(Order::convertToGetOrderDTO).collect(Collectors.toList());
        Page<GetOrderDTO> pageMock = new PageImpl<>(orders, PageRequest.of(0, 20), 4);
        String email = "ex@gmail.com";
        Mockito.when(orderService.getOrdersByEmail(Mockito.any(String.class), Mockito.any(Pageable.class))).thenReturn(pageMock);

        mockMvc.perform(get("/api/order/get/{email}", email)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(4))
                .andExpect(jsonPath("$.content[2]").exists());
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
