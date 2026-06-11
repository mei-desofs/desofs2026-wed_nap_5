package com.grupo.learningmore.domain.enrollment;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;

@Entity
@Table(name = "enrollments")
@Getter
public class Enrollment {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    @Column(nullable = false)
    private boolean active;

    public Enrollment() {
    }

    public Enrollment(String userId, String courseId) {
        this.id = generateSecureId();
        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = LocalDateTime.now();
        this.active = true;
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "ENR-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
