package com.project.store_management_tool.repository;

import com.project.store_management_tool.model.Order;
import com.project.store_management_tool.model.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findOrderByUser(User user);
}
