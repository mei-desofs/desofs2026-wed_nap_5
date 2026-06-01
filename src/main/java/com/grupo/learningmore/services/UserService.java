package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.repositories.UserRepository;
import com.grupo.learningmore.domain.user.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String name,
                           String email,
                           String password,
                           UserRole role) {

        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(
                name,
                email,
                encodedPassword,
                role
        );

        return repository.save(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    @Transactional
    public void changePassword(
            UUID userId,
            String currentPassword,
            String newPassword
    ) {

        User user = findById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.changePassword(encodedPassword);

        repository.save(user);
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        User user = findById(userId);
        user.deactivate();
        repository.save(user);
    }
}
