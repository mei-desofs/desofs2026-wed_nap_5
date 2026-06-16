package com.grupo.learningmore.domain.chat;

import com.grupo.learningmore.domain.course.Course;
import jakarta.persistence.*;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.HexFormat;
 

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
       // this.id = generateSecureId();
        this.name = name;
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
        return "CHR-" + HexFormat.of().formatHex(bytes).toUpperCase(); 
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
