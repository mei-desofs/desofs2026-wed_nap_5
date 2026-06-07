package com.grupo.learningmore.dto.response;

public record LoginResponse(
        String token,
        String userId,
        String name,
        String email,
        String role
) {
}
