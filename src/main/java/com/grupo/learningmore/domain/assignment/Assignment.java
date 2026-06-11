package com.grupo.learningmore.domain.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;

/**
 * Assignment aggregate root.
 * Manages the definition and lifecycle of assignment tasks within a course.
 * Includes submission collection management and audit trail for academic integrity (R4, R5, R8).
 */
@Entity
@Getter
@Table(name = "assignments")
public class Assignment {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String createdBy;

    @Version
    @Column(nullable = false)
    private Integer version;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "assignment_id")
    private List<Submission> submissions = new ArrayList<>();

    public Assignment() {
    }

    public Assignment(String title, String description, LocalDateTime deadline, String courseId, String createdBy) {
        this.id = generateSecureId();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.courseId = courseId;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0;
        this.submissions = new ArrayList<>();
    }

    public Assignment(String id, String title, String description, LocalDateTime deadline, String courseId, String createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.courseId = courseId;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0;
        this.submissions = new ArrayList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "ASN-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    // ============ Business logic methods ============

    /**
     * Check if the assignment deadline has expired.
     * Used to prevent late submissions and validate submission eligibility.
     *
     * @return true if deadline has passed
     */
    public boolean isDeadlineExpired() {
        return LocalDateTime.now().isAfter(this.deadline);
    }

    /**
     * Check if assignment can accept submissions.
     * Performs server-side validation to prevent deadline tampering (AC10, R8).
     *
     * @return true if submissions are still accepted
     */
    public boolean canBeSubmitted() {
        return !isDeadlineExpired();
    }

    /**
     * Add a submission to this assignment.
     *
     * @param submission the submission to add
     */
    public void addSubmission(Submission submission) {
        if (submission != null) {
            this.submissions.add(submission);
        }
    }

    /**
     * Remove a submission from this assignment.
     *
     * @param submission the submission to remove
     */
    public void removeSubmission(Submission submission) {
        this.submissions.remove(submission);
    }

    /**
     * Find a submission by its ID.
     *
     * @param submissionId the submission ID
     * @return the submission or null if not found
     */
    public Submission findSubmissionById(UUID submissionId) {
        return this.submissions.stream()
                .filter(submission -> submission.getId().equals(submissionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find a submission by user ID.
     * Returns the first (and should be only) submission from a specific user.
     *
     * @param userId the user ID
     * @return the submission or null if not found
     */
    public Submission findSubmissionByUserId(UUID userId) {
        return this.submissions.stream()
                .filter(submission -> submission.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
