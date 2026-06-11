package com.grupo.learningmore.dto.response;

import java.time.LocalDateTime;

public record CourseResponse(
        String id,
        String code,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy
) {
}
