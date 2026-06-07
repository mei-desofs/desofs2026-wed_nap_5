package com.grupo.learningmore.dto.Response;

import java.util.Date;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        String content,
        Date sentAt
) {
}