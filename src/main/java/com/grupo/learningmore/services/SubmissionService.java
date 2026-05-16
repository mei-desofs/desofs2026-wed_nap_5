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
import java.util.*;

@Service
public class SubmissionService {

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024L * 1024L;

    // Whitelist of allowed MIME types for submissions (R11, AC4 - prevent malicious uploads)
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "text/csv",
            "image/jpeg",
            "image/png",
            "application/zip",
            "application/x-zip-compressed"
    );

    // Whitelist of allowed file extensions (secondary validation)
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx",
            "txt", "csv", "jpg", "jpeg", "png", "zip"
    );

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

        // Prevent path traversal (AC5, R12): ensure resolved path is within assignment directory
        Path normalizedPath = storedPath.normalize();
        Path uploadBase = assignmentUploadPath.normalize();
        if (!normalizedPath.startsWith(uploadBase)) {
            throw new IllegalArgumentException("Invalid file path: potential path traversal detected");
        }

        Files.write(storedPath, file.getBytes());

        Submission submission = new Submission(
                assignment,
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

        var assignment = submission.getAssignment();

        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found");
        }

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

        // Validate MIME type (R11, AC4 - prevent malicious uploads)
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: PDF, Word, PowerPoint, Excel, Text, CSV, Images, ZIP");
        }

        // Validate file extension as secondary check
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File extension not allowed. Allowed extensions: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "submission.bin";
        }

        // Extract just the filename without path (prevents path traversal - R12, AC5)
        String cleanName = Paths.get(filename).getFileName().toString();

        // Remove potentially dangerous characters
        cleanName = cleanName.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Remove path traversal attempts
        cleanName = cleanName.replace("..", "_");
        cleanName = cleanName.replace("/", "_");
        cleanName = cleanName.replace("\\", "_");

        // Limit filename length
        if (cleanName.length() > 100) {
            cleanName = cleanName.substring(0, 100);
        }

        return cleanName;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
