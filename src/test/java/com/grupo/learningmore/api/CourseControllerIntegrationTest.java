package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class CourseControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CourseRepository courseRepository;

    private MockMvc mockMvc;
    private UUID professorId;

    @BeforeEach
    public void setUp() {
        // IMPORTANTE: Adicionado .apply(springSecurity()) para garantir que as Roles sejam validadas
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        courseRepository.deleteAll();
        professorId = UUID.randomUUID();
    }

    @Test
    public void testCreateCourseSuccess() throws Exception {
        String requestBody = """
                {
                    "code": "CS101",
                    "name": "Introduction to Computer Science",
                    "description": "Basic CS concepts"
                }
                """;

        mockMvc.perform(post("/api/courses")
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CS101"))
                .andExpect(jsonPath("$.name").value("Introduction to Computer Science"));
    }

    @Test
    public void testCreateCourseDuplicateCodeFails() throws Exception {
        courseRepository.save(new Course("MATH101", "Calculus", "Advanced math", professorId));

        String requestBody = """
                {
                    "code": "MATH101",
                    "name": "Different Name",
                    "description": "Different description"
                }
                """;

        mockMvc.perform(post("/api/courses")
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCourseById() throws Exception {
        Course course = courseRepository.save(new Course("PHYS101", "Physics", "Mechanics", professorId));

        mockMvc.perform(get("/api/courses/" + course.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PHYS101"));
    }

    @Test
    public void testGetCourseByCode() throws Exception {
        courseRepository.save(new Course("BIO101", "Biology", "Life sciences", professorId));

        mockMvc.perform(get("/api/courses/code/BIO101")
                        .with(user("testUser").roles("STUDENT")) // Adicionado para evitar o 403
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BIO101"));
    }

    @Test
    public void testGetAllCourses() throws Exception {
        courseRepository.save(new Course("ENG101", "English", "Literature", professorId));
        courseRepository.save(new Course("HIST101", "History", "World history", professorId));

        mockMvc.perform(get("/api/courses")
                        .with(user("testUser").roles("STUDENT")) // Adicionado para evitar o 403
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        Course course = courseRepository.save(new Course("ART101", "Art History", "Renaissance", professorId));

        String updateBody = """
                {
                    "name": "Modern Art History",
                    "description": "20th century art movements"
                }
                """;

        mockMvc.perform(put("/api/courses/" + course.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Modern Art History"));
    }

    @Test
    public void testDeleteCourse() throws Exception {
        Course course = courseRepository.save(new Course("MUSIC101", "Music", "Theory basics", professorId));

        // Usei ROLE ADMIN aqui pois o seu código anterior indicava que Delete exige ADMIN
        mockMvc.perform(delete("/api/courses/" + course.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }
}