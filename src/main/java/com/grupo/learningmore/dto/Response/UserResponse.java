package com.grupo.learningmore.dto.Response;

import com.grupo.learningmore.domain.user.UserRole;

import java.util.UUID;

public record UserResponse (
    UUID id,
    String name,
    String email,
    UserRole role,
    boolean active
){

}
