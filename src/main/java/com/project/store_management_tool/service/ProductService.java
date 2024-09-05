package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.AddProductDTO;
import com.project.store_management_tool.controller.dto.converter.AddProductDtoToProduct;
import com.project.store_management_tool.model.Product;
import com.project.store_management_tool.repository.ProductRepository;
import com.project.store_management_tool.service.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    private AddProductDtoToProduct addProductDtoToProduct;

    public Product addProduct(AddProductDTO addProductDTO) {
        return productRepository.save(addProductDtoToProduct.covertDtoToModel(addProductDTO));
    }

    public List<Product> addProducts(List<AddProductDTO> addProductDtoToProductList) {
        List<Product> products = addProductDtoToProductList.stream().map(AddProductDTO::convertToModel)
                .collect(Collectors.toList());
        return productRepository.saveAll(products);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }
        return productOptional.get();
    }

    public Product changePriceOfProduct(UUID id, Double price) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found", id);
        }

        Product product = productOptional.get();
        product.setPrice(price);
        productRepository.save(product);
        return productRepository.findById(id).get();
    }
}
