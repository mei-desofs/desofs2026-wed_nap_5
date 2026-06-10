package com.grupo.learningmore.dto.response;

import java.util.UUID;

public record ChatRoomResponse(
        UUID id,
        String name,
        UUID courseId
) {
}