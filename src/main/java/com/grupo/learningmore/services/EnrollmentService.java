package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.domain.enrollment.Enrollment;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



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

    public boolean isUserEnrolled(String userId, String chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("Chat room not found while checking enrollment: {}", chatRoomId);
                    return new RuntimeException("Chat room not found");
                });

        return enrollmentRepository.existsByUserIdAndCourseId(
                userId,
                chatRoom.getCourse().getId()
        );
    }

    /**
     * Check if a user is enrolled in a specific course
     */
    public boolean isUserEnrolledInCourse(String userId, String courseId) {

        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}