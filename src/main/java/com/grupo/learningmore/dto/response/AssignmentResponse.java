package com.grupo.learningmore.dto.response;

import java.time.LocalDateTime;
 

public record AssignmentResponse(
        String id,
        String title,
        String description,
        LocalDateTime deadline,
        String courseId,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer version,
        int submissionCount
) {
}
