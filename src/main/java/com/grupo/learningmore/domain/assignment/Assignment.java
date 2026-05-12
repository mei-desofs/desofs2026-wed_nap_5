package com.grupo.learningmore.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private LocalDateTime deadline;

    private Long courseId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assignment_id")
    private List<Submission> submissions = new ArrayList<>();

    public Assignment() {
    }

    public Assignment(UUID id, String title, String description, LocalDateTime deadline, Long courseId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.courseId = courseId;
        this.submissions = new ArrayList<>();
    }

    public void setId(UUID id) {
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
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    // Business logic methods
    public void addSubmission(Submission submission) {
        if (submission != null) {
            this.submissions.add(submission);
        }
    }

    public void removeSubmission(Submission submission) {
        this.submissions.remove(submission);
    }

    public Submission findSubmissionById(UUID submissionId) {
        return this.submissions.stream()
                .filter(submission -> submission.getId().equals(submissionId))
                .findFirst()
                .orElse(null);
    }

    public Submission findSubmissionByUserId(Long userId) {
        return this.submissions.stream()
                .filter(submission -> submission.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
