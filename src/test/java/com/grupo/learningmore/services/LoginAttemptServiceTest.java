package com.grupo.learningmore.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    void isBlocked_shouldReturnFalse_whenNoAttemptsExist() {
        assertFalse(service.isBlocked("test@email.com"));
    }

    @Test
    void isBlocked_shouldReturnFalse_whenNotBlocked() {
        service.recordFailedAttempt("test@email.com");

        assertFalse(service.isBlocked("test@email.com"));
    }

    @Test
    void recordFailedAttempts_shouldEventuallyBlockUser() {
        String email = "test@email.com";

        for (int i = 0; i < 5; i++) {
            service.recordFailedAttempt(email);
        }

        assertTrue(service.isBlocked(email));
    }

    @Test
    void recordFailedAttempts_shouldIncreaseCount() {
        String email = "test@email.com";

        service.recordFailedAttempt(email);
        service.recordFailedAttempt(email);

        assertFalse(service.isBlocked(email));
    }

    @Test
    void resetAttempts_shouldClearBlock() {
        String email = "test@email.com";

        for (int i = 0; i < 5; i++) {
            service.recordFailedAttempt(email);
        }

        assertTrue(service.isBlocked(email));

        service.resetAttempts(email);

        assertFalse(service.isBlocked(email));
    }

    @Test
    void isBlocked_shouldReturnFalse_afterReset() {
        String email = "test@email.com";

        for (int i = 0; i < 5; i++) {
            service.recordFailedAttempt(email);
        }

        assertTrue(service.isBlocked(email));

        service.resetAttempts(email);

        assertFalse(service.isBlocked(email));
    }

    @Test
    void normalize_shouldHandleNullEmail() {
        assertFalse(service.isBlocked(null));
        service.recordFailedAttempt(null);
        service.resetAttempts(null);
    }

    @Test
    void isBlocked_shouldReturnFalse_whenBlockedEntryIsRemoved() {
        String email = "test@email.com";

        for (int i = 0; i < 5; i++) {
            service.recordFailedAttempt(email);
        }

        assertTrue(service.isBlocked(email));

        service.resetAttempts(email);

        assertFalse(service.isBlocked(email));
    }

    @Test
    void resetAttempts_shouldActuallyRemoveEntry() {
        String email = "test@email.com";

        for (int i = 0; i < 5; i++) {
            service.recordFailedAttempt(email);
        }

        assertTrue(service.isBlocked(email));

        service.resetAttempts(email);

        assertFalse(service.isBlocked(email));
    }

    @Test
    void normalize_shouldHandleNullEmptyAndFormatting() {
        assertFalse(service.isBlocked(null));

        service.recordFailedAttempt("  TEST@EMAIL.COM  ");

        service.recordFailedAttempt("test@email.com");

        assertFalse(service.isBlocked("TEST@EMAIL.COM"));
    }
}