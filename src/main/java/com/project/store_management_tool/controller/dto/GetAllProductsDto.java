package com.project.store_management_tool.controller.dto;

import com.project.store_management_tool.model.Product;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllProductsDto {
    private List<ProductDto> productsDto;

    public static GetAllProductsDto convertFromModel(List<Product> products) {
        return new GetAllProductsDto(products.stream().map(ProductDto::convertFromModel).collect(Collectors.toList()));
    }
}
