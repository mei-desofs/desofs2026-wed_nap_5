package com.grupo.learningmore.api;

import com.grupo.learningmore.dto.Request.SendMessageRequest;
import com.grupo.learningmore.dto.Response.ChatMessageResponse;
import com.grupo.learningmore.services.ChatService;
import jakarta.validation.Valid;
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

        Long userId = Long.parseLong(authentication.getName());

        ChatMessageResponse response =
                chatService.sendMessage(
                        userId,
                        chatRoomId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('PROFESSOR')")
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable UUID chatRoomId
    ) {

        return ResponseEntity.ok(
                chatService.getMessages(chatRoomId)
        );
    }
}