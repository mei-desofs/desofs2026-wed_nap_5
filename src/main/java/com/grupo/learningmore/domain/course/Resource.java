package com.grupo.learningmore.domain.course;

import jakarta.persistence.*;
import lombok.Getter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
 

@Entity
@Table(name = "resources")
@Getter
public class Resource {


    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)      
    private String id;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String uploadedBy;

    public Resource() {
    }

    public Resource(String courseId, String filename, String filePath, Long fileSize, String contentType, String uploadedBy) {
        //this.id = generateSecureId();
        this.courseId = courseId;
        this.filename = filename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = LocalDateTime.now();
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
        return "RSC-" + HexFormat.of().formatHex(bytes).toUpperCase(); 
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}
