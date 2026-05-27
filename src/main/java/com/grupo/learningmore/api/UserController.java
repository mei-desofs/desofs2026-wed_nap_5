package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
            @Size(min = 8, max = 64)
            String password

    ) {
    }

    public record ChangePasswordRequest(

            @NotBlank
            String currentPassword,

            @NotBlank
            @Size(min = 8, max = 64)
            String newPassword

    ) {
    }

    public record UserResponse(
            UUID id,
            String name,
            String email,
            UserRole role,
            boolean active
    ) {
    }
}
