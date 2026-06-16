package com.grupo.learningmore.dto.request;

import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.security.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank
        String name,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        @ValidPassword
        String password,

        @NotNull
        UserRole role
) {
}
