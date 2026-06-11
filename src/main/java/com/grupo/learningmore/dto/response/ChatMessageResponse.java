<<<<<<< HEAD
package com.grupo.learningmore.dto.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
=======
package com.grupo.learningmore.dto.response;
>>>>>>> 5f0069b7a48e9c16c687ab0867f2eafe4fb237dc

import java.util.Date;
import java.util.UUID;

<<<<<<< HEAD
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
=======
public record ChatMessageResponse(
        UUID id,
        String content,
        Date sentAt
) {
>>>>>>> 5f0069b7a48e9c16c687ab0867f2eafe4fb237dc
}