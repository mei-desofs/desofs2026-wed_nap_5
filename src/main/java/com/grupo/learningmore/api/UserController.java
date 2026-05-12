package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.services.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User create(@Valid @RequestBody CreateUserRequest request) {
        return service.createUser(
                request.name(),
                request.email(),
                request.password(),
                request.role()
        );
    }

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
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
}
