package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.repositories.UserRepository;
import com.grupo.learningmore.domain.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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

        log.info("User creation attempt: email={} role={}", email, role);

        if (role == null) {
            log.warn("User creation failed - role is null for email={}", email);
            throw new IllegalArgumentException("Role is required");
        }

        if (repository.existsByEmail(email)) {
            log.warn("User creation failed - email already exists: {}", email);
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(
                name,
                email,
                encodedPassword,
                role
        );

        User saved = repository.save(user);

        log.info("User created successfully: userId={} email={} role={}",
                saved.getId(), email, role);

        return saved;
    }

    public List<User> findAll() {

        log.info("Fetching all users");

        return repository.findAll();
    }

    public User findById(UUID id) {

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", id);
                    return new IllegalArgumentException("User not found");
                });
    }

    public User findByEmail(String email) {

        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found by email: {}", email);
                    return new IllegalArgumentException("User not found");
                });
    }

    @Transactional
    public void changePassword(
            UUID userId,
            String currentPassword,
            String newPassword
    ) {

        log.info("Password change attempt: user={}", userId);

        User user = findById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            log.warn("Password change failed - invalid current password: user={}", userId);
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.changePassword(encodedPassword);

        repository.save(user);

        log.info("Password changed successfully: user={}", userId);
    }

    @Transactional
    public void deactivateUser(UUID userId) {

        log.warn("User deactivation requested: user={}", userId);

        User user = findById(userId);

        user.deactivate();

        repository.save(user);

        log.info("User deactivated successfully: user={}", userId);
    }
}