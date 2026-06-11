package com.grupo.learningmore.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String email) {

        String key = normalize(email);

        LoginAttempt attempt = attempts.get(key);

        if (attempt == null) {
            return false;
        }

        if (attempt.blockedUntil() == null) {
            return false;
        }

        if (Instant.now().isAfter(attempt.blockedUntil())) {
            log.info("Login block expired for user {}", key);
            attempts.remove(key);
            return false;
        }

        log.warn("Login attempt blocked for user {} (blocked until {})",
                key, attempt.blockedUntil());

        return true;
    }

    public void recordFailedAttempt(String email) {

        String key = normalize(email);

        LoginAttempt current = attempts.getOrDefault(
                key,
                new LoginAttempt(0, null)
        );

        int failedAttempts = current.failedAttempts() + 1;

        Instant blockedUntil = null;

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            blockedUntil = Instant.now().plus(BLOCK_DURATION_MINUTES, ChronoUnit.MINUTES);

            log.warn("User {} has been blocked due to {} failed login attempts",
                    key, failedAttempts);
        }

        attempts.put(key, new LoginAttempt(failedAttempts, blockedUntil));

        log.debug("Failed login attempt recorded for user {} (count={})",
                key, failedAttempts);
    }

    public void resetAttempts(String email) {

        String key = normalize(email);

        if (attempts.remove(key) != null) {
            log.info("Login attempts reset for user {}", key);
        }
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