package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.assignment.Submission;
import com.grupo.learningmore.dto.Request.GradeSubmissionRequest;
import com.grupo.learningmore.dto.Response.SubmissionResponse;
import com.grupo.learningmore.services.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @PostMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<SubmissionResponse> submitAssignment(
            Authentication authentication,
            @PathVariable UUID assignmentId,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        UUID userId = UUID.fromString(authentication.getName());

        Submission submission = submissionService.submit(assignmentId, userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(submission));
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<Page<SubmissionResponse>> getSubmissionsByAssignment(
            Authentication authentication,
            @PathVariable UUID assignmentId,
            Pageable pageable
    ) {
        UUID actorId = UUID.fromString(authentication.getName());

        Page<SubmissionResponse> responses = submissionService
                .getSubmissionsForAssignment(assignmentId, actorId, isAdmin(authentication), pageable)
                .map(this::mapToResponse);

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @GetMapping("/assignments/{assignmentId}/submissions/me")
    public ResponseEntity<SubmissionResponse> getMySubmission(
            Authentication authentication,
            @PathVariable UUID assignmentId
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        Submission submission = submissionService.getMySubmission(assignmentId, userId);
        return ResponseEntity.ok(mapToResponse(submission));
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
            Authentication authentication,
            @PathVariable UUID submissionId,
            @Valid @RequestBody GradeSubmissionRequest request
    ) {
        UUID actorId = UUID.fromString(authentication.getName());

        Submission submission = submissionService.gradeSubmission(
                submissionId,
                request.grade(),
                request.feedback(),
                actorId,
                isAdmin(authentication)
        );

        return ResponseEntity.ok(mapToResponse(submission));
    }

    private SubmissionResponse mapToResponse(Submission submission) {
        return new SubmissionResponse(
                submission.getId(),
                submission.getAssignmentId(),
                submission.getUserId(),
                submission.getSubmittedAt(),
                submission.getStatus(),
                submission.getGrade(),
                submission.getFeedback(),
                submission.getCreatedAt(),
                submission.getUpdatedAt(),
                submission.getVersion()
        );
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
