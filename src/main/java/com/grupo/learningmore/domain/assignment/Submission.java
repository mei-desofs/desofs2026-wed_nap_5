package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long userId;

    private LocalDateTime submittedAt;

    private SubmissionStatus status;

    private String filePath;

    private BigDecimal grade;

    private String feedback;

    public Submission() {
    }

    public Submission(UUID id, Long userId, LocalDateTime submittedAt, SubmissionStatus status, String filePath, BigDecimal grade, String feedback) {
        this.id = id;
        this.userId = userId;
        this.submittedAt = submittedAt;
        this.status = status;
        this.filePath = filePath;
        this.grade = grade;
        this.feedback = feedback;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
