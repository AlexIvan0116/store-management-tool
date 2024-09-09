package com.project.store_management_tool.controller;

import com.project.store_management_tool.controller.dto.LoginUserDTO;
import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.controller.dto.UserDto;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.service.UserService;
import com.project.store_management_tool.service.exception.UserAlreadyRegisteredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> register(@RequestBody RegisterUserDTO registerUserDTO) throws UserAlreadyRegisteredException {
        log.info(registerUserDTO.getEmail() + " is in process of registering...");

        User user = userService.registerUser(registerUserDTO);

        log.info("User " + user.getEmail() + " created.");
        return ResponseEntity.status(HttpStatus.OK).body("User " + user.getEmail() + " created.");
    }

    @PostMapping("/login")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> login(@RequestBody LoginUserDTO loginUserDTO)  {
        log.info(loginUserDTO.getEmail() + " is in process of logging in...");

        String token;
        try {
            token = userService.loginUser(loginUserDTO);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        log.info("User " + loginUserDTO.getEmail() + " logged in.");
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> userList = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/token/valid/{token}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> getIsTokenValid(@PathVariable String token, @RequestBody LoginUserDTO loginUserDTO) {
        try {
            userService.isTokenValid(token, loginUserDTO);
        } catch (UsernameNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.isTokenValid(token, loginUserDTO));
    }
}
