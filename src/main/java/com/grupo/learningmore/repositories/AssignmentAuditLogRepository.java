package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.assignment.AssignmentAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssignmentAuditLogRepository extends JpaRepository<AssignmentAuditLog, UUID> {

    /**
     * Find all audit logs for a specific assignment.
     *
     * @param assignmentId the assignment ID
     * @return list of audit logs
     */
    List<AssignmentAuditLog> findByAssignmentId(UUID assignmentId);

    /**
     * Find audit logs for a specific assignment with pagination.
     *
     * @param assignmentId the assignment ID
     * @param pageable     pagination info
     * @return paginated list of audit logs
     */
    Page<AssignmentAuditLog> findByAssignmentId(UUID assignmentId, Pageable pageable);

    /**
     * Find all audit logs by a specific actor (user).
     *
     * @param actorId the user ID
     * @return list of audit logs
     */
    List<AssignmentAuditLog> findByActorId(UUID actorId);
}
