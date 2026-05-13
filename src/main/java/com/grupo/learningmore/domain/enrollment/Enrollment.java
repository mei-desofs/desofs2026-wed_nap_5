package com.grupo.learningmore.domain.enrollment;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private UUID courseId;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    public Enrollment() {
    }

    public Enrollment(Long userId, UUID courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = LocalDateTime.now();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}
