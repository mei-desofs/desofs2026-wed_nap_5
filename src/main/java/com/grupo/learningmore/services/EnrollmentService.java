package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {

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
                .orElseThrow(() ->
                        new RuntimeException("Chat room not found"));

        return enrollmentRepository
                .existsByUserIdAndCourseId(
                        userId,
                        chatRoom.getCourse().getId()
                );
    }

    /**
     * Check if a user is enrolled in a specific course
     */
    public boolean isUserEnrolledInCourse(String userId, String courseId) {

        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);

        log.debug("Enrollment check (course): user {} in course {} = {}",
                userId, courseId, enrolled);

        return enrolled;
    }
}