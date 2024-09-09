package com.project.store_management_tool.handlers;

import com.project.store_management_tool.service.exception.ItemNotFoundInOrderException;
import com.project.store_management_tool.service.exception.OrderNotFoundException;
import com.project.store_management_tool.service.exception.ProductNotFoundException;
import com.project.store_management_tool.service.exception.UserAlreadyRegisteredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class HandlerController {
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<String> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException e) {
        log.error(e.getMessage());
        log.debug("Exception thrown: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundInOrderException.class)
    public ResponseEntity<String> handleItemNotFoundInOrderException(ItemNotFoundInOrderException e) {
        log.error(e.getMessage());
        log.debug("Exception thrown: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFoundException(OrderNotFoundException e) {
        log.error(e.getMessage());
        log.debug("Exception thrown: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException e) {
        log.error(e.getMessage());
        log.debug("Exception thrown: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
