package com.grupo.learningmore.bootstrap;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.domain.enrollment.Enrollment;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.repositories.CourseRepository;
import com.grupo.learningmore.repositories.ResourceRepository;
import com.grupo.learningmore.repositories.EnrollmentRepository;
import com.grupo.learningmore.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ResourceRepository resourceRepository;

    public DataBootstrap(
            UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            ResourceRepository resourceRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only load bootstrap data if the database is empty
        if (userRepository.count() > 0) {
            System.out.println("Database already populated. Skipping bootstrap data.");
            return;
        }

        System.out.println("Loading bootstrap data...");

        // Create a professor
        User professor = new User("Dr. João Silva", "professor@learningmore.com", "password123", UserRole.PROFESSOR);
        User savedProfessor = userRepository.save(professor);
        System.out.println("Created Professor: " + savedProfessor.getEmail());

        // Create students
        User student1 = new User("Maria Santos", "student1@learningmore.com", "password123", UserRole.STUDENT);
        User savedStudent1 = userRepository.save(student1);
        System.out.println("Created Student 1: " + savedStudent1.getEmail());

        User student2 = new User("Pedro Oliveira", "student2@learningmore.com", "password123", UserRole.STUDENT);
        User savedStudent2 = userRepository.save(student2);
        System.out.println("Created Student 2: " + savedStudent2.getEmail());

        // Create an admin
        User admin = new User("Admin User", "admin@learningmore.com", "password123", UserRole.ADMIN);
        User savedAdmin = userRepository.save(admin);
        System.out.println("Created Admin: " + savedAdmin.getEmail());

        // Create courses
        Course course1 = new Course("CYBERSEC-001", "Cibersegurança Avançada", "Curso sobre princípios e práticas de cibersegurança", savedProfessor.getId());
        Course savedCourse1 = courseRepository.save(course1);
        System.out.println("Created Course 1: " + savedCourse1.getName());

        Course course2 = new Course("WEB-002", "Desenvolvimento Web Moderno", "Curso sobre desenvolvimento web com tecnologias modernas", savedProfessor.getId());
        Course savedCourse2 = courseRepository.save(course2);
        System.out.println("Created Course 2: " + savedCourse2.getName());

        // Enroll students in courses
        Enrollment enrollment1 = new Enrollment(savedStudent1.getId(), savedCourse1.getId());
        enrollmentRepository.save(enrollment1);
        System.out.println("Enrolled Maria in Cibersegurança");

        Enrollment enrollment2 = new Enrollment(savedStudent1.getId(), savedCourse2.getId());
        enrollmentRepository.save(enrollment2);
        System.out.println("Enrolled Maria in Web Development");

        Enrollment enrollment3 = new Enrollment(savedStudent2.getId(), savedCourse1.getId());
        enrollmentRepository.save(enrollment3);
        System.out.println("Enrolled Pedro in Cibersegurança");

        // Create resources for courses
        Resource resource1 = new Resource(
                savedCourse1.getId(),
                "Introducao_Ciberseguranca.pdf",
                "/uploads/Introducao_Ciberseguranca.pdf",
                2048000L,
                "application/pdf",
                savedProfessor.getId()
        );
        resourceRepository.save(resource1);
        System.out.println("Created Resource 1 for Cibersegurança");

        Resource resource2 = new Resource(
                savedCourse1.getId(),
                "Praticas_Segurancas.pdf",
                "/uploads/Praticas_Segurancas.pdf",
                1536000L,
                "application/pdf",
                savedProfessor.getId()
        );
        resourceRepository.save(resource2);
        System.out.println("Created Resource 2 for Cibersegurança");

        Resource resource3 = new Resource(
                savedCourse2.getId(),
                "HTML_CSS_Basico.pdf",
                "/uploads/HTML_CSS_Basico.pdf",
                3072000L,
                "application/pdf",
                savedProfessor.getId()
        );
        resourceRepository.save(resource3);
        System.out.println("Created Resource 1 for Web Development");

        Resource resource4 = new Resource(
                savedCourse2.getId(),
                "JavaScript_Avancado.pdf",
                "/uploads/JavaScript_Avancado.pdf",
                2560000L,
                "application/pdf",
                savedProfessor.getId()
        );
        resourceRepository.save(resource4);
        System.out.println("Created Resource 2 for Web Development");

        System.out.println("Bootstrap data loaded successfully!");
    }
}
