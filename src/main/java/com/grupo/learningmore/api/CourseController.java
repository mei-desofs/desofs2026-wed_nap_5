package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.enrollment.Enrollment;
import com.grupo.learningmore.dto.request.CreateCourseRequest;
import com.grupo.learningmore.dto.request.UpdateCourseRequest;
import com.grupo.learningmore.dto.response.CourseResponse;
import com.grupo.learningmore.services.CourseService;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;
    private final EnrollmentRepository enrollmentRepository;

    public CourseController(CourseService courseService, EnrollmentRepository enrollmentRepository) {
        this.courseService = courseService;
        this.enrollmentRepository = enrollmentRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            Authentication authentication,
            @Valid @RequestBody CreateCourseRequest request
    ) {
        String userId = authentication.getName();

        log.info("POST /courses - Create course request by admin {}", userId);
        UUID userId = UUID.fromString(authentication.getName());

        log.info("POST /courses - Create course request by admin {}", userId);

        Course course = courseService.createCourse(
                request.code(),
                request.name(),
                request.description(),
                userId
        );

        log.info("Course created successfully with id {} and code {}", course.getId(), course.getCode());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToCourseResponse(course));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable String id) {

        log.info("GET /courses/{} - Fetch course by id", id);

    public ResponseEntity<CourseResponse> getCourseById(@PathVariable UUID id) {

        log.info("GET /courses/{} - Fetch course by id", id);

        Course course = courseService.findById(id);

        return ResponseEntity.ok(mapToCourseResponse(course));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CourseResponse> getCourseByCode(@PathVariable String code) {

        log.info("GET /courses/code/{} - Fetch course by code", code);

        Course course = courseService.findByCode(code);

        return ResponseEntity.ok(mapToCourseResponse(course));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {

        log.info("GET /courses - Fetch all courses");

        List<Course> courses = courseService.findAll();

        List<CourseResponse> responses = courses.stream()
                .map(this::mapToCourseResponse)
                .toList();

        log.info("Returned {} courses", responses.size());

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable String id,
            @Valid @RequestBody UpdateCourseRequest request
    ) {

        log.info("PUT /courses/{} - Update course request", id);

        Course course = courseService.updateCourse(
                id,
                request.name(),
                request.description()
        );

        log.info("Course {} updated successfully", id);

        return ResponseEntity.ok(mapToCourseResponse(course));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {

        log.warn("DELETE /courses/{} - Delete course request", id);

    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {

        log.warn("DELETE /courses/{} - Delete course request", id);

        courseService.deleteCourse(id);

        log.info("Course {} deleted successfully", id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/code/{code}")
    public ResponseEntity<Void> deleteCourseByCode(@PathVariable String code) {

        log.warn("DELETE /courses/code/{} - Delete course request", code);

        courseService.deleteCourseByCode(code);

        log.info("Course with code {} deleted successfully", code);

        return ResponseEntity.noContent().build();
    }

    private CourseResponse mapToCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCode(),
                course.getName(),
                course.getDescription(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getCreatedBy()
        );
    }

    public record UpdateCourseRequest(
            String name,
            String description
    ) {
    }

     

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Void> enrollInCourse(
            Authentication authentication,
            @PathVariable String courseId
    ) {
        String userId = authentication.getName();

        log.info("POST /courses/{}/enroll - Enrollment request by user {}", courseId, userId);

        courseService.findById(courseId);

        if (enrollmentRepository.existsByUserIdAndCourseIdAndActiveTrue(userId, courseId)) {
            log.warn("User {} attempted duplicate enrollment in course {}", userId, courseId);
            throw new IllegalArgumentException("You are already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment(userId, courseId);
        enrollmentRepository.save(enrollment);

        log.info("User {} enrolled successfully in course {}", userId, courseId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("Bad request: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}