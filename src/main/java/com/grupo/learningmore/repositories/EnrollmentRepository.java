package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, UUID> {

    boolean existsByUserIdAndCourseId(
            UUID userId,
            UUID courseId
    );

    List<Enrollment> findByUserIdAndActiveTrue(UUID userId);

    boolean existsByUserIdAndCourseIdAndActiveTrue(UUID userId, UUID courseId);
}