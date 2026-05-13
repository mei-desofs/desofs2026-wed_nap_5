package com.grupo.learningmore.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(
        @NotBlank(message = "Course code cannot be blank")
        @Size(min = 1, max = 20, message = "Course code must be between 1 and 20 characters")
        String code,

        @NotBlank(message = "Course name cannot be blank")
        @Size(min = 1, max = 255, message = "Course name must be between 1 and 255 characters")
        String name,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description
) {
}
