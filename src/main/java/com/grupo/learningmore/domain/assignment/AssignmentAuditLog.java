package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.hibernate.annotations.JdbcTypeCode;
 
import java.sql.Types;
 

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
    @JdbcTypeCode(Types.VARCHAR)
    private String id;

    @Column(nullable = false)
    private String assignmentId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String actorId;

    @Column(columnDefinition = "TEXT")
    private String oldValues;

    @Column(columnDefinition = "TEXT")
    private String newValues;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AssignmentAuditLog() {
    }

    public AssignmentAuditLog(String assignmentId, String action, String actorId, String oldValues, String newValues, LocalDateTime timestamp) {
        this.assignmentId = assignmentId;
        this.action = action;
        this.actorId = actorId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.timestamp = timestamp;
    }


    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateSecureId();
        }
    }


    private String generateSecureId() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return "ASN-" + HexFormat.of().formatHex(bytes).toUpperCase(); 
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
