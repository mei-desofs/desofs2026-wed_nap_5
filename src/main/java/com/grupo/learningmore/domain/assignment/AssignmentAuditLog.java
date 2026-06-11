package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;

/**
 * AssignmentAuditLog tracks all changes to Assignment entities.
 * Provides non-repudiation and accountability for academic record integrity (R8, R5).
 */
@Entity
@Getter
@Table(name = "assignment_audit_logs")
public class AssignmentAuditLog {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String assignmentId;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String actorId; // User who performed the action

    @Column(columnDefinition = "TEXT")
    private String oldValues; // JSON representation of previous values

    @Column(columnDefinition = "TEXT")
    private String newValues; // JSON representation of new values

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AssignmentAuditLog() {
    }

    public AssignmentAuditLog(String assignmentId, String action, String actorId, String oldValues, String newValues, LocalDateTime timestamp) {
        this.id = generateSecureId();
        this.assignmentId = assignmentId;
        this.action = action;
        this.actorId = actorId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.timestamp = timestamp;
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "AAL-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
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
