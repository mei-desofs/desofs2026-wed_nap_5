package com.grupo.learningmore.dto.Response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResourceResponse(
        UUID id,
        UUID courseId,
        String filename,
        Long fileSize,
        String contentType,
        LocalDateTime uploadedAt,
        UUID uploadedBy
) {
}
