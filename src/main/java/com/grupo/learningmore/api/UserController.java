package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.dto.Request.ChangePasswordRequest;
import com.grupo.learningmore.dto.Request.CreateUserRequest;
import com.grupo.learningmore.dto.Response.UserResponse;
import com.grupo.learningmore.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {

        User user = service.createUser(
                request.name(),
                request.email(),
                request.password(),
                UserRole.STUDENT
        );

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
        User user = service.findById(UUID.fromString(authentication.getName()));

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

        service.changePassword(
                UUID.fromString(authentication.getName()),
                request.currentPassword(),
                request.newPassword()
        );
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(@PathVariable UUID id) {
        service.deactivateUser(id);
    }

    @GetMapping
    public List<UserResponse> findAll() {

        return service.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.isActive()
                ))
                .toList();
    }

}
