package com.grupo.learningmore.user.api;

import com.grupo.learningmore.user.application.UserService;
import com.grupo.learningmore.user.domain.User;
import com.grupo.learningmore.user.domain.UserRole;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User create(@RequestBody CreateUserRequest request) {
        return service.createUser(
                request.name(),
                request.email(),
                request.passwordHash(),
                request.role()
        );
    }

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    public record CreateUserRequest(
            String name,
            String email,
            String passwordHash,
            UserRole role
    ) {}
}
