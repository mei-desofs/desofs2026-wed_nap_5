package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SubmissionAuditLog tracks all changes to Submission entities.
 * Provides non-repudiation and accountability for student work integrity (R8, R5).
 */
@Entity
@Getter
@Table(name = "submission_audit_logs")
public class SubmissionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID submissionId;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE (grade/feedback), DELETE

    @Column(nullable = false)
    private UUID actorId; // User who performed the action

    @Column(columnDefinition = "TEXT")
    private String oldValues; // JSON representation of previous values

    @Column(columnDefinition = "TEXT")
    private String newValues; // JSON representation of new values

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public SubmissionAuditLog() {
    }

    public SubmissionAuditLog(UUID submissionId, String action, UUID actorId, String oldValues, String newValues, LocalDateTime timestamp) {
        this.submissionId = submissionId;
        this.action = action;
        this.actorId = actorId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.timestamp = timestamp;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
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
