package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;

/**
 * Submission entity within the Assignment aggregate.
 * Represents student work submission with integrity controls for grading and audit trails (R4, R5, R8).
 */
@Entity
@Getter
@Table(name = "submissions")
public class Submission {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "assignment_id", insertable = false, updatable = false)
    private String assignmentId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "grade", precision = 5, scale = 2)
    private BigDecimal grade;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    public Submission() {
    }

    public Submission(Assignment assignment,
                      String userId,
                      LocalDateTime submittedAt,
                      SubmissionStatus status,
                      String filePath) {

        this.id = generateSecureId();
        this.assignment = assignment;
        this.userId = userId;
        this.submittedAt = submittedAt;
        this.status = status;
        this.filePath = filePath;
        this.version = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "SUB-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    public Submission(String id,
                      Assignment assignment,
                      String userId,
                      LocalDateTime submittedAt,
                      SubmissionStatus status,
                      String filePath,
                      BigDecimal grade,
                      String feedback) {

        this.id = id;
        this.assignment = assignment;
        this.userId = userId;
        this.submittedAt = submittedAt;
        this.status = status;
        this.filePath = filePath;
        this.grade = grade;
        this.feedback = feedback;
        this.version = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
        this.updatedAt = LocalDateTime.now();
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
        this.updatedAt = LocalDateTime.now();
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void grade(BigDecimal grade,
                      String feedback,
                      String graderId) {

        if (this.status == SubmissionStatus.GRADED) {
            throw new IllegalStateException("Submission already graded");
        }

        this.grade = grade;
        this.feedback = feedback;
        this.status = SubmissionStatus.GRADED;
        this.lastModifiedBy = graderId;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isLate(LocalDateTime deadline) {
        return this.submittedAt.isAfter(deadline);
    }

    public boolean isGraded() {
        return this.status == SubmissionStatus.GRADED;
    }

    public boolean isPending() {
        return this.status == SubmissionStatus.PENDING;
    }
}
