package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.assignment.Submission;
import com.grupo.learningmore.domain.assignment.SubmissionStatus;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.services.EnrollmentService;
import com.grupo.learningmore.repositories.AssignmentRepository;
import com.grupo.learningmore.repositories.SubmissionAuditLogRepository;
import com.grupo.learningmore.repositories.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private SubmissionAuditLogRepository submissionAuditLogRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private SubmissionService submissionService;

    @TempDir
    Path tempDir;

    private UUID assignmentId;
    private UUID studentId;
    private UUID professorId;

    @BeforeEach
    public void setUp() {
        assignmentId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        professorId = UUID.randomUUID();
        ReflectionTestUtils.setField(submissionService, "uploadDir", tempDir.toString());
    }

    @Test
    public void testSubmitSuccess() throws IOException {
        Assignment assignment = new Assignment(
                assignmentId,
                "Project",
                "Desc",
                LocalDateTime.now().plusDays(3),
                UUID.randomUUID(),
                professorId
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.pdf", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        Submission result = submissionService.submit(assignmentId, studentId, file);

        assertNotNull(result.getId());
        assertEquals(SubmissionStatus.PENDING, result.getStatus());
        verify(submissionAuditLogRepository).save(any());
    }

    @Test
    public void testSubmitFailsWhenDeadlineExpired() {
        Assignment assignment = new Assignment(
                assignmentId,
                "Project",
                "Desc",
                LocalDateTime.now().minusDays(1),
                UUID.randomUUID(),
                professorId
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.pdf", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        assertThrows(IllegalStateException.class, () -> submissionService.submit(assignmentId, studentId, file));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testSubmitFailsWhenStudentNotEnrolled() {
        Assignment assignment = new Assignment(
                assignmentId,
                "Project",
                "Desc",
                LocalDateTime.now().plusDays(3),
                UUID.randomUUID(),
                professorId
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.pdf", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> submissionService.submit(assignmentId, studentId, file));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testGradeFailsWhenNotOwner() {
        UUID otherProfessor = UUID.randomUUID();

        Assignment assignment = new Assignment(
                UUID.randomUUID(),
                "Project",
                "Desc",
                LocalDateTime.now().plusDays(2),
                UUID.randomUUID(),
                professorId
        );

        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId(UUID.randomUUID());

        when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));

        assertThrows(AccessDeniedException.class, () -> submissionService.gradeSubmission(
                submission.getId(),
                new BigDecimal("18.50"),
                "Good work",
                otherProfessor,
                false
        ));

        verify(submissionRepository, never()).save(any());
    }
}
