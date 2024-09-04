package com.project.store_management_tool.repository;

import com.project.store_management_tool.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> getByEmail(String email);
    Optional<User> getUserById(UUID id);
}
