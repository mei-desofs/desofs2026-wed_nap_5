package com.grupo.learningmore.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAssignmentRequest(
        @NotBlank String title,
        String description,
        @NotNull @Future LocalDateTime deadline
) {
}
