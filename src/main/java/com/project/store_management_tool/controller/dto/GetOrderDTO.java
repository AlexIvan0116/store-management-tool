package com.project.store_management_tool.controller.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@EqualsAndHashCode
@Getter
@Setter
public class GetOrderDTO {
    private String orderId;

    private String userId;

    private String userEmail;

    private Map<String, Integer> productsAndQuantity;

}
