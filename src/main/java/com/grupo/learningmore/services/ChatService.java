package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatMessage;
import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.dto.Request.SendMessageRequest;
import com.grupo.learningmore.dto.Response.ChatMessageResponse;
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
            Long userId,
            Long chatRoomId,
            SendMessageRequest request
    ) {

        if (!enrollmentService.isUserEnrolled(userId, chatRoomId)) {
            throw new AccessDeniedException(
                    "User not enrolled in this course"
            );
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() ->
                        new RuntimeException("Chat room not found"));

        String sanitizedContent = sanitize(request.getContent());

        ChatMessage message = new ChatMessage();
        message.setId(UUID.randomUUID());
        message.setChatRoom(chatRoom);
        message.setContent(sanitizedContent);
        message.setSentAt(new Date());

        ChatMessage saved = chatMessageRepository.save(message);

        log.info("User {} sent message to room {}",
                userId,
                chatRoomId);

        return new ChatMessageResponse(
                saved.getId(),
                saved.getContent(),
                saved.getSentAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long chatRoomId) {

        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new RuntimeException("Chat room not found");
        }

        return chatMessageRepository
                .findByChatRoomIdOrderBySentAtAsc(chatRoomId)
                .stream()
                .map(message -> new ChatMessageResponse(
                        message.getId(),
                        message.getContent(),
                        message.getSentAt()
                ))
                .toList();
    }

    private String sanitize(String content) {
        return content
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}