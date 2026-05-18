package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.dto.Response.ResourceResponse;
import com.grupo.learningmore.services.ResourceService;
import com.grupo.learningmore.services.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final EnrollmentService enrollmentService;

    public ResourceController(ResourceService resourceService, EnrollmentService enrollmentService) {
        this.resourceService = resourceService;
        this.enrollmentService = enrollmentService;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResourceResponse> uploadResource(
            Authentication authentication,
            @PathVariable UUID courseId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        UUID userId = UUID.fromString(authentication.getName());
        Resource resource = resourceService.uploadResource(courseId, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResourceResponse(resource));
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getResourcesByCourse(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        ResponseEntity<List<ResourceResponse>> accessCheck = validateUserAccess(authentication, courseId);
        if (accessCheck != null) {
            return accessCheck;
        }

        List<Resource> resources = resourceService.findByCourseId(courseId);
        List<ResourceResponse> responses = resources.stream()
                .map(this::mapToResourceResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResourceById(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId,
            Authentication authentication
    ) {
        Resource resource = resourceService.findById(resourceId);

        if (!resource.getCourseId().equals(courseId)) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<ResourceResponse> accessCheck = validateUserAccess(authentication, courseId);
        if (accessCheck != null) {
            return accessCheck;
        }

        return ResponseEntity.ok(mapToResourceResponse(resource));
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId
    ) throws IOException {
        Resource resource = resourceService.findById(resourceId);

        if (!resource.getCourseId().equals(courseId)) {
            return ResponseEntity.notFound().build();
        }

        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    private ResourceResponse mapToResourceResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getCourseId(),
                resource.getFilename(),
                resource.getFileSize(),
                resource.getContentType(),
                resource.getUploadedAt(),
                resource.getUploadedBy()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        if (ex.getMessage().toLowerCase().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Reusable verification helper. 
     * Returns null if access is allowed, or a pre-built ResponseEntity (401/403) if denied.
     */
    private <T> ResponseEntity<T> validateUserAccess(Authentication authentication, UUID courseId) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!hasAdminOrProfessorRole(authentication)) {
            try {
                UUID userId = UUID.fromString(authentication.getName());
                
                if (!enrollmentService.isUserEnrolledInCourse(userId, courseId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        return null;
    }

    private boolean hasAdminOrProfessorRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_PROFESSOR"));
    }
}