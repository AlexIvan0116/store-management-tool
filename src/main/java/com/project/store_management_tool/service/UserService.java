package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.LoginUserDTO;
import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.controller.dto.UserDto;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.service.exception.UserAlreadyRegisteredException;
import com.project.store_management_tool.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public User registerUser(RegisterUserDTO registerUserDTO) throws UserAlreadyRegisteredException {
        Optional<User> user = userRepository.getByEmail(registerUserDTO.getEmail());

        if (user.isPresent()) {
            throw new UserAlreadyRegisteredException(user.get().getEmail());
        }

        return userRepository.save(registerUserDTO.convertToModel());
    }

    public String loginUser(LoginUserDTO loginUserDTO) {
        Optional<User> optionalUser = userRepository.getByEmail(loginUserDTO.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Wrong password!");
            }

            return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        }

        throw new UsernameNotFoundException("Email not found!");
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(User::convertToDto).collect(Collectors.toList());
    }

    public boolean isTokenValid(String token, LoginUserDTO loginUserDTO) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.getByEmail(loginUserDTO.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User email not registered.");
        }
        return jwtUtil.isTokenValid(token, optionalUser.get().getEmail(), optionalUser.get().getRole().toString());
    }
}
