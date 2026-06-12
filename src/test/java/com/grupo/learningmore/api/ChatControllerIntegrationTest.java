package com.grupo.learningmore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo.learningmore.domain.chat.ChatMessage;
import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.enrollment.Enrollment;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.dto.request.SendMessageRequest;
import com.grupo.learningmore.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
 

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@ActiveProfiles("test")
public class ChatControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String  chatRoomId;
    private String  studentId;
    private String  professorId;

    @BeforeEach
    public void clean() {
        chatMessageRepository.deleteAll();
        enrollmentRepository.deleteAll();
        chatRoomRepository.deleteAll();
        userRepository.deleteAll();
        courseRepository.deleteAll();
        }


    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        objectMapper = new ObjectMapper();

        chatMessageRepository.deleteAll();
        chatRoomRepository.deleteAll();
        enrollmentRepository.deleteAll();
        userRepository.deleteAll();
        courseRepository.deleteAll();

        // users
        User student = new User(
                "Student",
                "student@test.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );

        User professor = new User(
                "Professor",
                "prof@test.com",
                passwordEncoder.encode("password123"),
                UserRole.PROFESSOR
        );

        studentId = userRepository.save(student).getId();
        professorId = userRepository.save(professor).getId();

        

        Course course = new Course(
                
                "Cybersecurity",
                 "CRS-001",
                "Advanced security course",
                professorId
        );

        Course savedCourse = courseRepository.save(course);

        ChatRoom room = new ChatRoom();
        room.setName("General Chat");
        room.setCourse(savedCourse);

        ChatRoom savedRoom = chatRoomRepository.save(room);
        chatRoomId = savedRoom.getId();

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(studentId);
        enrollment.setCourseId(savedCourse.getId());
        enrollment.setActive(true);
        enrollment.setEnrolledAt(LocalDateTime.now());

        enrollmentRepository.save(enrollment);
    }

    private RequestPostProcessor auth(String  userId, String role) {
        return user(userId.toString())
                .roles(role)
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void shouldSendMessageSuccessfully() throws Exception {

        SendMessageRequest request =
                new SendMessageRequest("Hello professor");

        mockMvc.perform(post("/api/chat/" + chatRoomId + "/messages")
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello professor"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldRejectWhenUserNotEnrolled() throws Exception {
        SendMessageRequest request = new SendMessageRequest("Trying to access chat");

        mockMvc.perform(post("/api/chat/" + chatRoomId + "/messages")
                        .with(auth(professorId, "PROFESSOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnMessagesOrdered() throws Exception {
        ChatMessage m1 = new ChatMessage();
        m1.setChatRoom(chatRoomRepository.findById(chatRoomId).get());
        m1.setContent("First message");
        m1.setSentAt(new Date());

        ChatMessage m2 = new ChatMessage();
        m2.setChatRoom(chatRoomRepository.findById(chatRoomId).get());
        m2.setContent("Second message");
        m2.setSentAt(new Date(System.currentTimeMillis() + 1000));

        chatMessageRepository.saveAll(List.of(m1, m2));

        mockMvc.perform(get("/api/chat/" + chatRoomId + "/messages")
                        .with(auth(studentId, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].content").value("First message"))
                .andExpect(jsonPath("$.content[1].content").value("Second message"));
    }

    @Test
    void shouldReturnErrorWhenChatRoomDoesNotExist() throws Exception {

       String randomId = "chat-room-inexistente-" + System.nanoTime();

        SendMessageRequest request = new SendMessageRequest("Hello");

        mockMvc.perform(post("/api/chat/" + randomId + "/messages")
                        .with(auth(studentId, "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
