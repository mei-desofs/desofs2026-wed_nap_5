package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Submission entity within the Assignment aggregate.
 * Represents student work submission with integrity controls for grading and audit trails (R4, R5, R8).
 */
@Entity
@Getter
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID assignmentId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

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
    private Long lastModifiedBy;

    public Submission() {
    }

    public Submission(UUID assignmentId, Long userId, LocalDateTime submittedAt, SubmissionStatus status, String filePath) {
        this.assignmentId = assignmentId;
        this.userId = userId;
        this.submittedAt = submittedAt;
        this.status = status;
        this.filePath = filePath;
        this.version = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Submission(UUID id, Long userId, LocalDateTime submittedAt, SubmissionStatus status, String filePath, BigDecimal grade, String feedback) {
        this.id = id;
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setUserId(Long userId) {
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

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    // ============ Business logic methods ============

    /**
     * Grade this submission.
     * Performs immutable audit trail tracking for academic integrity (R4, R5).
     * @param grade the grade (0-100)
     * @param feedback optional feedback message
     * @param graderId the user ID of the professor grading
     * @throws IllegalStateException if submission is already graded
     */
    public void grade(BigDecimal grade, String feedback, Long graderId) {
        if (this.status == SubmissionStatus.GRADED) {
            throw new IllegalStateException("Submission already graded");
        }
        this.grade = grade;
        this.feedback = feedback;
        this.status = SubmissionStatus.GRADED;
        this.lastModifiedBy = graderId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if this submission is late.
     * @param deadline the assignment deadline
     * @return true if submitted after deadline
     */
    public boolean isLate(LocalDateTime deadline) {
        return this.submittedAt.isAfter(deadline);
    }

    /**
     * Check if this submission has been graded.
     * @return true if status is GRADED
     */
    public boolean isGraded() {
        return this.status == SubmissionStatus.GRADED;
    }

    /**
     * Check if this submission has pending grade.
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == SubmissionStatus.PENDING;
    }
}
