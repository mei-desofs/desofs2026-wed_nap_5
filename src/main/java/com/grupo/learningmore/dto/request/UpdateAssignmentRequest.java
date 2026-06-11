package com.grupo.learningmore.dto.request;

import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record UpdateAssignmentRequest(
        String title,
        String description,
        @Future LocalDateTime deadline
) {
}
