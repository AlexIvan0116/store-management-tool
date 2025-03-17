package com.project.store_management_tool.service;

import com.project.store_management_tool.controller.dto.item.ProductItemDto;
import com.project.store_management_tool.controller.dto.item.ProductItemsByUserDto;
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

    public List<ProductItemDto> getAllItems() {
        List<ProductItem> items = productItemRepository.findAll();
        return items.stream().map(ProductItemDto::convertFromModel).collect(Collectors.toList());
    }

    public List<ProductItemsByUserDto> getItemsByUser(String email) throws UsernameNotFoundException {
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
                        .filter(orderListEntry -> user.getEmail().equals(orderListEntry.getKey().getUser().getEmail()))
                        .findFirst();

        if (resultEntry.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductItemsByUserDto> groupedItems = resultEntry.get().getValue()
                .stream().map(
                        productItem -> ProductItemsByUserDto.convertFromModel(productItem, user.getId().toString(), email))
                .collect(Collectors.toList());

        return groupedItems;
    }
}
