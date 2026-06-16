package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatMessage;
import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.dto.request.SendMessageRequest;
import com.grupo.learningmore.dto.response.ChatMessageResponse;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.ChatMessageRepository;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import com.grupo.learningmore.repositories.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;

    private static final Logger log =
            LoggerFactory.getLogger(ChatService.class);

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            ChatRoomRepository chatRoomRepository,
            CourseRepository courseRepository,
            EnrollmentService enrollmentService
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.courseRepository =  courseRepository;
        this.enrollmentService = enrollmentService;
    }

    @Transactional
    public ChatMessageResponse sendMessage(
            String userId,
            String chatRoomId,
            SendMessageRequest request
    ) {

        log.debug("Message submission requested - user={}, chatRoom={}",
                userId, chatRoomId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("Message submission failed - chat room not found: {}",
                            chatRoomId);
                    return new IllegalArgumentException("Chat room not found");
                });

        if (!enrollmentService.isUserEnrolled(userId, chatRoomId)) {

            log.warn("Unauthorized message submission attempt - user={}, chatRoom={}", userId, chatRoomId);

            throw new AccessDeniedException(
                    "User not enrolled in this course"
            );
        }

        String sanitizedContent = sanitize(request.content());

        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setContent(sanitizedContent);
        message.setSentAt(new Date());

        ChatMessage saved = chatMessageRepository.save(message);

        log.info(
                "Message sent successfully - messageId={}, user={}, chatRoom={}",
                saved.getId(),
                userId,
                chatRoomId
        );

        return new ChatMessageResponse(
                saved.getId(),
                saved.getContent(),
                saved.getSentAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getMessages(String chatRoomId, Pageable pageable) {

        log.info("Fetching paginated messages for chatRoom {}", chatRoomId);

        if (!chatRoomRepository.existsById(chatRoomId)) {
            log.warn("Chat room not found: {}", chatRoomId);
            throw new RuntimeException("Chat room not found");
        }

        return chatMessageRepository
                .findByChatRoomIdOrderBySentAtAsc(chatRoomId, pageable)
                .map(message -> new ChatMessageResponse(
                        message.getId(),
                        message.getContent(),
                        message.getSentAt()
                ));
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatsForUser(String userId) {

        log.info("Fetching chats for user {}", userId);

        return chatRoomRepository.findChatsByUserId(userId);
    }

    @Transactional
    public ChatRoom createChatRoom(String name, String courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoom.setCourse(course);

        return chatRoomRepository.save(chatRoom);
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(String chatRoomId) {

        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRoomsByCourse(String courseId) {
        return chatRoomRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> findAllChatRooms() {

        log.info("Fetching all chat rooms");

        return chatRoomRepository.findAll();
    }

    private String sanitize(String content) {
        return content
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
