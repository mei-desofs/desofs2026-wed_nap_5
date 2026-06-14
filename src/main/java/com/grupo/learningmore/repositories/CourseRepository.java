package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
 

public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByCode(String code);

    boolean existsByCode(String code);
}
