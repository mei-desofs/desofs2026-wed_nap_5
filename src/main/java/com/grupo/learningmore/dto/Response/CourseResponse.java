package com.grupo.learningmore.dto.Response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String code,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID createdBy
) {
}
