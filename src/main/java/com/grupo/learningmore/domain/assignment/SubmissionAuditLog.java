package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;

/**
 * SubmissionAuditLog tracks all changes to Submission entities.
 * Provides non-repudiation and accountability for student work integrity (R8, R5).
 */
@Entity
@Getter
@Table(name = "submission_audit_logs")
public class SubmissionAuditLog {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String submissionId;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE (grade/feedback), DELETE

    @Column(nullable = false)
    private String actorId; // User who performed the action

    @Column(columnDefinition = "TEXT")
    private String oldValues; // JSON representation of previous values

    @Column(columnDefinition = "TEXT")
    private String newValues; // JSON representation of new values

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public SubmissionAuditLog() {
    }

    public SubmissionAuditLog(String submissionId, String action, String actorId, String oldValues, String newValues, LocalDateTime timestamp) {
        this.id = generateSecureId();
        this.submissionId = submissionId;
        this.action = action;
        this.actorId = actorId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.timestamp = timestamp;
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "SAL-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setActorId(String actorId) {
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
