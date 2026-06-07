package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.dto.request.LoginRequest;
import com.grupo.learningmore.dto.response.LoginResponse;
import com.grupo.learningmore.security.JwtService;
import com.grupo.learningmore.services.LoginAttemptService;
import com.grupo.learningmore.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(
                userService,
                jwtService,
                passwordEncoder,
                loginAttemptService
        );

        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
    }

    @Test
    void loginSuccessReturnsTokenAndUserData() {
        User user = new User("Bruno", "bruno@test.com", "hashed-password", UserRole.STUDENT);

        when(userService.findByEmail("bruno@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user.getId().toString(), "STUDENT", 0L)).thenReturn("jwt-token");

        ResponseEntity<LoginResponse> response = authController.login(
                new LoginRequest("bruno@test.com", "password123")
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().token());
        assertEquals(user.getId().toString(), response.getBody().userId());
        assertEquals("Bruno", response.getBody().name());
        assertEquals("bruno@test.com", response.getBody().email());
        assertEquals("STUDENT", response.getBody().role());

        verify(loginAttemptService).resetAttempts("bruno@test.com");
        verify(jwtService).generateToken(user.getId().toString(), "STUDENT", 0L);
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() {
        User user = new User("Bruno", "bruno@test.com", "hashed-password", UserRole.STUDENT);

        when(userService.findByEmail("bruno@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        ResponseEntity<LoginResponse> response = authController.login(
                new LoginRequest("bruno@test.com", "wrong-password")
        );

        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(loginAttemptService).recordFailedAttempt("bruno@test.com");
        verify(jwtService, never()).generateToken(anyString(), anyString(), anyLong());
    }

    @Test
    void loginWithUnknownEmailReturnsUnauthorized() {
        when(userService.findByEmail("missing@test.com"))
                .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<LoginResponse> response = authController.login(
                new LoginRequest("missing@test.com", "password123")
        );

        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(loginAttemptService).recordFailedAttempt("missing@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString(), anyLong());
    }

    @Test
    void blockedLoginReturnsTooManyRequests() {
        when(loginAttemptService.isBlocked("blocked@test.com")).thenReturn(true);

        ResponseEntity<LoginResponse> response = authController.login(
                new LoginRequest("blocked@test.com", "password123")
        );

        assertEquals(429, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(userService, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString(), anyLong());
    }
}
