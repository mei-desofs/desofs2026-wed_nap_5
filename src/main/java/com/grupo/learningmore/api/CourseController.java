package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.enrollment.Enrollment;
import com.grupo.learningmore.dto.request.CreateCourseRequest;
import com.grupo.learningmore.dto.request.UpdateCourseRequest;
import com.grupo.learningmore.dto.response.CourseResponse;
import com.grupo.learningmore.services.CourseService;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

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
        UUID userId = UUID.fromString(authentication.getName());

        Course course = courseService.createCourse(
                request.code(),
                request.name(),
                request.description(),
                userId
        );

        CourseResponse response = mapToCourseResponse(course);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

     
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable UUID id) {
        Course course = courseService.findById(id);
        return ResponseEntity.ok(mapToCourseResponse(course));
    }

     
    
    @GetMapping("/code/{code}")
    public ResponseEntity<CourseResponse> getCourseByCode(@PathVariable String code) {
        Course course = courseService.findByCode(code);
        return ResponseEntity.ok(mapToCourseResponse(course));
    }

    
    @GetMapping  
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<Course> courses = courseService.findAll();
        List<CourseResponse> responses = courses.stream()
                .map(this::mapToCourseResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        Course course = courseService.updateCourse(
                id,
                request.name(),
                request.description()
        );

        return ResponseEntity.ok(mapToCourseResponse(course));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/code/{code}")
    public ResponseEntity<Void> deleteCourseByCode(@PathVariable String code) {
        courseService.deleteCourseByCode(code);
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
     

    @PreAuthorize("hasRole('STUDENT')and hasRole('ADMIN')")
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Void> enrollInCourse(
            Authentication authentication, 
            @PathVariable UUID courseId
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        
        courseService.findById(courseId);
        
        if (enrollmentRepository.existsByUserIdAndCourseIdAndActiveTrue(userId, courseId)) {
            throw new IllegalArgumentException("You are already enrolled in this course.");
        }
        
        Enrollment enrollment = new Enrollment(userId, courseId);
        enrollmentRepository.save(enrollment);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
