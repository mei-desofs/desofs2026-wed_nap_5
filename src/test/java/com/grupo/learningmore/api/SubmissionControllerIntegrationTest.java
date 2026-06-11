package com.grupo.learningmore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.enrollment.Enrollment;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.dto.request.GradeSubmissionRequest;
import com.grupo.learningmore.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SubmissionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UUID professorId;
    private UUID studentId;
    private UUID courseId;
    private UUID assignmentId;

    @BeforeEach
    public void clean() {     
        chatMessageRepository.deleteAll();
        enrollmentRepository.deleteAll();
        chatRoomRepository.deleteAll();
        userRepository.deleteAll();
        courseRepository.deleteAll();
        }
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        submissionRepository.deleteAll();
        assignmentRepository.deleteAll();
        enrollmentRepository.deleteAll();
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

        Enrollment enrollment = new Enrollment(studentId, courseId);
        enrollmentRepository.save(enrollment);

        Assignment assignment = new Assignment(
                "Secure API Project",
                "Implement secure API",
                LocalDateTime.now().plusDays(7),
                courseId,
                professorId
        );
        Assignment savedAssignment = assignmentRepository.save(assignment);
        assignmentId = savedAssignment.getId();
    }

    @Test
    public void testSubmitAssignmentSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "solution.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/assignments/" + assignmentId + "/submissions")
                        .file(file)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(studentId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void testSubmitAssignmentExpiredDeadline() throws Exception {
        Assignment expiredAssignment = new Assignment(
                "Old Project",
                "Description",
                LocalDateTime.now().minusDays(1),
                courseId,
                professorId
        );
        Assignment saved = assignmentRepository.save(expiredAssignment);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "solution.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/assignments/" + saved.getId() + "/submissions")
                        .file(file)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testSubmitAssignmentDuplicateFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "solution.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/assignments/" + assignmentId + "/submissions")
                        .file(file)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().isCreated());

        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "solution2.pdf",
                "application/pdf",
                "PDF content 2".getBytes()
        );

        mockMvc.perform(multipart("/api/assignments/" + assignmentId + "/submissions")
                        .file(file2)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetSubmissionsByAssignmentProfessor() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "solution.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/assignments/" + assignmentId + "/submissions")
                        .file(file)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/assignments/" + assignmentId + "/submissions")
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].userId").value(studentId.toString()));
    }

        @Test
        public void testGetSubmissionsByAssignmentStudentForbidden() throws Exception {
                mockMvc.perform(get("/api/assignments/" + assignmentId + "/submissions")
                                                .with(user(studentId.toString()).roles("STUDENT"))
                                                .with(csrf()))
                                .andExpect(status().isForbidden());
        }

    @Test
    public void testGradeSubmissionSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "solution.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        var submitResponse = mockMvc.perform(multipart("/api/assignments/" + assignmentId + "/submissions")
                        .file(file)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andReturn();

        String submissionId = objectMapper.readTree(submitResponse.getResponse().getContentAsString()).get("id").asText();

        GradeSubmissionRequest gradeRequest = new GradeSubmissionRequest(
                new BigDecimal("18.50"),
                "Excellent work, very secure implementation"
        );

        mockMvc.perform(put("/api/submissions/" + submissionId + "/grade")
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(18.50))
                .andExpect(jsonPath("$.status").value("GRADED"));
    }

    @Test
    public void testGradeSubmissionInvalidGrade() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "solution.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        var submitResponse = mockMvc.perform(multipart("/api/assignments/" + assignmentId + "/submissions")
                        .file(file)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andReturn();

        String submissionId = objectMapper.readTree(submitResponse.getResponse().getContentAsString()).get("id").asText();

        GradeSubmissionRequest gradeRequest = new GradeSubmissionRequest(
                new BigDecimal("150"),
                "Invalid grade"
        );

        mockMvc.perform(put("/api/submissions/" + submissionId + "/grade")
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGradeSubmissionUnauthorizedStudent() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "solution.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        var submitResponse = mockMvc.perform(multipart("/api/assignments/" + assignmentId + "/submissions")
                        .file(file)
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andReturn();

        String submissionId = objectMapper.readTree(submitResponse.getResponse().getContentAsString()).get("id").asText();

        GradeSubmissionRequest gradeRequest = new GradeSubmissionRequest(
                new BigDecimal("18.50"),
                "Grading from student"
        );

        mockMvc.perform(put("/api/submissions/" + submissionId + "/grade")
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isForbidden());
    }
}
