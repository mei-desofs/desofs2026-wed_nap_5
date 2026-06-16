package com.grupo.learningmore.security;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final List<String> CONTEXT_WORDS = Arrays.asList(
            "learningmore",
            "isep",
            "desofs",
            "password",
            "qwerty",
            "123456"
    );

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return true; // Handled by @NotBlank
        }

        String lowerPassword = password.toLowerCase();

        for (String word : CONTEXT_WORDS) {
            if (lowerPassword.contains(word)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Password cannot contain common or context-specific words like: " + word)
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
