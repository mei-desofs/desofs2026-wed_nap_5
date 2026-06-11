package com.grupo.learningmore.dto.response;

import com.grupo.learningmore.domain.assignment.SubmissionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        UUID assignmentId,
        UUID userId,
        LocalDateTime submittedAt,
        SubmissionStatus status,
        BigDecimal grade,
        String feedback,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer version
) {
}
