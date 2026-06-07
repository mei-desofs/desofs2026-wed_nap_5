package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.dto.response.ResourceResponse;
import com.grupo.learningmore.services.ResourceService;
import com.grupo.learningmore.services.EnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

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

        log.info("POST /courses/{}/resources - Upload resource by user {}", courseId, userId);

        Resource resource = resourceService.uploadResource(courseId, file, userId);

        log.info("Resource uploaded successfully: {} (course {})", resource.getId(), courseId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResourceResponse(resource));
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getResourcesByCourse(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        log.info("GET /courses/{}/resources - Fetch resources", courseId);

        ResponseEntity<List<ResourceResponse>> accessCheck =
                validateUserAccess(authentication, courseId);

        if (accessCheck != null) {
            log.warn("Access denied to resources for course {}", courseId);
            return accessCheck;
        }

        List<Resource> resources = resourceService.findByCourseId(courseId);

        List<ResourceResponse> responses = resources.stream()
                .map(this::mapToResourceResponse)
                .toList();

        log.info("Returned {} resources for course {}", responses.size(), courseId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResourceById(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId,
            Authentication authentication
    ) {
        log.info("GET /courses/{}/resources/{} - Fetch resource", courseId, resourceId);

        Resource resource = resourceService.findById(resourceId);

        if (!resource.getCourseId().equals(courseId)) {
            log.warn("Resource {} does not belong to course {}", resourceId, courseId);
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<ResourceResponse> accessCheck =
                validateUserAccess(authentication, courseId);

        if (accessCheck != null) {
            log.warn("Access denied to resource {} for course {}", resourceId, courseId);
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

        log.warn("DELETE /courses/{}/resources/{} - Delete resource request", courseId, resourceId);

        Resource resource = resourceService.findById(resourceId);

        if (!resource.getCourseId().equals(courseId)) {
            log.warn("Attempt to delete resource {} from wrong course {}", resourceId, courseId);
            return ResponseEntity.notFound().build();
        }

        resourceService.deleteResource(resourceId);

        log.info("Resource {} deleted successfully", resourceId);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("Bad request in ResourceController: {}", ex.getMessage());

        if (ex.getMessage().toLowerCase().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    private <T> ResponseEntity<T> validateUserAccess(Authentication authentication, UUID courseId) {
        if (authentication == null) {
            log.warn("Unauthorized access attempt to course resources {}", courseId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!hasAdminOrProfessorRole(authentication)) {
            try {
                UUID userId = UUID.fromString(authentication.getName());

                if (!enrollmentService.isUserEnrolledInCourse(userId, courseId)) {
                    log.warn("Forbidden access: user {} not enrolled in course {}", userId, courseId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

            } catch (IllegalArgumentException ex) {
                log.warn("Invalid authentication userId format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        return null;
    }

    private boolean hasAdminOrProfessorRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_ADMIN") ||
                                auth.getAuthority().equals("ROLE_PROFESSOR"));
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
}