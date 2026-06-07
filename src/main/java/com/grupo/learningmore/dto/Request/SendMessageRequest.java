package com.grupo.learningmore.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(

        @NotBlank(message = "Message cannot be empty")
        @Size(max = 1000, message = "Message too long")
        String content

) {
}