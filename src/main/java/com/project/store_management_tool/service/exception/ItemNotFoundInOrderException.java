package com.project.store_management_tool.service.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class ItemNotFoundInOrderException extends RuntimeException {
    private UUID productId;
    private UUID orderId;

    public ItemNotFoundInOrderException(String message, UUID productId, UUID orderId) {
        super(message);
        this.productId = productId;
        this.orderId = orderId;
    }

    @Override
    public String getMessage() {
        return "Product with id " + productId.toString() + " could not be found in order " + orderId.toString();
    }
}
