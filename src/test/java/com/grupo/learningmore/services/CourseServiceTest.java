package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        when(courseRepository.existsByCode("CS101")).thenReturn(false);

        when(courseRepository.save(any(Course.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Course result = courseService.createCourse(
                "CS101",
                "Computer Science",
                "Intro to CS",
                professorId
        );

        assertNotNull(result);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(captor.capture());

        Course saved = captor.getValue();

        assertEquals("CS101", saved.getCode());
        assertEquals("Computer Science", saved.getName());
        assertEquals(professorId, saved.getCreatedBy());
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


    @Test
    public void testDeleteCourseByCodeSuccess() {
        // Arrange
        String courseCode = "CS101";
        Course expectedCourse = new Course(courseCode, "Computer Science", "Intro", professorId);
        UUID courseId = UUID.randomUUID();
        expectedCourse.setId(courseId); // O ID que o deleteById vai receber

        // Simulamos o findByCode que o service invoca internamente
        when(courseRepository.findByCode(courseCode)).thenReturn(Optional.of(expectedCourse));

        // Act
        courseService.deleteCourseByCode(courseCode);

        // Assert
        verify(courseRepository).findByCode(courseCode);
        
         verify(courseRepository).deleteById(courseId); 
    }

    @Test
    public void testDeleteCourseByCodeNotFoundThrowsException() {
        // Arrange
        String invalidCode = "UNKNOWN";
        when(courseRepository.findByCode(invalidCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> courseService.deleteCourseByCode(invalidCode));
        verify(courseRepository).findByCode(invalidCode);
        verify(courseRepository, never()).delete(any());
    }

    
}
