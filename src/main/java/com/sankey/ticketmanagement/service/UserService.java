package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.exception.ResourceNotFoundException;
import com.sankey.ticketmanagement.model.User;
import com.sankey.ticketmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User toggleUserStatus(String id) {
        User user = getUserById(id);
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}