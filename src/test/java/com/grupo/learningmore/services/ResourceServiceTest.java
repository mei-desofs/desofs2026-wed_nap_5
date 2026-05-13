package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.repositories.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.File;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private ResourceService resourceService;

    private UUID courseId;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        courseId = UUID.randomUUID();
        userId = UUID.randomUUID();

        ReflectionTestUtils.setField(resourceService, "uploadDir", "uploads");
        new File("uploads").mkdirs();

    }

    @Test
    public void testUploadResourceSuccess() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lecture.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        Course course = new Course("CS101", "CS", "Intro", userId);
        course.setId(courseId);
        when(courseService.findById(courseId)).thenReturn(course);

        Resource savedResource = new Resource(courseId, "lecture.pdf", "/uploads/uuid_lecture.pdf", 11L, "application/pdf", userId);
        savedResource.setId(UUID.randomUUID());
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);

        // Act
        Resource result = resourceService.uploadResource(courseId, file, userId);

        // Assert
        assertNotNull(result);
        assertEquals("lecture.pdf", result.getFilename());
        assertEquals("application/pdf", result.getContentType());
        verify(courseService).findById(courseId);
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    public void testUploadResourceEmptyFileThrowsException() throws IOException {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        Course course = new Course("CS101", "CS", "Intro", userId);
        course.setId(courseId);
        when(courseService.findById(courseId)).thenReturn(course);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            resourceService.uploadResource(courseId, emptyFile, userId)
        );

        verify(resourceRepository, never()).save(any());
    }

    @Test
    public void testUploadResourceNonExistentCourseFails() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lecture.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(courseService.findById(courseId)).thenThrow(new IllegalArgumentException("Course not found"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            resourceService.uploadResource(courseId, file, userId)
        );

        verify(resourceRepository, never()).save(any());
    }

    @Test
    public void testFindByIdSuccess() {
        // Arrange
        UUID resourceId = UUID.randomUUID();
        Resource expectedResource = new Resource(courseId, "file.txt", "/path", 100L, "text/plain", userId);
        expectedResource.setId(resourceId);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(expectedResource));

        // Act
        Resource result = resourceService.findById(resourceId);

        // Assert
        assertNotNull(result);
        assertEquals("file.txt", result.getFilename());
        verify(resourceRepository).findById(resourceId);
    }

    @Test
    public void testFindByIdNotFoundThrowsException() {
        // Arrange
        UUID resourceId = UUID.randomUUID();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceService.findById(resourceId));
        verify(resourceRepository).findById(resourceId);
    }
}
