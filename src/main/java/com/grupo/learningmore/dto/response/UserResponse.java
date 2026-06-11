<<<<<<< HEAD
package com.grupo.learningmore.dto.Response;

import com.grupo.learningmore.domain.user.UserRole;

public record UserResponse (
    String id,
=======
package com.grupo.learningmore.dto.response;

import com.grupo.learningmore.domain.user.UserRole;

import java.util.UUID;

public record UserResponse (
    UUID id,
>>>>>>> 5f0069b7a48e9c16c687ab0867f2eafe4fb237dc
    String name,
    String email,
    UserRole role,
    boolean active
){

}
