package com.grupo.learningmore.dto.response;

import java.util.Date;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        String content,
        Date sentAt
) {
}