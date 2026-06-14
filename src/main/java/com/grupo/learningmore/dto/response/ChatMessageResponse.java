package com.grupo.learningmore.dto.response;

import java.util.Date;
 

public record ChatMessageResponse(
        String id,
        String content,
        Date sentAt
) {
}