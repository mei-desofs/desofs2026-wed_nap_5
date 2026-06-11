package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    /**
     * Find all assignments for a specific course.
     *
     * @param courseId the course ID
     * @return list of assignments in the course
     */
    List<Assignment> findByCourseId(String courseId);

    /**
     * Find an assignment by ID with ownership validation.
     * Used to ensure the requester is the course owner.
     *
     * @param id       the assignment ID
     * @param courseId the course ID
     * @return optional containing the assignment if it exists in the given course
     */
    @Query("SELECT a FROM Assignment a WHERE a.id = :id AND a.courseId = :courseId")
    Optional<Assignment> findByIdAndCourseId(@Param("id") String id, @Param("courseId") String courseId);

    /**
     * Check if an assignment exists in a specific course.
     *
     * @param assignmentId the assignment ID
     * @param courseId     the course ID
     * @return true if assignment exists in the course
     */
    boolean existsByIdAndCourseId(String assignmentId, String courseId);

    /**
     * Find assignments created by a specific professor.
     *
     * @param createdBy the String of the professor (user ID)
     * @return list of assignments created by the professor
     */
    List<Assignment> findByCreatedBy(String createdBy);
}
