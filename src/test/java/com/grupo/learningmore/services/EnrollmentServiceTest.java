package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private String userId;
    private String courseId;
    private String chatRoomId;
    private ChatRoom mockChatRoom;

    @BeforeEach
    public void setUp() {
        userId = "user-" + System.nanoTime();
        courseId = "course-" + System.nanoTime();
        chatRoomId = "chatroom-" + System.nanoTime();

        mockChatRoom = mock(ChatRoom.class);
        Course mockCourse = mock(Course.class);

        Mockito.lenient().when(mockChatRoom.getCourse()).thenReturn(mockCourse);
        Mockito.lenient().when(mockCourse.getId()).thenReturn(courseId);
    }

    @Test
    public void testIsUserEnrolledInCourseTrue() {
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);

        boolean result = enrollmentService.isUserEnrolledInCourse(userId, courseId);

        assertTrue(result);
        verify(enrollmentRepository).existsByUserIdAndCourseId(userId, courseId);
    }

    @Test
    public void testIsUserEnrolledInCourseFalse() {
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);

        boolean result = enrollmentService.isUserEnrolledInCourse(userId, courseId);

        assertFalse(result);
        verify(enrollmentRepository).existsByUserIdAndCourseId(userId, courseId);
    }

    @Test
    public void testIsUserEnrolledInChatRoomTrue() {
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(mockChatRoom));
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);

        boolean result = enrollmentService.isUserEnrolled(userId, chatRoomId);

        assertTrue(result);
        verify(chatRoomRepository).findById(chatRoomId);
        verify(enrollmentRepository).existsByUserIdAndCourseId(userId, courseId);
    }

    @Test
    public void testIsUserEnrolledInChatRoomFalse() {
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(mockChatRoom));
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);

        boolean result = enrollmentService.isUserEnrolled(userId, chatRoomId);

        assertFalse(result);
        verify(chatRoomRepository).findById(chatRoomId);
        verify(enrollmentRepository).existsByUserIdAndCourseId(userId, courseId);
    }

    @Test
    public void testIsUserEnrolledChatRoomNotFoundThrowsException() {
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                enrollmentService.isUserEnrolled(userId, chatRoomId)
        );

        assertEquals("Chat room not found", exception.getMessage());
        verify(chatRoomRepository).findById(chatRoomId);
        verifyNoInteractions(enrollmentRepository);
    }
}