package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatMessage;
import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.dto.request.SendMessageRequest;
import com.grupo.learningmore.dto.response.ChatMessageResponse;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.ChatMessageRepository;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final EnrollmentService enrollmentService;

    private static final Logger log =
            LoggerFactory.getLogger(ChatService.class);

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            ChatRoomRepository chatRoomRepository,
            EnrollmentService enrollmentService
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.enrollmentService = enrollmentService;
    }

    @Transactional
    public ChatMessageResponse sendMessage(
            UUID userId,
            UUID chatRoomId,
            SendMessageRequest request
    ) {

        log.info("Sending message - user {} to chatRoom {}", userId, chatRoomId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        if (!enrollmentService.isUserEnrolled(userId, chatRoomId)) {
            throw new AccessDeniedException("User not enrolled in this course");
        }

        String sanitizedContent = sanitize(request.content());

        if (!sanitizedContent.equals(request.content())) {
            log.warn("Message from user {} was sanitized (possible XSS attempt)", userId);
        }

        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setContent(sanitizedContent);
        message.setSentAt(new Date());

        ChatMessage saved = chatMessageRepository.save(message);

        log.info("Message sent successfully - user {} in chatRoom {}, messageId {}",
                userId, chatRoomId, saved.getId());

        return new ChatMessageResponse(
                saved.getId(),
                saved.getContent(),
                saved.getSentAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(UUID chatRoomId) {

        log.info("Fetching messages for chatRoom {}", chatRoomId);

        if (!chatRoomRepository.existsById(chatRoomId)) {
            log.warn("Chat room not found: {}", chatRoomId);
            throw new RuntimeException("Chat room not found");
        }

        List<ChatMessageResponse> result = chatMessageRepository
                .findByChatRoomIdOrderBySentAtAsc(chatRoomId)
                .stream()
                .map(message -> new ChatMessageResponse(
                        message.getId(),
                        message.getContent(),
                        message.getSentAt()
                ))
                .toList();

        log.info("Returned {} messages for chatRoom {}", result.size(), chatRoomId);

        return result;
    }

    private String sanitize(String content) {
        return content
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
