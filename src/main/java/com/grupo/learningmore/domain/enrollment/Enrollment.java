package com.grupo.learningmore.domain.enrollment;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollments")
@Getter
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID courseId;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    @Column(nullable = false)
    private boolean active;

    public Enrollment() {
    }

    public Enrollment(UUID userId, UUID courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = LocalDateTime.now();
        this.active = true;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
