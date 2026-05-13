/*package com.grupo.learningmore.api;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.dto.Request.CreateCourseRequest;
import com.grupo.learningmore.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    private UUID professorId;

    @BeforeEach
    public void setUp() {
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(() -> professorId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CS101"))
                .andExpect(jsonPath("$.name").value("Introduction to Computer Science"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void testCreateCourseDuplicateCodeFails() throws Exception {
        // Create first course
        courseRepository.save(new Course("MATH101", "Calculus", "Advanced math", professorId));

        String requestBody = """
                {
                    "code": "MATH101",
                    "name": "Different Name",
                    "description": "Different description"
                }
                """;

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(() -> professorId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCourseById() throws Exception {
        Course course = courseRepository.save(new Course("PHYS101", "Physics", "Mechanics", professorId));

        mockMvc.perform(get("/api/courses/" + course.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PHYS101"))
                .andExpect(jsonPath("$.name").value("Physics"));
    }

    @Test
    public void testGetCourseByCode() throws Exception {
        Course course = courseRepository.save(new Course("BIO101", "Biology", "Life sciences", professorId));

        mockMvc.perform(get("/api/courses/code/BIO101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BIO101"))
                .andExpect(jsonPath("$.id").value(course.getId().toString()));
    }

    @Test
    public void testGetAllCourses() throws Exception {
        courseRepository.save(new Course("ENG101", "English", "Literature", professorId));
        courseRepository.save(new Course("HIST101", "History", "World history", professorId));

        mockMvc.perform(get("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].code", hasItems("ENG101", "HIST101")));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Modern Art History"))
                .andExpect(jsonPath("$.description").value("20th century art movements"));
    }

    @Test
    public void testDeleteCourse() throws Exception {
        Course course = courseRepository.save(new Course("MUSIC101", "Music", "Theory basics", professorId));

        mockMvc.perform(delete("/api/courses/" + course.getId())
                        .principal(() -> UUID.randomUUID().toString())) // ADMIN user
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/courses/" + course.getId()))
                .andExpect(status().isNotFound());
    }
}*/
