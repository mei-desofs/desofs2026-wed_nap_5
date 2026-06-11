package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.dto.request.ChangePasswordRequest;
import com.grupo.learningmore.dto.request.CreateUserRequest;
import com.grupo.learningmore.dto.response.UserResponse;
import com.grupo.learningmore.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {

        log.info("POST /api/users - Creating user with email {}", request.email());

        User user = service.createUser(
                request.name(),
                request.email(),
                request.password(),
                request.role()
        );

        log.info("User created successfully with id {}", user.getId());

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {

        log.info("Authentication name: {}", authentication.getName());

        UUID userId = UUID.fromString(authentication.getName());

        log.info("GET /api/users/me - Fetching user profile for {}", userId);

        User user = service.findById(userId);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }

    @PutMapping("/me/password")
    public void changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        UUID userId = UUID.fromString(authentication.getName());

        log.info("PUT /api/users/me/password - Password change request for user {}", userId);

        service.changePassword(
                userId,
                request.currentPassword(),
                request.newPassword()
        );

        log.info("Password changed successfully for user {}", userId);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(@PathVariable UUID id) {

        log.warn("PUT /api/users/{}/deactivate - User deactivation requested", id);

        service.deactivateUser(id);

        log.info("User {} deactivated successfully", id);
    }

    @GetMapping
    public List<UserResponse> findAll() {

        log.info("GET /api/users - Fetching all users");

        List<UserResponse> users = service.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.isActive()
                ))
                .toList();

        log.info("Fetched {} users", users.size());

        return users;
    }
}