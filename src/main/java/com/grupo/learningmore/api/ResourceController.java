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

        ResourceResponse response = mapToResourceResponse(resource);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getResourcesByCourse(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        // Check if user is authorized to see resources
        // Students can only see resources if they're enrolled
        // Professors and Admins can always see resources
        if (authentication != null && !hasAdminOrProfessorRole(authentication)) {
            final long userId;
            try {
                userId = Long.parseLong(authentication.getName());
            } catch (NumberFormatException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (!enrollmentService.isUserEnrolledInCourse(userId, courseId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
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

        // Verify that the resource belongs to the course
        if (!resource.getCourseId().equals(courseId)) {
            return ResponseEntity.notFound().build();
        }

        // Check if user is authorized to see this resource
        // Students can only see resources if they're enrolled
        // Professors and Admins can always see resources
        if (authentication != null && !hasAdminOrProfessorRole(authentication)) {
            final Long userId;
            try {
                userId = Long.parseLong(authentication.getName());
            } catch (NumberFormatException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (!enrollmentService.isUserEnrolledInCourse(userId, courseId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
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

        // Verify that the resource belongs to the course
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
        // Se a mensagem contiver "not found", retorna 404, caso contrário 400
        if (ex.getMessage().toLowerCase().contains("not found")) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    private boolean hasAdminOrProfessorRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_PROFESSOR"));
    }
}
