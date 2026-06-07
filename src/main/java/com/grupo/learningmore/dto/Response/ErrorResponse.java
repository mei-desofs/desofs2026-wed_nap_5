package com.grupo.learningmore.dto.Response;

import java.time.LocalDateTime;

/**
 * Standard error response DTO for REST API errors.
 * Provides consistent error responses without exposing sensitive information.
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
}