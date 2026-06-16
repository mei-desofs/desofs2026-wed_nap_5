package com.grupo.learningmore.dto.request;

import com.grupo.learningmore.security.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 64)
        @ValidPassword
        String newPassword
) {
}
