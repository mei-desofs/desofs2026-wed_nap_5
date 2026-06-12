package com.grupo.learningmore.dto.Response;

import java.util.UUID;

public record ChatRoomResponse(
        String id,
        String name,
        String courseId
) {
}