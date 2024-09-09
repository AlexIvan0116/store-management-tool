package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.UserDto;
import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.model.UserRoles;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.util.JWTUtil;
import com.project.store_management_tool.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    public void getAllUsers() {
        User user1 = Util.getUser();
        User user2 = Util.getUser();
        User user3 = Util.getUser();
        user1.setEmail("ex1@gmail.com");
        user1.setRole(UserRoles.USER);
        user2.setEmail("ex2@gmail.com");
        user2.setRole(UserRoles.USER);
        user3.setEmail("ex3@gmail.com");
        user3.setRole(UserRoles.USER);
        List<User> users = new ArrayList<>(Arrays.asList(user1, user2, user3));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("ex2@gmail.com", result.get(1).getEmail());
        Assertions.assertEquals(user1.getId(), result.get(0).getId());
    }
}
