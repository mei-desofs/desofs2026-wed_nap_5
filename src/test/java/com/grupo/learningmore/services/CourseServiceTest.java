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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private String professorId;

    @BeforeEach
    public void setUp() {
        professorId = "professor-" + System.nanoTime();
    }

    @Test
    public void testCreateCourseSuccess() {
        when(courseRepository.existsByCode(anyString())).thenReturn(false);

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

        assertNotNull(saved.getCode());
        assertEquals("Computer Science", saved.getName());
        assertEquals(professorId, saved.getCreatedBy());
    }

    @Test
    public void testCreateCourseDuplicateCodeThrowsException() {
        when(courseRepository.existsByCode("MATH101")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                courseService.createCourse("MATH101", "Mathematics", "Calc 1", professorId)
        );

        verify(courseRepository).existsByCode("MATH101");
        verify(courseRepository, never()).save(any());
    }

    @Test
    public void testFindByIdSuccess() {
        String courseId = "course-" + System.nanoTime();
        Course expectedCourse = new Course("Physics", "Physics", "Mechanics", professorId);
        expectedCourse.setId(courseId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(expectedCourse));

        Course result = courseService.findById(courseId);

        assertNotNull(result);
        verify(courseRepository).findById(courseId);
    }

    @Test
    public void testFindByIdNotFoundThrowsException() {
        String courseId = "course-" + System.nanoTime();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> courseService.findById(courseId));
        verify(courseRepository).findById(courseId);
    }

    @Test
    public void testFindByCodeSuccess() {
        Course expectedCourse = new Course("Biology", "Biology", "Life Sciences", professorId);
        when(courseRepository.findByCode("BIO101")).thenReturn(Optional.of(expectedCourse));

        Course result = courseService.findByCode("BIO101");

        assertNotNull(result);
        verify(courseRepository).findByCode("BIO101");
    }

    @Test
    public void testFindByCodeNotFoundThrowsException() {
        when(courseRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> courseService.findByCode("INVALID"));
        verify(courseRepository).findByCode("INVALID");
    }

    @Test
    public void testDeleteCourseByCodeSuccess() {
        String courseCode = "CS101";
        Course expectedCourse = new Course("Computer Science", "CS101", "Intro", professorId);
        String courseId = "course-" + System.nanoTime();
        expectedCourse.setId(courseId);

        when(courseRepository.findByCode(courseCode)).thenReturn(Optional.of(expectedCourse));

        courseService.deleteCourseByCode(courseCode);

        verify(courseRepository).findByCode(courseCode);
        verify(courseRepository).deleteById(courseId); 
    }

    @Test
    public void testDeleteCourseByCodeNotFoundThrowsException() {
        String invalidCode = "UNKNOWN";
        when(courseRepository.findByCode(invalidCode)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> courseService.deleteCourseByCode(invalidCode));
        verify(courseRepository).findByCode(invalidCode);
        verify(courseRepository, never()).delete(any());
    }

    @Test
    public void testFindAllCourses() {
        java.util.List<Course> courses = java.util.List.of(
            new Course("Science", "SCIENCE101", "Intro", professorId),
            new Course("Math", "MATH101", "Calc", professorId)
        );
        when(courseRepository.findAll()).thenReturn(courses);

        java.util.List<Course> result = courseService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository).findAll();
    }

    @Test
    public void testUpdateCourseSuccess() {
        String courseId = "course-" + System.nanoTime();
        Course existingCourse = new Course("Old Name", "OLD_CODE", "Old Desc", professorId);
        existingCourse.setId(courseId);
         
        LocalDateTime dataAntiga = LocalDateTime.now().minusDays(5);
        existingCourse.setUpdatedAt(dataAntiga);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Course updated = courseService.updateCourse(courseId, "New Name", "New Desc");

        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
        assertEquals("New Desc", updated.getDescription());
         
        assertTrue(updated.getUpdatedAt().isAfter(dataAntiga), 
                "O mutante sobreviveu! A data de atualização não foi modificada para o momento presente.");
        
        verify(courseRepository).save(existingCourse);
    }

    @Test
    public void testDeleteCourseSuccess() {
        String targetId = "course-" + System.nanoTime();
 
        courseService.deleteCourse(targetId);

        verify(courseRepository).deleteById(targetId);
    }
}