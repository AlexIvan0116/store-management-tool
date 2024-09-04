package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.LoginUserDTO;
import com.project.store_management_tool.controller.dto.RegisterUserDTO;
import com.project.store_management_tool.controller.dto.converter.RegisterUserDtoToUser;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.UserRepository;
import com.project.store_management_tool.service.exception.UserAlreadyRegisteredException;
import com.project.store_management_tool.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegisterUserDtoToUser dtoConverter;
    private final JWTUtil jwtUtil;

    public User registerUser(RegisterUserDTO registerUserDTO) {
        userRepository.getByEmail(registerUserDTO.getEmail()).ifPresent(
                (user) -> { throw new UserAlreadyRegisteredException(user.getEmail()); });

        return userRepository.save(dtoConverter.covertDtoToModel(registerUserDTO));
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
}
