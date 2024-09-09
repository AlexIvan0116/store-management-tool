package com.project.store_management_tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.service.UserService;
import com.project.store_management_tool.service.exception.UserAlreadyRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static com.project.store_management_tool.util.Util.getProducts;
import static com.project.store_management_tool.util.Util.getRegisterUserDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getProductById() throws Exception {
        RegisterUserDTO dto = getRegisterUserDto(passwordEncoder);
        User user = dto.convertToModel();
        Mockito.when(userService.registerUser(Mockito.any(RegisterUserDTO.class))).thenReturn(user);
        byte[] inputBody = objectMapper.writeValueAsBytes(dto);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User " + user.getEmail() + " created."));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getProductById_ThrowsUserAlreadyRegisteredException() throws Exception {
        RegisterUserDTO dto = getRegisterUserDto(passwordEncoder);
        Mockito.when(userService.registerUser(Mockito.any(RegisterUserDTO.class))).thenThrow(UserAlreadyRegisteredException.class);
        byte[] inputBody = objectMapper.writeValueAsBytes(dto);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
