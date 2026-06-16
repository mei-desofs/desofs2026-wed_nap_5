package com.grupo.learningmore.domain.chat;

import jakarta.persistence.*;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HexFormat;
 


@Entity
@Getter
public class ChatMessage {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String content;
    private Date sentAt;

    public ChatMessage() {
    }

    public ChatMessage( ChatRoom chatRoom, String content, Date sentAt) {
      //  this.id = generateSecureId();
        this.chatRoom = chatRoom;
        this.content = content;
        this.sentAt = sentAt;
    }

    /*public ChatMessage(ChatRoom chatRoom, String content, Date sentAt) {
        this.chatRoom = chatRoom;
        this.content = content;
        this.sentAt = sentAt;
    }*/

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateSecureId();
        }
    }    

    private String generateSecureId() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return "CHM-" + HexFormat.of().formatHex(bytes).toUpperCase(); 
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
