package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

 
import java.util.List;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, String> {

    boolean existsByUserIdAndCourseId(
            String userId,
            String courseId
    );

    List<Enrollment> findByUserIdAndActiveTrue(String userId);

    boolean existsByUserIdAndCourseIdAndActiveTrue(String userId, String courseId);
}