package com.project.store_management_tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.store_management_tool.controller.dto.LoginUserDTO;
import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.controller.dto.UserDto;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.model.UserRoles;
import com.project.store_management_tool.service.UserService;
import com.project.store_management_tool.service.exception.UserAlreadyRegisteredException;
import com.project.store_management_tool.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.store_management_tool.util.Util.*;
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
    public void register() throws Exception {
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
    public void register_ThrowsUserAlreadyRegisteredException() throws Exception {
        RegisterUserDTO dto = getRegisterUserDto(passwordEncoder);
        Mockito.when(userService.registerUser(Mockito.any(RegisterUserDTO.class))).thenThrow(UserAlreadyRegisteredException.class);
        byte[] inputBody = objectMapper.writeValueAsBytes(dto);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void login() throws Exception {
        LoginUserDTO dto = Util.getLoginUserDto();
        String email = dto.getEmail();
        String role = UserRoles.USER.name();
        String jwtToken = Util.getToken(email, role);
        Mockito.when(userService.loginUser(Mockito.any(LoginUserDTO.class))).thenReturn(jwtToken);
        byte[] inputBody = objectMapper.writeValueAsBytes(dto);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(jwtToken));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void login_BadCredentialsException() throws Exception {
        LoginUserDTO dto = Util.getLoginUserDto();

        Mockito.when(userService.loginUser(Mockito.any(LoginUserDTO.class))).thenThrow(BadCredentialsException.class);
        byte[] inputBody = objectMapper.writeValueAsBytes(dto);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void login_UsernameNotFoundException() throws Exception {
        LoginUserDTO dto = Util.getLoginUserDto();

        Mockito.when(userService.loginUser(Mockito.any(LoginUserDTO.class))).thenThrow(UsernameNotFoundException.class);
        byte[] inputBody = objectMapper.writeValueAsBytes(dto);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputBody)
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUsers() throws Exception {
        List<UserDto> users = new ArrayList<>(Arrays.asList(getUserDto(), getUserDto(), getUserDto()));
        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/auth/users")
                .header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(users.get(0).getId().toString()))
                .andExpect(jsonPath("$[2].id").value(users.get(2).getId().toString()));
    }
}
