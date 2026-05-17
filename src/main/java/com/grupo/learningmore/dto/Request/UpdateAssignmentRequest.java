package com.grupo.learningmore.dto.Request;

import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record UpdateAssignmentRequest(
        String title,
        String description,
        @Future LocalDateTime deadline
) {
}
