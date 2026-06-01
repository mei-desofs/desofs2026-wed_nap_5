package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUserSuccess() {
        when(repository.existsByEmail("student@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(
                "Student",
                "student@test.com",
                "password123",
                UserRole.STUDENT
        );

        assertNotNull(result);
        assertEquals("Student", result.getName());
        assertEquals("student@test.com", result.getEmail());
        assertEquals("encoded-password", result.getPasswordHash());
        assertEquals(UserRole.STUDENT, result.getRole());
        assertTrue(result.isActive());
        assertEquals(0L, result.getTokenVersion());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        assertEquals("encoded-password", captor.getValue().getPasswordHash());
    }

    @Test
    public void testCreateUserDuplicateEmailThrowsException() {
        when(repository.existsByEmail("student@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(
                        "Student",
                        "student@test.com",
                        "password123",
                        UserRole.STUDENT
                )
        );

        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    public void testCreateUserWithNullRoleThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(
                        "Student",
                        "student@test.com",
                        "password123",
                        null
                )
        );

        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    public void testFindAllReturnsUsers() {
        User user = new User("Admin", "admin@test.com", "hash", UserRole.ADMIN);
        when(repository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("admin@test.com", result.get(0).getEmail());
    }

    @Test
    public void testFindByIdSuccess() {
        UUID id = UUID.randomUUID();
        User user = new User("User", "user@test.com", "hash", UserRole.STUDENT);
        when(repository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.findById(id);

        assertEquals("user@test.com", result.getEmail());
    }

    @Test
    public void testFindByIdNotFoundThrowsException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findById(id));
    }

    @Test
    public void testFindByEmailSuccess() {
        User user = new User("User", "user@test.com", "hash", UserRole.STUDENT);
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("user@test.com");

        assertEquals("user@test.com", result.getEmail());
    }

    @Test
    public void testFindByEmailNotFoundThrowsException() {
        when(repository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userService.findByEmail("missing@test.com")
        );
    }

    @Test
    public void testChangePasswordSuccessUpdatesHashAndTokenVersion() {
        UUID userId = UUID.randomUUID();
        User user = new User("User", "user@test.com", "old-hash", UserRole.STUDENT);

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword123", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("new-hash");

        userService.changePassword(userId, "oldPassword123", "newPassword123");

        assertEquals("new-hash", user.getPasswordHash());
        assertEquals(1L, user.getTokenVersion());

        verify(repository).save(user);
    }

    @Test
    public void testChangePasswordWithWrongCurrentPasswordThrowsException() {
        UUID userId = UUID.randomUUID();
        User user = new User("User", "user@test.com", "old-hash", UserRole.STUDENT);

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "old-hash")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword(userId, "wrongPassword", "newPassword123")
        );

        assertEquals("old-hash", user.getPasswordHash());
        assertEquals(0L, user.getTokenVersion());

        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).save(any());
    }
}
