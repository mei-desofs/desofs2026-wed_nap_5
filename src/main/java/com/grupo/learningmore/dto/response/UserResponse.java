package com.grupo.learningmore.dto.response;

import com.grupo.learningmore.domain.user.UserRole;

 

public record UserResponse (
    String id,
    String name,
    String email,
    UserRole role,
    boolean active
){

}
