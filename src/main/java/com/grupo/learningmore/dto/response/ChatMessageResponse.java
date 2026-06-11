package com.grupo.learningmore.dto.Response;

import java.util.Date;

public record ChatMessageResponse(
        String id,
        String content,
        Date sentAt
) {
}