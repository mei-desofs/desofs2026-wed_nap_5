package com.grupo.learningmore.user.application;

import com.grupo.learningmore.user.domain.User;
import com.grupo.learningmore.user.domain.UserRole;
import com.grupo.learningmore.user.infrastructure.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(String name, String email, String passwordHash, UserRole role) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(name, email, passwordHash, role);
        return repository.save(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
