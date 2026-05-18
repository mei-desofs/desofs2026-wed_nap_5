package com.grupo.learningmore.domain.chat;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;


@Entity
@Getter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String content;
    private Date sentAt;

    public ChatMessage() {
    }

    public ChatMessage(UUID id, ChatRoom chatRoom, String content, Date sentAt) {
        this.id = id;
        this.chatRoom = chatRoom;
        this.content = content;
        this.sentAt = sentAt;
    }

    public ChatMessage(ChatRoom chatRoom, String content, Date sentAt) {
        this.chatRoom = chatRoom;
        this.content = content;
        this.sentAt = sentAt;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
