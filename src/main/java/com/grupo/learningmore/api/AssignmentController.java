package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.dto.Request.CreateAssignmentRequest;
import com.grupo.learningmore.dto.Request.UpdateAssignmentRequest;
import com.grupo.learningmore.dto.Response.AssignmentResponse;
import com.grupo.learningmore.services.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<AssignmentResponse> createAssignment(
            Authentication authentication,
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateAssignmentRequest request
    ) {
        UUID actorId = UUID.fromString(authentication.getName());

        Assignment assignment = assignmentService.createAssignment(
                courseId,
                request.title(),
                request.description(),
                request.deadline(),
                actorId,
                isAdmin(authentication)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(assignment));
    }

    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByCourse(@PathVariable UUID courseId) {
        List<AssignmentResponse> responses = assignmentService.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable UUID assignmentId) {
        Assignment assignment = assignmentService.findById(assignmentId);
        return ResponseEntity.ok(mapToResponse(assignment));
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/courses/{courseId}/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            Authentication authentication,
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @Valid @RequestBody UpdateAssignmentRequest request
    ) {
        UUID actorId = UUID.fromString(authentication.getName());

        Assignment assignment = assignmentService.updateAssignment(
                courseId,
                assignmentId,
                request.title(),
                request.description(),
                request.deadline(),
                actorId,
                isAdmin(authentication)
        );

        return ResponseEntity.ok(mapToResponse(assignment));
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/courses/{courseId}/assignments/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(
            Authentication authentication,
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId
    ) {
        UUID actorId = UUID.fromString(authentication.getName());

        assignmentService.deleteAssignment(
                courseId,
                assignmentId,
                actorId,
                isAdmin(authentication)
        );

        return ResponseEntity.noContent().build();
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDeadline(),
                assignment.getCourseId(),
                assignment.getCreatedBy(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt(),
                assignment.getVersion(),
                assignment.getSubmissions().size()
        );
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
