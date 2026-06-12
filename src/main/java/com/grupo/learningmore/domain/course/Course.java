package com.grupo.learningmore.domain.course;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;


@Entity
@Table(name = "courses")
@Getter
public class Course {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String createdBy;

    public Course() {
    }

    public Course(String name,String code, String description, String createdBy) {
        this.id = generateSecureId();
        this.code = code;     
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

     

    /*private String generateSecureCourseCode() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "CRS-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }*/

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "ID-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
