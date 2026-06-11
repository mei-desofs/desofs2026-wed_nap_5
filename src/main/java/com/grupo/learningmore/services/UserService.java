package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.repositories.UserRepository;
import com.grupo.learningmore.domain.user.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public User findById(String id) {

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
            String userId,
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
    public void deactivateUser(String userId) {

        log.warn("User deactivation requested: user={}", userId);

        User user = findById(userId);

        user.deactivate();

        repository.save(user);

        log.info("User deactivated successfully: user={}", userId);
    }
}