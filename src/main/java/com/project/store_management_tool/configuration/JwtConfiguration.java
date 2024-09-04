package com.project.store_management_tool.configuration;

import com.project.store_management_tool.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfiguration {

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }
}
