package com.grupo.learningmore.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String email) {
        LoginAttempt attempt = attempts.get(normalize(email));

        if (attempt == null) {
            return false;
        }

        if (attempt.blockedUntil() == null) {
            return false;
        }

        if (Instant.now().isAfter(attempt.blockedUntil())) {
            attempts.remove(normalize(email));
            return false;
        }

        return true;
    }

    public void recordFailedAttempt(String email) {
        String key = normalize(email);

        LoginAttempt current = attempts.getOrDefault(
                key,
                new LoginAttempt(0, null)
        );

        int failedAttempts = current.failedAttempts() + 1;
        Instant blockedUntil = failedAttempts >= MAX_FAILED_ATTEMPTS
                ? Instant.now().plus(BLOCK_DURATION_MINUTES, ChronoUnit.MINUTES)
                : null;

        attempts.put(key, new LoginAttempt(failedAttempts, blockedUntil));
    }

    public void resetAttempts(String email) {
        attempts.remove(normalize(email));
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private record LoginAttempt(
            int failedAttempts,
            Instant blockedUntil
    ) {
    }
}
