package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.assignment.SubmissionAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionAuditLogRepository extends JpaRepository<SubmissionAuditLog, UUID> {
    
    /**
     * Find all audit logs for a specific submission.
     * @param submissionId the submission ID
     * @return list of audit logs
     */
    List<SubmissionAuditLog> findBySubmissionId(UUID submissionId);
    
    /**
     * Find audit logs for a specific submission with pagination.
     * @param submissionId the submission ID
     * @param pageable pagination info
     * @return paginated list of audit logs
     */
    Page<SubmissionAuditLog> findBySubmissionId(UUID submissionId, Pageable pageable);
    
    /**
     * Find all audit logs by a specific actor (user).
     * @param actorId the user ID
     * @return list of audit logs
     */
    List<SubmissionAuditLog> findByActorId(UUID actorId);
}
