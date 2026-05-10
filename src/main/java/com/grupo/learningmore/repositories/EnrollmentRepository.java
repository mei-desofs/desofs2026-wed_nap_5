package com.grupo.learningmore.repositories;

import java.util.UUID;

//TODO: colocar a dependencia depois de ter a classe de dominio Enrollement
public interface EnrollmentRepository
        extends JpaRepository<Enrollment, UUID> {

    boolean existsByUserIdAndCourseId(
            Long userId,
            Long courseId
    );
}