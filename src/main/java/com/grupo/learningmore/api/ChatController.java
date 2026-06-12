package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.dto.request.CreateChatRoomRequest;
import com.grupo.learningmore.dto.request.SendMessageRequest;
import com.grupo.learningmore.dto.Response.ChatMessageResponse;
import com.grupo.learningmore.dto.Response.ChatRoomResponse;
import com.grupo.learningmore.services.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            @PathVariable String chatRoomId,
            @Valid @RequestBody SendMessageRequest request
    ) {

        log.info("POST /chat/{}/messages - Send message request", chatRoomId);

        try {
            String userId = authentication.getName();

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @PathVariable String chatRoomId,
            Pageable pageable
    ) {

        log.info("GET /chat/{}/messages - Fetch messages", chatRoomId);

        Page<ChatMessageResponse> messages =
                chatService.getMessages(chatRoomId, pageable);

        log.info("Returned {} messages for chat {}", messages.getTotalElements(), chatRoomId);

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatRoomResponse>> getMyChats(Authentication authentication) {

        String userId =  authentication.getName();

        log.info("GET /chat/me - user {}", userId);

        List<ChatRoom> chats = chatService.getChatsForUser(userId);

        List<ChatRoomResponse> result = chats.stream()
                .map(chat -> new ChatRoomResponse(
                        chat.getId(),
                        chat.getName(),
                        chat.getCourse().getId()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping("/chatrooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @Valid @RequestBody CreateChatRoomRequest request
    ) {

        log.info("POST /chatrooms - Creating chat room for course {}", request.courseId());

        ChatRoom chatRoom = chatService.createChatRoom(
                request.name(),
                request.courseId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ChatRoomResponse(
                        chatRoom.getId(),
                        chatRoom.getName(),
                        chatRoom.getCourse().getId()
                ));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoomById(
            @PathVariable String chatRoomId
    ) {

        log.info("GET /chatrooms/{} - Fetch chat room", chatRoomId);

        ChatRoom chatRoom = chatService.getChatRoomById(chatRoomId);

        return ResponseEntity.ok(new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCourse().getId()
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/courses/{courseId}/chatrooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRoomsByCourse(
            @PathVariable String courseId
    ) {

        log.info("GET /chat/courses/{}/chatrooms", courseId);

        List<ChatRoom> chatRooms = chatService.getChatRoomsByCourse(courseId);

        return ResponseEntity.ok(
                chatRooms.stream()
                        .map(chat -> new ChatRoomResponse(
                                chat.getId(),
                                chat.getName(),
                                courseId
                        ))
                        .toList()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/chatrooms")
    public ResponseEntity<List<ChatRoomResponse>> getAllChatRooms() {

        log.info("GET /chat/chatrooms - Fetch all chat rooms");

        List<ChatRoomResponse> result = chatService.findAllChatRooms()
                .stream()
                .map(chat -> new ChatRoomResponse(
                        chat.getId(),
                        chat.getName(),
                        chat.getCourse().getId()
                ))
                .toList();

        log.info("Returned {} chat rooms", result.size());

        return ResponseEntity.ok(result);
    }
}