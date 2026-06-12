package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.assignment.Submission;
import com.grupo.learningmore.domain.assignment.SubmissionStatus;
import com.grupo.learningmore.exceptions.AccessDeniedException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private String assignmentId;
    private String studentId;
    private String professorId;

    @BeforeEach
    public void setUp() {
        assignmentId = "ASN-5a8c3b1f2e4d6a9c8b7f5e3d2c1a0b9f";
        studentId = "USR-a1b2c3d4e5f6789012345678901a2b3c";
        professorId = "USR-b2c3d4e5f6789012345678901a2b3c4d5";
        ReflectionTestUtils.setField(submissionService, "uploadDir", tempDir.toString());
    }

    @Test
    public void testSubmitSuccess() throws IOException {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "USR-c3d4e5f6789012345678901a2b3c4d5e",
                LocalDateTime.now().plusDays(3)
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.pdf", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission s = invocation.getArgument(0);
            s.setId("SUB-1a2b3c4d5e6f789012345678901a2b3c");
            return s;
        });

        Submission result = submissionService.submit(assignmentId, studentId, file);

        assertNotNull(result.getId());
        assertEquals(SubmissionStatus.PENDING, result.getStatus());
        verify(submissionAuditLogRepository).save(any());
    }

    @Test
    public void testSubmitFailsWhenAlreadySubmitted() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "USR-d4e5f6789012345678901a2b3c4d5e6f",
                LocalDateTime.now().plusDays(3)
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.pdf", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> submissionService.submit(assignmentId, studentId, file));

        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testSubmitFailsWhenFileIsNull() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "USR-e5f6789012345678901a2b3c4d5e6f70",
                LocalDateTime.now().plusDays(3)
        );

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> submissionService.submit(assignmentId, studentId, null));

        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testSubmitFailsWhenMimeTypeIsNotAllowed() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "USR-f6f6789012345678901a2b3c4d5e6f701",
                LocalDateTime.now().plusDays(3)
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.pdf", "application/x-msdownload", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> submissionService.submit(assignmentId, studentId, file));

        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testSubmitFailsWhenExtensionIsNotAllowed() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-c3d4e5f6789012345678901a2b3c4d5e",
                LocalDateTime.now().plusDays(3)
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.exe", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> submissionService.submit(assignmentId, studentId, file));

        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testSubmitFailsWhenFileIsTooLarge() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-d4e5f6789012345678901a2b3c4d5e6f",
                LocalDateTime.now().plusDays(3)
        );

        MultipartFile oversizedFile = mock(MultipartFile.class);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);
        when(oversizedFile.isEmpty()).thenReturn(false);
        when(oversizedFile.getSize()).thenReturn(50L * 1024L * 1024L + 1L);

        assertThrows(IllegalArgumentException.class, () -> submissionService.submit(assignmentId, studentId, oversizedFile));

        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testSubmitSanitizesFilenameAndStoresWithinAssignmentDirectory() throws IOException {
        Assignment assignment = createTestAssignment(
                assignmentId, 
                "CRS-c3d4e5f6789012345678901a2b3c4d5e", 
                LocalDateTime.now().plusDays(3)
        );

        MockMultipartFile file = new MockMultipartFile("file", "../evil name.pdf", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission s = invocation.getArgument(0);
            s.setId("SUB-2b3c4d5e6f789012345678901a2b3c4d");
            return s;
        });

        Submission result = submissionService.submit(assignmentId, studentId, file);

        assertNotNull(result.getFilePath());
        assertTrue(result.getFilePath().contains(tempDir.resolve("assignments").resolve(assignmentId).toString()));
        assertTrue(Path.of(result.getFilePath()).getFileName().toString().contains("evil_name.pdf"));
    }

    @Test
    public void testSubmitAcceptsFileAtMaximumSize() throws IOException {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-f6f6789012345678901a2b3c4d5e6f702",
                LocalDateTime.now().plusDays(3)
        );

        MultipartFile maxSizeFile = mock(MultipartFile.class);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);
        when(maxSizeFile.isEmpty()).thenReturn(false);
        when(maxSizeFile.getSize()).thenReturn(50L * 1024L * 1024L);
        when(maxSizeFile.getContentType()).thenReturn("application/pdf");
        when(maxSizeFile.getOriginalFilename()).thenReturn("work.pdf");
        when(maxSizeFile.getBytes()).thenReturn("content".getBytes());
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission s = invocation.getArgument(0);
            s.setId("SUB-3c4d5e6f789012345678901a2b3c4d5e");
            return s;
        });

        Submission result = submissionService.submit(assignmentId, studentId, maxSizeFile);

        assertNotNull(result.getId());
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    public void testSubmitPreservesOneHundredCharacterFilename() throws IOException {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-a6789012345678901a2b3c4d5e6f7012",
                LocalDateTime.now().plusDays(3)
        );

        String originalFilename = "a".repeat(96) + ".pdf";
        MockMultipartFile file = new MockMultipartFile("file", originalFilename, "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.existsByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(false);
        when(enrollmentService.isUserEnrolledInCourse(studentId, assignment.getCourseId())).thenReturn(true);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission s = invocation.getArgument(0);
            s.setId("SUB-4d5e6f789012345678901a2b3c4d5e6f");
            return s;
        });

        Submission result = submissionService.submit(assignmentId, studentId, file);

        String storedName = Path.of(result.getFilePath()).getFileName().toString();
        assertTrue(storedName.endsWith(originalFilename));
    }

    @Test
    public void testSubmitFailsWhenDeadlineExpired() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-b789012345678901a2b3c4d5e6f70123",
                LocalDateTime.now().minusDays(1) // Cenário de expiração
        );

        MockMultipartFile file = new MockMultipartFile("file", "work.pdf", "application/pdf", "content".getBytes());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        assertThrows(IllegalStateException.class, () -> submissionService.submit(assignmentId, studentId, file));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    public void testSubmitFailsWhenStudentNotEnrolled() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-c89012345678901a2b3c4d5e6f701234",
                LocalDateTime.now().plusDays(3)
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
        String otherProfessor = "USR-d9012345678901a2b3c4d5e6f7012345";

        Assignment assignment = createTestAssignment(
                "ASN-e012345678901a2b3c4d5e6f70123456",
                "CRS-d01234567890a1b2c3d4e5f678901234",
                LocalDateTime.now().plusDays(2)
        );

        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId("SUB-5e6f789012345678901a2b3c4d5e6f78");

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

    @Test
    public void testGradeSubmissionSuccess() {
        Assignment assignment = createTestAssignment(
                "ASN-f123456789012345678901a2b3c4d5e67",
                "CRS-e12345678901a2b3c4d5e6f7890123456",
                LocalDateTime.now().plusDays(2)
        );

        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId("SUB-6f789012345678901a2b3c4d5e6f7890");

        when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Submission graded = submissionService.gradeSubmission(
                submission.getId(),
                new BigDecimal("18.50"),
                "Good work",
                professorId,
                false
        );

        assertEquals(SubmissionStatus.GRADED, graded.getStatus());
        assertEquals(new BigDecimal("18.50"), graded.getGrade());
        assertEquals("Good work", graded.getFeedback());
        assertEquals(professorId, graded.getLastModifiedBy());
        verify(submissionAuditLogRepository).save(any());
    }

    @Test
    public void testGradeSubmissionRejectsInvalidLowGrade() {
        assertThrows(IllegalArgumentException.class, () -> submissionService.gradeSubmission(
                "SUB-7890123456789a01b2c3d4e5f678901ab",
                new BigDecimal("-1"),
                "Bad grade",
                professorId,
                false
        ));
    }

    @Test
    public void testGradeSubmissionRejectsNullGrade() {
        assertThrows(IllegalArgumentException.class, () -> submissionService.gradeSubmission(
                "SUB-8901234567890ab1c2d3e4f5678901abc",
                null,
                "Bad grade",
                professorId,
                false
        ));
    }

    @Test
    public void testGradeSubmissionRejectsInvalidHighGrade() {
        assertThrows(IllegalArgumentException.class, () -> submissionService.gradeSubmission(
                "SUB-9012345678901bc2d3e4f567890abc1bcd",
                new BigDecimal("101"),
                "Bad grade",
                professorId,
                false
        ));
    }

    @Test
    public void testGradeSubmissionAcceptsZeroGrade() {
        Assignment assignment = createTestAssignment(
                "ASN-a123456789012bc3d4e5f6789012bc3de",
                "CRS-f234567890123cd4e5f67890123cd45ef",
                LocalDateTime.now().plusDays(2)
        );

        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId("SUB-a123456789012cd4e5f67890123cd45efg");

        when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Submission graded = submissionService.gradeSubmission(
                submission.getId(),
                BigDecimal.ZERO,
                "Minimum passing grade",
                professorId,
                false
        );

        assertEquals(BigDecimal.ZERO, graded.getGrade());
        assertEquals(SubmissionStatus.GRADED, graded.getStatus());
    }

    @Test
    public void testGradeSubmissionAcceptsHundredGrade() {
        Assignment assignment = createTestAssignment(
                "ASN-b234567890123cd4e5f678901234de4ef",
                "CRS-g345678901234de5f6789012345ef56fgh",
                LocalDateTime.now().plusDays(2)
        );

        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId("SUB-b234567890123de5f6789012345ef56fh");

        when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Submission graded = submissionService.gradeSubmission(
                submission.getId(),
                new BigDecimal("100"),
                "Perfect work",
                professorId,
                false
        );

        assertEquals(new BigDecimal("100"), graded.getGrade());
        assertEquals(SubmissionStatus.GRADED, graded.getStatus());
    }

    @Test
    public void testGradeSubmissionRejectsAlreadyGradedSubmission() {
        Assignment assignment = createTestAssignment(
                "ASN-c345678901234de5f6789012345ef56fghi",
                "CRS-h456789012345ef6789012345f6g67ghij",
                LocalDateTime.now().plusDays(2)
        );

        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId("SUB-d456789012345ef6789012345ef67ghijk");
        submission.grade(new BigDecimal("10.00"), "Initial grade", professorId);

        when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));

        assertThrows(IllegalStateException.class, () -> submissionService.gradeSubmission(
                submission.getId(),
                new BigDecimal("15.00"),
                "Regrade",
                professorId,
                false
        ));
    }

    @Test
    public void testGradeSubmissionRejectsWhenAssignmentIsMissing() {
        Assignment assignment = createTestAssignment(
                "ASN-x1y2z3a4b5c6d7e8f901a2b3c4d5e6f7",
                "CRS-z1y2x3w4v5u6t7s8r9q0p1o2n3m4l5k6",
                LocalDateTime.now().plusDays(2)
        );

        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId("SUB-m1n2o3p4q5r6s7t8u9v0w1x2y3z4a5b6");
        submission.setAssignment(null);

        when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));

        assertThrows(IllegalArgumentException.class, () -> submissionService.gradeSubmission(
                submission.getId(),
                new BigDecimal("15.00"),
                "Regrade",
                professorId,
                false
                ));
    }

    @Test
    public void testGetMySubmissionNotFoundThrowsException() {
        when(submissionRepository.findByAssignmentIdAndUserId(assignmentId, studentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> submissionService.getMySubmission(assignmentId, studentId));
        verify(submissionRepository).findByAssignmentIdAndUserId(assignmentId, studentId);
    }

    @Test
    public void testGetSubmissionsForAssignmentSuccess() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-a1b2c3d4e5f6789012345678901a2b3c",
                LocalDateTime.now().plusDays(2)
        );
        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        submission.setId("SUB-z1y2x3w4v5u6t7s8r9q0p1o2n3m4l5k6");

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(submissionRepository.findByAssignmentId(eq(assignmentId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(submission)));

        Page<Submission> result = submissionService.getSubmissionsForAssignment(assignmentId, professorId, false, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(submissionRepository).findByAssignmentId(eq(assignmentId), any(Pageable.class));
    }

    @Test
    public void testGetSubmissionsForAssignmentRejectsUnauthorizedActor() {
        Assignment assignment = createTestAssignment(
                assignmentId,
                "CRS-b2c3d4e5f6789012345678901a2b3c4d",
                LocalDateTime.now().plusDays(2)
        );

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        String unauthorizedActorId = "USR-hacker123456789012345678901a2b3c";

        assertThrows(AccessDeniedException.class, () -> submissionService.getSubmissionsForAssignment(
                assignmentId,
                unauthorizedActorId,
                false,
                Pageable.unpaged()
        ));
    }

     
    private Assignment createTestAssignment(String id, String courseId, LocalDateTime deadline) {
        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setTitle("Project");
        assignment.setDescription("Desc");
        assignment.setDeadline(deadline);
        assignment.setCourseId(courseId);
        assignment.setCreatedBy(professorId);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        assignment.setVersion(0);
        return assignment;
    }
}