package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AssignmentAuditLog tracks all changes to Assignment entities.
 * Provides non-repudiation and accountability for academic record integrity (R8, R5).
 */
@Entity
@Getter
@Table(name = "assignment_audit_logs")
public class AssignmentAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID assignmentId;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private UUID actorId; // User who performed the action

    @Column(columnDefinition = "TEXT")
    private String oldValues; // JSON representation of previous values

    @Column(columnDefinition = "TEXT")
    private String newValues; // JSON representation of new values

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AssignmentAuditLog() {
    }

    public AssignmentAuditLog(UUID assignmentId, String action, UUID actorId, String oldValues, String newValues, LocalDateTime timestamp) {
        this.assignmentId = assignmentId;
        this.action = action;
        this.actorId = actorId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.timestamp = timestamp;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
