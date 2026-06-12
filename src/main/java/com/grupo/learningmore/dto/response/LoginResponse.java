package com.grupo.learningmore.dto.Response;

public record LoginResponse(
        String token,
        String userId,
        String name,
        String email,
        String role
) {
}
