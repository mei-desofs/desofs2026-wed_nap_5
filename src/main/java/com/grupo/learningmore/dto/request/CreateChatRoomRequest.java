package com.grupo.learningmore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateChatRoomRequest(
        @NotBlank String name,
        @NotNull String courseId
) {
}
