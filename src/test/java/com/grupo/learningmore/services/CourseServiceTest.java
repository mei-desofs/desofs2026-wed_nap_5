package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private UUID professorId;

    @BeforeEach
    public void setUp() {
        professorId = UUID.randomUUID();
    }

    @Test
    public void testCreateCourseSuccess() {
        // Arrange
        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        Course expectedCourse = new Course("CS101", "Computer Science", "Intro to CS", professorId);
        when(courseRepository.save(any(Course.class))).thenReturn(expectedCourse);

        // Act
        Course result = courseService.createCourse("CS101", "Computer Science", "Intro to CS", professorId);

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getCode());
        assertEquals("Computer Science", result.getName());
        verify(courseRepository).existsByCode("CS101");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    public void testCreateCourseDuplicateCodeThrowsException() {
        // Arrange
        when(courseRepository.existsByCode("MATH101")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                courseService.createCourse("MATH101", "Mathematics", "Calc 1", professorId)
        );

        verify(courseRepository).existsByCode("MATH101");
        verify(courseRepository, never()).save(any());
    }

    @Test
    public void testFindByIdSuccess() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        Course expectedCourse = new Course("PHYS101", "Physics", "Mechanics", professorId);
        expectedCourse.setId(courseId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(expectedCourse));

        // Act
        Course result = courseService.findById(courseId);

        // Assert
        assertNotNull(result);
        assertEquals("PHYS101", result.getCode());
        verify(courseRepository).findById(courseId);
    }

    @Test
    public void testFindByIdNotFoundThrowsException() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> courseService.findById(courseId));
        verify(courseRepository).findById(courseId);
    }

    @Test
    public void testFindByCodeSuccess() {
        // Arrange
        Course expectedCourse = new Course("BIO101", "Biology", "Life Sciences", professorId);
        when(courseRepository.findByCode("BIO101")).thenReturn(Optional.of(expectedCourse));

        // Act
        Course result = courseService.findByCode("BIO101");

        // Assert
        assertNotNull(result);
        assertEquals("BIO101", result.getCode());
        verify(courseRepository).findByCode("BIO101");
    }

    @Test
    public void testFindByCodeNotFoundThrowsException() {
        // Arrange
        when(courseRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> courseService.findByCode("INVALID"));
        verify(courseRepository).findByCode("INVALID");
    }
}
