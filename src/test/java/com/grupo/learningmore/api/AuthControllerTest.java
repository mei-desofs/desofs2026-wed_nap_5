package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.security.JwtService;
import com.grupo.learningmore.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginSuccessReturnsTokenAndUserData() {
        User user = new User("Bruno", "bruno@test.com", "hashed-password", UserRole.STUDENT);

        when(userService.findByEmail("bruno@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user.getId().toString(), "STUDENT")).thenReturn("jwt-token");

        ResponseEntity<AuthController.LoginResponse> response = authController.login(
                new AuthController.LoginRequest("bruno@test.com", "password123")
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().token());
        assertEquals(user.getId().toString(), response.getBody().userId());
        assertEquals("Bruno", response.getBody().name());
        assertEquals("bruno@test.com", response.getBody().email());
        assertEquals("STUDENT", response.getBody().role());

        verify(jwtService).generateToken(user.getId().toString(), "STUDENT");
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() {
        User user = new User("Bruno", "bruno@test.com", "hashed-password", UserRole.STUDENT);

        when(userService.findByEmail("bruno@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        ResponseEntity<AuthController.LoginResponse> response = authController.login(
                new AuthController.LoginRequest("bruno@test.com", "wrong-password")
        );

        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginWithUnknownEmailReturnsUnauthorized() {
        when(userService.findByEmail("missing@test.com"))
                .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<AuthController.LoginResponse> response = authController.login(
                new AuthController.LoginRequest("missing@test.com", "password123")
        );

        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
}
