package com.grupo.learningmore.api;

import com.grupo.learningmore.dto.request.SendMessageRequest;
import com.grupo.learningmore.dto.response.ChatMessageResponse;
import com.grupo.learningmore.services.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('STUDENT') or hasRole('PROFESSOR')"
    )
    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            Authentication authentication,
            @PathVariable UUID chatRoomId,
            @Valid @RequestBody SendMessageRequest request
    ) {

        log.info("POST /chat/{}/messages - Send message request", chatRoomId);

        try {
            UUID userId = UUID.fromString(authentication.getName());

            log.info("User {} sending message to chat {}", userId, chatRoomId);

            ChatMessageResponse response =
                    chatService.sendMessage(
                            userId,
                            chatRoomId,
                            request
                    );

            log.info("Message sent successfully by user {} in chat {}", userId, chatRoomId);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (NumberFormatException e) {
            log.warn("Invalid authentication userId format while sending message to chat {}", chatRoomId);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('PROFESSOR')")
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable UUID chatRoomId
    ) {

        log.info("GET /chat/{}/messages - Fetch messages", chatRoomId);

        List<ChatMessageResponse> messages = chatService.getMessages(chatRoomId);

        log.info("Returned {} messages for chat {}", messages.size(), chatRoomId);

        return ResponseEntity.ok(messages);
    }
}