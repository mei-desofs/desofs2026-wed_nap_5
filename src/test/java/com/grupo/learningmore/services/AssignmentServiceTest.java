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
    public void testCreateAssignmentAsAdminBypassesOwnershipCheck() {
        Course course = new Course("CS-001", "Cybersecurity", "Course", UUID.randomUUID());
        course.setId(courseId);

        when(courseService.findById(courseId)).thenReturn(course);
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment a = invocation.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        Assignment result = assignmentService.createAssignment(
                courseId,
                "Project 2",
                "Admin-created assignment",
                LocalDateTime.now().plusDays(7),
                professorId,
                true
        );

        assertNotNull(result.getId());
        assertEquals("Project 2", result.getTitle());
        verify(assignmentRepository).save(any(Assignment.class));
        verify(assignmentAuditLogRepository).save(any());
    }

    @Test
    public void testCreateAssignmentRejectsNullDeadline() {
        Course course = new Course("CS-001", "Cybersecurity", "Course", professorId);
        course.setId(courseId);

        assertThrows(IllegalArgumentException.class, () -> assignmentService.createAssignment(
                courseId,
                "Project 1",
                "Implement secure API",
                null,
                professorId,
                false
        ));

        verify(assignmentRepository, never()).save(any());
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

        @Test
        public void testFindByCourseIdReturnsAssignments() {
        Assignment assignment = new Assignment(
            UUID.randomUUID(),
            "Title",
            "Desc",
            LocalDateTime.now().plusDays(3),
            courseId,
            professorId
        );
        Course course = new Course("CS-001", "Cybersecurity", "Course", professorId);
        course.setId(courseId);

        when(courseService.findById(courseId)).thenReturn(course);
        when(assignmentRepository.findByCourseId(courseId)).thenReturn(java.util.List.of(assignment));

        java.util.List<Assignment> result = assignmentService.findByCourseId(courseId);

        assertEquals(1, result.size());
        assertEquals(assignment.getId(), result.get(0).getId());
        verify(courseService).findById(courseId);
        verify(assignmentRepository).findByCourseId(courseId);
        }

        @Test
        public void testFindByIdNotFoundThrowsException() {
        UUID assignmentId = UUID.randomUUID();

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> assignmentService.findById(assignmentId));
        verify(assignmentRepository).findById(assignmentId);
        }

        @Test
        public void testUpdateAssignmentPreservesUnprovidedFieldsAndUpdatesDeadline() {
        Assignment assignment = new Assignment(
            UUID.randomUUID(),
            "Old title",
            "Old desc",
            LocalDateTime.now().plusDays(2),
            courseId,
            professorId
        );
        LocalDateTime updatedAtBefore = assignment.getUpdatedAt();

        when(assignmentRepository.findByIdAndCourseId(assignment.getId(), courseId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Assignment updated = assignmentService.updateAssignment(
            courseId,
            assignment.getId(),
            "   ",
            null,
            LocalDateTime.now().plusDays(10),
            professorId,
            false
        );

        assertEquals("Old title", updated.getTitle());
        assertEquals("Old desc", updated.getDescription());
        assertTrue(updated.getDeadline().isAfter(LocalDateTime.now().plusDays(3)));
        assertNotEquals(updatedAtBefore, updated.getUpdatedAt());
        verify(assignmentRepository).save(assignment);
        verify(assignmentAuditLogRepository).save(any());
        }

        @Test
        public void testUpdateAssignmentRejectsUnauthorizedActor() {
        Assignment assignment = new Assignment(
            UUID.randomUUID(),
            "Old title",
            "Old desc",
            LocalDateTime.now().plusDays(2),
            courseId,
            professorId
        );

        when(assignmentRepository.findByIdAndCourseId(assignment.getId(), courseId)).thenReturn(Optional.of(assignment));

        assertThrows(AccessDeniedException.class, () -> assignmentService.updateAssignment(
            courseId,
            assignment.getId(),
            "New title",
            "New desc",
            LocalDateTime.now().plusDays(5),
            UUID.randomUUID(),
            false
        ));

        verify(assignmentRepository, never()).save(any());
        }

        @Test
        public void testUpdateAssignmentRejectsExpiredDeadline() {
        Assignment assignment = new Assignment(
            UUID.randomUUID(),
            "Old title",
            "Old desc",
            LocalDateTime.now().plusDays(2),
            courseId,
            professorId
        );

        when(assignmentRepository.findByIdAndCourseId(assignment.getId(), courseId)).thenReturn(Optional.of(assignment));

        assertThrows(IllegalArgumentException.class, () -> assignmentService.updateAssignment(
            courseId,
            assignment.getId(),
            "New title",
            "New desc",
            LocalDateTime.now().minusDays(1),
            professorId,
            false
        ));

        verify(assignmentRepository, never()).save(any());
        }

        @Test
        public void testDeleteAssignmentSuccess() {
        Assignment assignment = new Assignment(
            UUID.randomUUID(),
            "Title",
            "Desc",
            LocalDateTime.now().plusDays(2),
            courseId,
            professorId
        );

        when(assignmentRepository.findByIdAndCourseId(assignment.getId(), courseId)).thenReturn(Optional.of(assignment));

        assignmentService.deleteAssignment(courseId, assignment.getId(), professorId, false);

        verify(assignmentAuditLogRepository).save(any());
        verify(assignmentRepository).delete(assignment);
        }

        @Test
        public void testDeleteAssignmentRejectsUnauthorizedActor() {
        Assignment assignment = new Assignment(
            UUID.randomUUID(),
            "Title",
            "Desc",
            LocalDateTime.now().plusDays(2),
            courseId,
            professorId
        );

        when(assignmentRepository.findByIdAndCourseId(assignment.getId(), courseId)).thenReturn(Optional.of(assignment));

        assertThrows(AccessDeniedException.class, () -> assignmentService.deleteAssignment(
            courseId,
            assignment.getId(),
            UUID.randomUUID(),
            false
        ));

        verify(assignmentRepository, never()).delete(any());
        verify(assignmentAuditLogRepository, never()).save(any());
        }
}
