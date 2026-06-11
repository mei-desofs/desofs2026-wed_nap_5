package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.assignment.Submission;
import com.grupo.learningmore.domain.assignment.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, String> {

    /**
     * Find a submission by assignment and user.
     *
     * @param assignmentId the assignment ID
     * @param userId       the user ID
     * @return optional containing the submission if exists
     */
    Optional<Submission> findByAssignmentIdAndUserId(String assignmentId, String userId);

    /**
     * Find all submissions for a specific assignment (with pagination).
     *
     * @param assignmentId the assignment ID
     * @param pageable     pagination info
     * @return paginated list of submissions
     */
    Page<Submission> findByAssignmentId(String assignmentId, Pageable pageable);

    /**
     * Find all submissions for a specific assignment without pagination.
     *
     * @param assignmentId the assignment ID
     * @return list of all submissions for the assignment
     */
    List<Submission> findByAssignmentId(String assignmentId);

    /**
     * Check if a submission exists for a given assignment and user.
     *
     * @param assignmentId the assignment ID
     * @param userId       the user ID
     * @return true if submission exists
     */
    boolean existsByAssignmentIdAndUserId(String assignmentId, String userId);

    /**
     * Find all submissions by a specific user (across all assignments).
     *
     * @param userId   the user ID
     * @param pageable pagination info
     * @return paginated list of user's submissions
     */
    Page<Submission> findByUserId(String userId, Pageable pageable);

    /**
     * Find all submissions by a specific user for a specific assignment list.
     * Useful for getting all submissions in a course.
     *
     * @param userId        the user ID
     * @param assignmentIds list of assignment IDs
     * @return list of submissions
     */
    @Query("SELECT s FROM Submission s WHERE s.userId = :userId AND s.assignment.id IN :assignmentIds")
    List<Submission> findByUserIdAndAssignmentIdIn(@Param("userId") String userId, @Param("assignmentIds") List<String> assignmentIds);

    /**
     * Find all submissions with a specific status.
     *
     * @param status the submission status
     * @return list of submissions with the status
     */
    List<Submission> findByStatus(SubmissionStatus status);

    /**
     * Find all late submissions (submitted after deadline).
     *
     * @param assignmentId the assignment ID
     * @param deadline     the deadline timestamp
     * @return list of late submissions
     */
    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.submittedAt > :deadline")
    List<Submission> findLateSubmissions(@Param("assignmentId") String assignmentId, @Param("deadline") LocalDateTime deadline);

    /**
     * Count submissions by status for an assignment.
     *
     * @param assignmentId the assignment ID
     * @param status       the submission status
     * @return count of submissions with the status
     */
    long countByAssignmentIdAndStatus(String assignmentId, SubmissionStatus status);

    /**
     * Find submissions that need grading (status = PENDING).
     *
     * @param assignmentId the assignment ID
     * @param pageable     pagination info
     * @return paginated list of pending submissions
     */
    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.status = com.grupo.learningmore.domain.assignment.SubmissionStatus.PENDING")
    Page<Submission> findPendingSubmissions(@Param("assignmentId") String assignmentId, Pageable pageable);
}
