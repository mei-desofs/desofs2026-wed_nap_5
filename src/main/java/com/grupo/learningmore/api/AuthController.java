package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.dto.Request.LoginRequest;
import com.grupo.learningmore.dto.Response.LoginResponse;
import com.grupo.learningmore.security.JwtService;
import com.grupo.learningmore.services.LoginAttemptService;
import com.grupo.learningmore.services.UserService;
import jakarta.validation.Valid;
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
    private final LoginAttemptService loginAttemptService;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        if (loginAttemptService.isBlocked(request.email())) {
            logger.warn("Blocked login attempt due to too many failures for email: {}", request.email());
            return ResponseEntity.status(429).build();
        }

        try {
            User user = userService.findByEmail(request.email());

            if (!user.isActive()) {
                logger.warn("Login attempt for inactive user: {}", request.email());
                loginAttemptService.recordFailedAttempt(request.email());
                return ResponseEntity.status(403).build();
            }

            if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                logger.warn("Failed login attempt for email: {}", request.email());
                loginAttemptService.recordFailedAttempt(request.email());
                return ResponseEntity.status(401).build();
            }

            loginAttemptService.resetAttempts(request.email());

            String token = jwtService.generateToken(
                    user.getId().toString(),
                    user.getRole().name(),
                    user.getTokenVersion()
            );

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
            loginAttemptService.recordFailedAttempt(request.email());
            return ResponseEntity.status(401).build();
        }
    }
}
