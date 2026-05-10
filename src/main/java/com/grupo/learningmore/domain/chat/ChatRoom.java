package com.grupo.learningmore.domain.chat;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Entity
@Getter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    //TODO: verificar se está correto depois de ter a classe
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    public ChatRoom() {
    }

    public ChatRoom(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
