package com.grupo.learningmore.dto.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.UUID;

public class ChatMessageResponse {

    private UUID id;
    @NotBlank
    @Size(max = 1000)
    private String content;
    private Date sentAt;

    public ChatMessageResponse(UUID id, String content, Date sentAt) {
        this.id = id;
        this.content = content;
        this.sentAt = sentAt;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getSentAt() {
        return sentAt;
    }
}