package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final ChatRoomRepository chatRoomRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            ChatRoomRepository chatRoomRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public boolean isUserEnrolled(UUID userId, UUID chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("Chat room not found while checking enrollment: {}", chatRoomId);
                    return new RuntimeException("Chat room not found");
                });

        boolean enrolled = enrollmentRepository
                .existsByUserIdAndCourseId(
                        userId,
                        chatRoom.getCourse().getId()
                );

        log.debug("Enrollment check (chatRoom): user {} enrolled in course {} = {}",
                userId, chatRoom.getCourse().getId(), enrolled);

        return enrolled;
    }

    /**
     * Check if a user is enrolled in a specific course
     */
    public boolean isUserEnrolledInCourse(UUID userId, UUID courseId) {

        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);

        log.debug("Enrollment check (course): user {} in course {} = {}",
                userId, courseId, enrolled);

        return enrolled;
    }
}