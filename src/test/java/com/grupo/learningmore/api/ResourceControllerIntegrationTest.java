package com.grupo.learningmore.api;

 
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.domain.enrollment.Enrollment;
 
import com.grupo.learningmore.repositories.CourseRepository;
import com.grupo.learningmore.repositories.ResourceRepository;
import com.grupo.learningmore.repositories.EnrollmentRepository; 
import com.grupo.learningmore.repositories.ChatRoomRepository;
import com.grupo.learningmore.repositories.UserRepository;
import com.grupo.learningmore.repositories.ChatMessageRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
public class ResourceControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository; 

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private MockMvc mockMvc;
    private UUID courseId;
    private UUID professorId;

    
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

        enrollmentRepository.deleteAll();   
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
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
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
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetResourcesByCourse() throws Exception {
        resourceRepository.save(new Resource(courseId, "lecture1.pdf", "/path/1", 1024L, "application/pdf", professorId));

        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGetResourceById() throws Exception {
        Resource resource = resourceRepository.save(
                new Resource(courseId, "notes.txt", "/path/notes.txt", 512L, "text/plain", professorId)
        );

        mockMvc.perform(get("/api/courses/" + courseId + "/resources/" + resource.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("notes.txt"));
    }

    @Test
    public void testDeleteResource() throws Exception {
        // 1. Define a temporary fake path where the service expects the file to live
        java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads", courseId.toString());
        java.nio.file.Files.createDirectories(uploadDir); // Create the course directory if missing
        
        java.nio.file.Path fakeFilePath = uploadDir.resolve("delete-me.pdf");
        
        // 2. PHYSICALLY create the dummy file on disk so Files.exists() returns true!
        java.nio.file.Files.write(fakeFilePath, "Dummy PDF data".getBytes());

        // 3. Save the resource metadata entity pointing to this physical file path
        Resource resource = resourceRepository.save(
                new Resource(courseId, "delete-me.pdf", fakeFilePath.toString(), 1024L, "application/pdf", professorId)
        );

     // 4. Perform the delete request
         mockMvc.perform(delete("/api/courses/" + courseId + "/resources/" + resource.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

    // 5. Clean up safety check: Verify the service actually deleted the physical file from disk
    org.junit.jupiter.api.Assertions.assertFalse(java.nio.file.Files.exists(fakeFilePath));
 }


    @Test
    public void testDeleteResource_WhenPhysicalFileIsMissingFromDisk() throws Exception {
        
      String nonExistentFilePath = "uploads/" + courseId.toString() + "/ghost-file.pdf";

      Resource resource = resourceRepository.save(
        new Resource(courseId, "ghost-file.pdf", nonExistentFilePath, 1024L, "application/pdf", professorId)
        );

         mockMvc.perform(delete("/api/courses/" + courseId + "/resources/" + resource.getId())
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isNoContent()); // Should still succeed with 204!

         org.junit.jupiter.api.Assertions.assertFalse(resourceRepository.existsById(resource.getId()));
 }     

    @Test
    public void testUploadResourceToNonExistentCourseFails() throws Exception {
        UUID nonExistentCourseId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/api/courses/" + nonExistentCourseId + "/resources")
                        .file(file)
                        .with(user(professorId.toString()).roles("PROFESSOR"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // ENROLLMENT & RESOURCE ACCESS TESTS (CLEANED & DYNAMIC)
    // =========================================================================

    @Test
    public void testGetResourcesAsAdmin_BypassesEnrollment() throws Exception {
        UUID adminId = UUID.randomUUID();

        // Admin checks don't read from the enrollment table
        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .with(user(adminId.toString()).roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk()); 
    }

    @Test
    public void testGetResourcesAsEnrolledStudent_Success() throws Exception {
        UUID studentId = UUID.randomUUID();

        // Physically save an active enrollment in your test database context
        enrollmentRepository.save(new Enrollment(studentId, courseId));
        resourceRepository.save(new Resource(courseId, "student-view.pdf", "/path/2", 1024L, "application/pdf", professorId));

        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .with(user(studentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGetResourcesAsUnenrolledStudent_ReturnsForbidden() throws Exception {
        UUID maliciousStudentId = UUID.randomUUID();

        // We specifically DO NOT add an enrollment mapping for this user ID
        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .with(user(maliciousStudentId.toString()).roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().isForbidden()); 
    }

    @Test
    public void testGetResourcesWithMalformedUserUuid_ReturnsUnauthorized() throws Exception {
        // Simulates an unparseable authentication name (e.g. not a valid UUID format string)
        mockMvc.perform(get("/api/courses/" + courseId + "/resources")
                        .with(user("malformed-string-id-abc").roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); 
    }


   
}