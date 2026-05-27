package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.security.JwtService;
import com.grupo.learningmore.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.findByEmail(request.email());

            if (!user.isActive()) {
                logger.warn("Login attempt for inactive user: {}", request.email());
                return ResponseEntity.status(403).build();
            }

            if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                logger.warn("Failed login attempt for email: {}", request.email());
                return ResponseEntity.status(401).build();
            }

            String token = jwtService.generateToken(user.getId().toString(), user.getRole().name());

            logger.info("Successful login for user: {}", user.getEmail());

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    user.getId().toString(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed login attempt for unknown email: {}", request.email());
            return ResponseEntity.status(401).build();
        }
    }

    public record LoginRequest(
            @Email
            @NotBlank
            String email,

            @NotBlank
            String password
    ) {
    }

    public record LoginResponse(
            String token,
            String userId,
            String name,
            String email,
            String role
    ) {
    }
}
