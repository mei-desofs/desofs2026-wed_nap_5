package com.grupo.learningmore.dto.response;

import java.time.LocalDateTime;
 
public record ResourceResponse(
        String id,
        String courseId,
        String filename,
        Long fileSize,
        String contentType,
        LocalDateTime uploadedAt,
        String uploadedBy
) {
}
