package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.services.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

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
                request.role()
        );

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
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

    public record CreateUserRequest(

            @NotBlank
            String name,

            @Email
            @NotBlank
            String email,

            @NotBlank
            String password,

            UserRole role
    ) {}

    public record UserResponse(
            UUID id,
            String name,
            String email,
            UserRole role,
            boolean active
    ) {}
}
