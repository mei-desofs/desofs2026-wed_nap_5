package com.grupo.learningmore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.dto.request.CreateAssignmentRequest;
import com.grupo.learningmore.dto.request.UpdateAssignmentRequest;
import com.grupo.learningmore.repositories.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Garante isolamento perfeito por transação em cada execução de teste
public class AssignmentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String professorId;
    private String studentId;
    private String courseId;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // Limpeza rigorosa respeitando chaves estrangeiras
        chatMessageRepository.deleteAll();
        enrollmentRepository.deleteAll();
        chatRoomRepository.deleteAll();
        assignmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        
        // Sincroniza o estado atual com a BD imediatamente
        entityManager.flush();

        // Massa de dados limpa
        User professor = new User("Dr. Professor", "prof@test.com", passwordEncoder.encode("password123"), UserRole.PROFESSOR);
        User savedProfessor = userRepository.save(professor);
        professorId = savedProfessor.getId();

        User student = new User("Student User", "student@test.com", passwordEncoder.encode("password123"), UserRole.STUDENT);
        User savedStudent = userRepository.save(student);
        studentId = savedStudent.getId();

        Course course = new Course("Cybersecurity", "CRS-001", "Advanced security course", professorId);
        Course savedCourse = courseRepository.save(course);
        courseId = savedCourse.getId();
        
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void testCreateAssignmentSuccessful() throws Exception {
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                "Secure API Implementation",
                "Implement a REST API with proper authentication",
                LocalDateTime.now().plusDays(7)
        );

        mockMvc.perform(post("/api/courses/" + courseId + "/assignments")
                        .with(user("prof@test.com").roles("PROFESSOR"))
                        .with(csrf())                
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Secure API Implementation"))
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void testCreateAssignmentInvalidDeadline() throws Exception {
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                "Secure API Implementation",
                "Implement a REST API with proper authentication",
                LocalDateTime.now().minusDays(1)
        );

        mockMvc.perform(post("/api/courses/" + courseId + "/assignments")
                        .with(user("prof@test.com").roles("PROFESSOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAssignmentsByCourseProfessor() throws Exception {
        Assignment assignment = new Assignment(
                "Project 1",
                "Description",
                LocalDateTime.now().plusDays(5),
                courseId,
                professorId
        );
        // Deixamos o ID seguro gerado pelo próprio construtor da Entidade
        assignmentRepository.saveAndFlush(assignment);
        entityManager.clear();

        mockMvc.perform(get("/api/courses/" + courseId + "/assignments")
                        .with(user("prof@test.com").roles("PROFESSOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Project 1"));
    }

    @Test
    public void testGetAssignmentsByStudent() throws Exception {
        Assignment assignment = new Assignment(
                "Project 1",
                "Description",
                LocalDateTime.now().plusDays(5),
                courseId,
                professorId
        );
        assignmentRepository.saveAndFlush(assignment);
        entityManager.clear();

        mockMvc.perform(get("/api/courses/" + courseId + "/assignments")
                        .with(user("student@test.com").roles("STUDENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testUpdateAssignmentForbiddenOtherProfessor() throws Exception {
        User otherProf = new User("Other Prof", "other@test.com", passwordEncoder.encode("pass"), UserRole.PROFESSOR);
        otherProf = userRepository.saveAndFlush(otherProf);

        Assignment assignment = new Assignment(
                "Project 1",
                "Description",
                LocalDateTime.now().plusDays(5),
                courseId,
                otherProf.getId()
        );
        Assignment savedAssignment = assignmentRepository.saveAndFlush(assignment);
        entityManager.clear();

        UpdateAssignmentRequest updateRequest = new UpdateAssignmentRequest(
                "Updated title",
                "Updated desc",
                LocalDateTime.now().plusDays(10)
        );

        mockMvc.perform(put("/api/courses/" + courseId + "/assignments/" + savedAssignment.getId())
                        .with(user("prof@test.com").roles("PROFESSOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteAssignmentSuccess() throws Exception {
        Assignment assignment = new Assignment(
                "Project 1",
                "Description",
                LocalDateTime.now().plusDays(5),
                courseId,
                professorId
        );
        Assignment savedAssignment = assignmentRepository.saveAndFlush(assignment);
        entityManager.clear();

        mockMvc.perform(delete("/api/courses/" + courseId + "/assignments/" + savedAssignment.getId())
                        .with(user("prof@test.com").roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}