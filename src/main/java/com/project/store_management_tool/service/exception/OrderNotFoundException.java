package com.project.store_management_tool.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class OrderNotFoundException extends RuntimeException {
    private UUID id;

    public OrderNotFoundException(String message, UUID id) {
        super(message);
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Order with id " + id.toString() + " could not be found";
    }
}
