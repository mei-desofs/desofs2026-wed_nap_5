package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

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

    public boolean isUserEnrolled(Long userId, UUID chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() ->
                        new RuntimeException("Chat room not found"));

        return enrollmentRepository
                .existsByUserIdAndCourseId(
                        userId,
                        chatRoom.getCourse().getId()
                );
    }
}