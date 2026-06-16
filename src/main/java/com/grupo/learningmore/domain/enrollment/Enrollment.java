package com.grupo.learningmore.domain.enrollment;

import jakarta.persistence.*;
import lombok.Getter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
 

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
        //this.id = generateSecureId();
        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = LocalDateTime.now();
        this.active = true;
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
        return "ENR-" + HexFormat.of().formatHex(bytes).toUpperCase(); 
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
