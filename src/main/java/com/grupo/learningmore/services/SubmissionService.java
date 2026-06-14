package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.assignment.Submission;
import com.grupo.learningmore.domain.assignment.SubmissionAuditLog;
import com.grupo.learningmore.domain.assignment.SubmissionStatus;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.AssignmentRepository;
import com.grupo.learningmore.repositories.SubmissionAuditLogRepository;
import com.grupo.learningmore.repositories.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024L * 1024L;

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

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx",
            "txt", "csv", "jpg", "jpeg", "png", "zip"
    );

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionAuditLogRepository submissionAuditLogRepository;
    private final EnrollmentService enrollmentService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            SubmissionAuditLogRepository submissionAuditLogRepository,
            EnrollmentService enrollmentService
    ) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionAuditLogRepository = submissionAuditLogRepository;
        this.enrollmentService = enrollmentService;
    }

    @Transactional
    public Submission submit(String assignmentId, String userId, MultipartFile file) throws IOException {

        log.info("Submission attempt: user={} assignment={}", userId, assignmentId);

        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.warn("Submission failed - assignment not found: {}", assignmentId);
                    return new IllegalArgumentException("Assignment not found");
                });

        if (!assignment.canBeSubmitted()) {
            log.warn("Submission rejected - deadline expired: assignment={} user={}",
                    assignmentId, userId);
            throw new IllegalStateException("Assignment deadline has expired");
        }

        if (submissionRepository.existsByAssignmentIdAndUserId(assignmentId, userId)) {
            log.warn("Duplicate submission attempt: user={} assignment={}",
                    userId, assignmentId);
            throw new IllegalArgumentException("User already submitted this assignment");
        }

        if (!enrollmentService.isUserEnrolledInCourse(userId, assignment.getCourseId())) {
            log.warn("Unauthorized submission attempt: user={} course={}",
                    userId, assignment.getCourseId());
            throw new AccessDeniedException("User is not enrolled in the course for this assignment");
        }

        if (assignmentId == null || assignmentId.isBlank()) {
            throw new IllegalArgumentException("Invalid assignment id");
        }

        if (!assignmentId.matches("^[a-zA-Z0-9_-]+$")) {
            log.warn("Invalid assignmentId format: {}", assignmentId);
            throw new IllegalArgumentException("Invalid assignment id");
        }

        String cleanAssignmentId = org.springframework.util.StringUtils.cleanPath(assignmentId);
        String safeAssignmentId = java.nio.file.Paths.get(cleanAssignmentId)
                .getFileName()
                .toString();

        validateSubmissionFile(file, userId, assignmentId);

        Path uploadBasePath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        Path assignmentUploadPath = uploadBasePath
                .resolve("assignments")
                .resolve(safeAssignmentId)
                .normalize();

        if (!assignmentUploadPath.startsWith(uploadBasePath)) {
            log.error("Path traversal detected during submission: user={} assignment={}",
                    userId, assignmentId);
            throw new IllegalArgumentException("Invalid file path: potential path traversal detected");
        }

        Files.createDirectories(assignmentUploadPath);

        String safeOriginalName = sanitizeFilename(file.getOriginalFilename());

        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        String randomPrefix = HexFormat.of().formatHex(randomBytes).toUpperCase();

        String generatedName = randomPrefix + "_" + safeOriginalName;
        Path storedPath = assignmentUploadPath.resolve(generatedName);

        Path normalizedPath = storedPath.normalize();
        Path uploadBase = assignmentUploadPath.normalize();

        if (!normalizedPath.startsWith(uploadBase)) {
            log.error("Path traversal detected during submission file write: user={} assignment={}",
                    userId, assignmentId);
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

        log.info("Submission successful: submissionId={} user={} assignment={}",
                saved.getId(), userId, assignmentId);

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Submission> getSubmissionsForAssignment(
            String assignmentId,
            String actorId,
            boolean isAdmin,
            Pageable pageable
    ) {
        log.info("Fetching submissions: assignment={} actor={} admin={}",
                assignmentId, actorId, isAdmin);

        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.warn("Assignment not found when fetching submissions: {}", assignmentId);
                    return new IllegalArgumentException("Assignment not found");
                });

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            log.warn("Unauthorized submission listing attempt: actor={} assignment={}",
                    actorId, assignmentId);
            throw new AccessDeniedException("Only the assignment owner can view all submissions");
        }

        return submissionRepository.findByAssignmentId(assignmentId, pageable);
    }

    @Transactional(readOnly = true)
    public Submission getMySubmission(String assignmentId, String userId) {
        log.info("Fetching personal submission: user={} assignment={}", userId, assignmentId);

        return submissionRepository.findByAssignmentIdAndUserId(assignmentId, userId)
                .orElseThrow(() -> {
                    log.warn("Submission not found: user={} assignment={}", userId, assignmentId);
                    return new IllegalArgumentException("Submission not found");
                });
    }

    @Transactional
    public Submission gradeSubmission(
            String submissionId,
            BigDecimal grade,
            String feedback,
            String actorId,
            boolean isAdmin
    ) {
        log.info("Grading submission: submission={} actor={} admin={}",
                submissionId, actorId, isAdmin);

        if (grade == null || grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(new BigDecimal("100")) > 0) {
            log.warn("Invalid grade value: {} by actor {}", grade, actorId);
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    log.warn("Submission not found for grading: {}", submissionId);
                    return new IllegalArgumentException("Submission not found");
                });

        var assignment = submission.getAssignment();

        if (assignment == null) {
            log.error("Corrupted submission (missing assignment): {}", submissionId);
            throw new IllegalArgumentException("Assignment not found");
        }

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            log.warn("Unauthorized grading attempt: actor={} submission={}",
                    actorId, submissionId);
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

        log.info("Submission graded successfully: submission={} grade={} actor={}",
                submissionId, grade, actorId);

        return saved;
    }

    private void validateSubmissionFile(MultipartFile file, String userId, String assignmentId) {
        if (file == null || file.isEmpty()) {
            log.warn("Empty submission file: user={} assignment={}", userId, assignmentId);
            throw new IllegalArgumentException("Submission file cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            log.warn("File too large: user={} assignment={} size={}",
                    userId, assignmentId, file.getSize());
            throw new IllegalArgumentException("Submission file exceeds maximum size of 50MB");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            log.warn("Invalid MIME type: user={} assignment={} type={}",
                    userId, assignmentId, mimeType);
            throw new IllegalArgumentException("File type not allowed");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            log.warn("Invalid filename: user={} assignment={}", userId, assignmentId);
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("Invalid file extension: user={} assignment={} ext={}",
                    userId, assignmentId, extension);
            throw new IllegalArgumentException("File extension not allowed");
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "submission.bin";
        }

        String cleanName = Paths.get(filename).getFileName().toString();
        cleanName = cleanName.replaceAll("[^a-zA-Z0-9._-]", "_");
        cleanName = cleanName.replace("..", "_");
        cleanName = cleanName.replace("/", "_");
        cleanName = cleanName.replace("\\", "_");

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