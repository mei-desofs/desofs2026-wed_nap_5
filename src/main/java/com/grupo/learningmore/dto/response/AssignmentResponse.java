package com.grupo.learningmore.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime deadline,
        UUID courseId,
        UUID createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer version,
        int submissionCount
) {
}
