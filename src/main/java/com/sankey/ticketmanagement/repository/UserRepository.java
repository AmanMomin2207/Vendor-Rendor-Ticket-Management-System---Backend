package com.sankey.ticketmanagement.repository;

import com.sankey.ticketmanagement.model.Role;
import com.sankey.ticketmanagement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByRole(Role role);

    Optional<User> findByEmail(String email);
}