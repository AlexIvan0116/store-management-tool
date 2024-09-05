package com.project.store_management_tool.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAlreadyRegisteredException extends RuntimeException {

    private final String email;

    public UserAlreadyRegisteredException(String email) {
        super(email);
        this.email = email;
    }

    @Override
    public String getMessage() {
        return "User with email " + email + " is already registered.";
    }
}
