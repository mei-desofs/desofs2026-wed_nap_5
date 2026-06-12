package com.grupo.learningmore.domain.chat;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;


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

    

    public ChatMessage(ChatRoom chatRoom, String content, Date sentAt) {
        this.id = generateSecureId();
        this.chatRoom = chatRoom;
        this.content = content;
        this.sentAt = sentAt;
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "CHM-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }


    public void setId(String id) {
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
