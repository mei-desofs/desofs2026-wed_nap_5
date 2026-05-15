package com.grupo.learningmore.api;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
public class ResourceControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private MockMvc mockMvc;
    private UUID courseId;
    private UUID professorId;

    @BeforeEach
    public void setUp() {
        // Configura o MockMvc com suporte a segurança, igual ao CourseIntegrationTest
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        resourceRepository.deleteAll();
        courseRepository.deleteAll();

        professorId = UUID.randomUUID();
        // Criamos um curso base para associar os recursos nos testes
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
                        .with(user(professorId.toString()).roles("PROFESSOR"))) // Correção da segurança
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filename").value("lecture1.pdf"))
                .andExpect(jsonPath("$.courseId").value(courseId.toString()));
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
                        .with(user(professorId.toString()).roles("PROFESSOR")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetResourcesByCourse() throws Exception {
        resourceRepository.save(new Resource(courseId, "lecture1.pdf", "/path/1", 1024L, "application/pdf", professorId));

        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .with(user(professorId.toString()).roles("PROFESSOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGetResourceById() throws Exception {
        Resource resource = resourceRepository.save(
                new Resource(courseId, "notes.txt", "/path/notes.txt", 512L, "text/plain", professorId)
        );

        // Usamos o professorId real em vez de um aleatório ou "student"
        mockMvc.perform(get("/api/courses/" + courseId + "/resources/" + resource.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("notes.txt"));
    }

    @Test
    public void testDeleteResource() throws Exception {
        Resource resource = resourceRepository.save(
                new Resource(courseId, "delete-me.pdf", "/path/delete.pdf", 1024L, "application/pdf", professorId)
        );

        mockMvc.perform(delete("/api/courses/" + courseId + "/resources/" + resource.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR")))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUploadResourceToNonExistentCourseFails() throws Exception {
        UUID nonExistentCourseId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/api/courses/" + nonExistentCourseId + "/resources")
                        .file(file)
                        .with(user(professorId.toString()).roles("PROFESSOR")))
                .andExpect(status().isNotFound());
    }
}