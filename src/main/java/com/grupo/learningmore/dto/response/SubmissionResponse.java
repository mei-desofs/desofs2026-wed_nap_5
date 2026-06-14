package com.grupo.learningmore.dto.response;

import com.grupo.learningmore.domain.assignment.SubmissionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
 

public record SubmissionResponse(
        String id,
        String assignmentId,
        String userId,
        LocalDateTime submittedAt,
        SubmissionStatus status,
        BigDecimal grade,
        String feedback,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer version
) {
}
