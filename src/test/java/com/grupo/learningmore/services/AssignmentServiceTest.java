package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.AssignmentAuditLogRepository;
import com.grupo.learningmore.repositories.AssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private AssignmentAuditLogRepository assignmentAuditLogRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private AssignmentService assignmentService;

    private UUID courseId;
    private UUID professorId;

    @BeforeEach
    public void setUp() {
        courseId = UUID.randomUUID();
        professorId = UUID.randomUUID();
    }

    @Test
    public void testCreateAssignmentSuccess() {
        Course course = new Course("CS-001", "Cybersecurity", "Course", professorId);
        course.setId(courseId);

        when(courseService.findById(courseId)).thenReturn(course);
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment a = invocation.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        Assignment result = assignmentService.createAssignment(
                courseId,
                "Project 1",
                "Implement secure API",
                LocalDateTime.now().plusDays(5),
                professorId,
                false
        );

        assertNotNull(result.getId());
        assertEquals("Project 1", result.getTitle());
        verify(assignmentRepository).save(any(Assignment.class));
        verify(assignmentAuditLogRepository).save(any());
    }

    @Test
    public void testCreateAssignmentDeniedWhenNotCourseOwner() {
        UUID otherProfessor = UUID.randomUUID();

        Course course = new Course("CS-001", "Cybersecurity", "Course", otherProfessor);
        course.setId(courseId);

        when(courseService.findById(courseId)).thenReturn(course);

        assertThrows(AccessDeniedException.class, () -> assignmentService.createAssignment(
                courseId,
                "Project 1",
                "Implement secure API",
                LocalDateTime.now().plusDays(5),
                professorId,
                false
        ));

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    public void testUpdateAssignmentSuccess() {
        Assignment assignment = new Assignment(
                UUID.randomUUID(),
                "Old title",
                "Old desc",
                LocalDateTime.now().plusDays(2),
                courseId,
                professorId
        );

        when(assignmentRepository.findByIdAndCourseId(assignment.getId(), courseId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Assignment updated = assignmentService.updateAssignment(
                courseId,
                assignment.getId(),
                "New title",
                "New desc",
                LocalDateTime.now().plusDays(10),
                professorId,
                false
        );

        assertEquals("New title", updated.getTitle());
        assertEquals("New desc", updated.getDescription());
        verify(assignmentAuditLogRepository).save(any());
    }
}
