package com.grupo.learningmore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.dto.request.CreateAssignmentRequest;
import com.grupo.learningmore.repositories.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
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
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)  
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

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String professorId;
    private String studentId;
    private String courseId;

    /*@BeforeEach
    public void clean() {     
        assignmentRepository.deleteAll();
        chatMessageRepository.deleteAll();
        enrollmentRepository.deleteAll();
        chatRoomRepository.deleteAll();
        userRepository.deleteAll();
        courseRepository.deleteAll();
        }*/
    
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        assignmentRepository.deleteAll();
        chatMessageRepository.deleteAll();
        enrollmentRepository.deleteAll();
        chatRoomRepository.deleteAll();
        courseRepository.deleteAll();  
        userRepository.deleteAll();

        User professor = new User("Dr. Professor", "prof@test.com", passwordEncoder.encode("password123"), UserRole.PROFESSOR);
        User savedProfessor = userRepository.save(professor);
        professorId = savedProfessor.getId();

        User student = new User("Student User", "student@test.com", passwordEncoder.encode("password123"), UserRole.STUDENT);
        User savedStudent = userRepository.save(student);
        studentId = savedStudent.getId();

        Course course = new Course("CS-001", "Cybersecurity", "Advanced security course", professorId);
        Course savedCourse = courseRepository.save(course);
        courseId = savedCourse.getId();
    }

    @Test
    public void testCreateAssignmentSuccessful() throws Exception {
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                "Secure API Implementation",
                "Implement a REST API with proper authentication",
                LocalDateTime.now().plusDays(7)
        );

        mockMvc.perform(post("/api/courses/" + courseId + "/assignments")
                        .with(user(professorId).roles("ADMIN"))
                        .with(csrf())                 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Secure API Implementation"))
                //.andExpect(jsonPath("$.courseId").value(courseId.toString()))
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
                        .with(user(professorId.toString()).roles("PROFESSOR"))
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
        assignmentRepository.save(assignment);

        mockMvc.perform(get("/api/courses/" + courseId + "/assignments")
                        .with(user(professorId.toString()).roles("PROFESSOR"))
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
        assignmentRepository.save(assignment);

        mockMvc.perform(get("/api/courses/" + courseId + "/assignments")
                        .with(user(studentId).roles("STUDENT"))
                        .with(authentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(studentId, null, org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_STUDENT"))))
                        .header("Authorization", "Bearer mock-test-token-to-avoid-500-error")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testUpdateAssignmentForbiddenOtherProfessor() throws Exception {
        User otherProf = new User("Other Prof", "other@test.com", passwordEncoder.encode("pass"), UserRole.PROFESSOR);
        otherProf = userRepository.save(otherProf);

        Assignment assignment = new Assignment(
                "Project 1",
                "Description",
                LocalDateTime.now().plusDays(5),
                courseId,
                otherProf.getId()
        );
        assignment = assignmentRepository.save(assignment);

        var updateRequest = new com.grupo.learningmore.dto.request.UpdateAssignmentRequest(
                "Updated title",
                "Updated desc",
                LocalDateTime.now().plusDays(10)
        );

        mockMvc.perform(put("/api/courses/" + courseId + "/assignments/" + assignment.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR"))
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
        assignment = assignmentRepository.save(assignment);

        mockMvc.perform(delete("/api/courses/" + courseId + "/assignments/" + assignment.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
