package com.grupo.learningmore.domain.chat;

import com.grupo.learningmore.domain.course.Course;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;

@Entity
@Getter
public class ChatRoom {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String name;


    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    public ChatRoom() {
    }

     

    public ChatRoom(String name) {
        this.id = generateSecureId();
        this.name = name;
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "CHR-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
