/*package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.repositories.CourseRepository;
import com.grupo.learningmore.repositories.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private UUID courseId;
    private UUID professorId;

    @BeforeEach
    public void setUp() {
        resourceRepository.deleteAll();
        courseRepository.deleteAll();

        professorId = UUID.randomUUID();
        Course course = courseRepository.save(new Course("CS101", "Computer Science", "Introduction", professorId));
        courseId = course.getId();
    }

    @Test
    public void testUploadResourceSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lecture1.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "PDF content here".getBytes()
        );

        mockMvc.perform(multipart("/api/courses/" + courseId + "/resources")
                        .file(file)
                        .principal(() -> professorId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filename").value("lecture1.pdf"))
                .andExpect(jsonPath("$.contentType").value(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(jsonPath("$.courseId").value(courseId.toString()))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void testUploadResourceEmptyFileFails() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/courses/" + courseId + "/resources")
                        .file(emptyFile)
                        .principal(() -> professorId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUploadResourceToNonExistentCourseFails() throws Exception {
        UUID nonExistentCourseId = UUID.randomUUID();
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test content".getBytes()
        );

        mockMvc.perform(multipart("/api/courses/" + nonExistentCourseId + "/resources")
                        .file(file)
                        .principal(() -> professorId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetResourcesByCourse() throws Exception {
        // Upload multiple resources
        resourceRepository.save(new Resource(courseId, "lecture1.pdf", "/path/lecture1.pdf", 1024L, "application/pdf", professorId));
        resourceRepository.save(new Resource(courseId, "lecture2.pdf", "/path/lecture2.pdf", 2048L, "application/pdf", professorId));

        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].filename", hasItems("lecture1.pdf", "lecture2.pdf")));
    }

    @Test
    public void testGetResourceById() throws Exception {
        Resource resource = resourceRepository.save(
                new Resource(courseId, "notes.txt", "/path/notes.txt", 512L, "text/plain", professorId)
        );

        mockMvc.perform(get("/api/courses/" + courseId + "/resources/" + resource.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("notes.txt"))
                .andExpect(jsonPath("$.fileSize").value(512))
                .andExpect(jsonPath("$.contentType").value("text/plain"));
    }

    @Test
    public void testGetResourceFromWrongCourseFails() throws Exception {
        UUID otherCourseId = UUID.randomUUID();
        Resource resource = resourceRepository.save(
                new Resource(courseId, "file.txt", "/path/file.txt", 256L, "text/plain", professorId)
        );

        // Try to access resource from different course
        mockMvc.perform(get("/api/courses/" + otherCourseId + "/resources/" + resource.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteResource() throws Exception {
        Resource resource = resourceRepository.save(
                new Resource(courseId, "delete-me.pdf", "/path/delete-me.pdf", 1024L, "application/pdf", professorId)
        );

        mockMvc.perform(delete("/api/courses/" + courseId + "/resources/" + resource.getId())
                        .principal(() -> professorId.toString()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/courses/" + courseId + "/resources/" + resource.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEmptyResourcesList() throws Exception {
        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
} */
