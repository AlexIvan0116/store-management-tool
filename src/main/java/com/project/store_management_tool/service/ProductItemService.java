package com.project.store_management_tool.service;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.ProductItem;
import com.project.store_management_tool.model.User;
import com.project.store_management_tool.repository.ProductItemRepository;
import com.project.store_management_tool.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductItemService {
    private final ProductItemRepository productItemRepository;
    private final UserRepository userRepository;

    public List<ProductItem> getAllItems() {
        return productItemRepository.findAll();
    }

    public List<ProductItem> getItemsByUser(String email) {
        Optional<User> optionalUser = userRepository.getByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found.");
        }
        List<ProductItem> productItems = productItemRepository.findAll();
        Map<Order, List<ProductItem>> groupedProductItems =
                productItems.stream().collect(Collectors.groupingBy(ProductItem::getOrder));

        User user = optionalUser.get();
        Optional<Map.Entry<Order, List<ProductItem>>> resultEntry =
                groupedProductItems.entrySet().stream()
                        .filter(orderListEntry -> user.equals(orderListEntry.getKey().getUser())).findFirst();

        return resultEntry.isPresent() ? resultEntry.get().getValue() : new ArrayList<>();
    }
}
