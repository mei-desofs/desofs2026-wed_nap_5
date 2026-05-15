package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.assignment.Submission;
import com.grupo.learningmore.domain.assignment.SubmissionAuditLog;
import com.grupo.learningmore.domain.assignment.SubmissionStatus;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.AssignmentRepository;
import com.grupo.learningmore.repositories.SubmissionAuditLogRepository;
import com.grupo.learningmore.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SubmissionService {

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024L * 1024L;

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionAuditLogRepository submissionAuditLogRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            SubmissionAuditLogRepository submissionAuditLogRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionAuditLogRepository = submissionAuditLogRepository;
    }

    @Transactional
    public Submission submit(UUID assignmentId, UUID userId, MultipartFile file) throws IOException {
        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.canBeSubmitted()) {
            throw new IllegalStateException("Assignment deadline has expired");
        }

        if (submissionRepository.existsByAssignmentIdAndUserId(assignmentId, userId)) {
            throw new IllegalArgumentException("User already submitted this assignment");
        }

        validateSubmissionFile(file);

        Path assignmentUploadPath = Paths.get(uploadDir, "assignments", assignmentId.toString());
        Files.createDirectories(assignmentUploadPath);

        String safeOriginalName = sanitizeFilename(file.getOriginalFilename());
        String generatedName = UUID.randomUUID() + "_" + safeOriginalName;
        Path storedPath = assignmentUploadPath.resolve(generatedName);
        Files.write(storedPath, file.getBytes());

        Submission submission = new Submission(
                assignmentId,
                userId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                storedPath.toString()
        );

        Submission saved = submissionRepository.save(submission);

        submissionAuditLogRepository.save(new SubmissionAuditLog(
                saved.getId(),
                "SUBMIT",
                userId,
                null,
                "status=" + saved.getStatus(),
                LocalDateTime.now()
        ));

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Submission> getSubmissionsForAssignment(UUID assignmentId, UUID actorId, boolean isAdmin, Pageable pageable) {
        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            throw new AccessDeniedException("Only the assignment owner can view all submissions");
        }

        return submissionRepository.findByAssignmentId(assignmentId, pageable);
    }

    @Transactional(readOnly = true)
    public Submission getMySubmission(UUID assignmentId, UUID userId) {
        return submissionRepository.findByAssignmentIdAndUserId(assignmentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
    }

    @Transactional
    public Submission gradeSubmission(UUID submissionId, BigDecimal grade, String feedback, UUID actorId, boolean isAdmin) {
        if (grade == null || grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        var assignment = assignmentRepository.findById(submission.getAssignmentId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            throw new AccessDeniedException("Only the assignment owner can grade submissions");
        }

        String oldValues = "status=" + submission.getStatus() + ",grade=" + submission.getGrade();

        submission.grade(grade, feedback, actorId);
        Submission saved = submissionRepository.save(submission);

        submissionAuditLogRepository.save(new SubmissionAuditLog(
                saved.getId(),
                "GRADE",
                actorId,
                oldValues,
                "status=" + saved.getStatus() + ",grade=" + saved.getGrade(),
                LocalDateTime.now()
        ));

        return saved;
    }

    private void validateSubmissionFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Submission file cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Submission file exceeds maximum size of 50MB");
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "submission.bin";
        }

        String cleanName = Paths.get(filename).getFileName().toString();
        return cleanName.replace("..", "_");
    }
}
