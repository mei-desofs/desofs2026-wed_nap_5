package com.grupo.learningmore.bootstrap;

import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.assignment.Submission;
import com.grupo.learningmore.domain.assignment.SubmissionStatus;
import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.domain.enrollment.Enrollment;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.domain.user.UserRole;
import com.grupo.learningmore.repositories.*;
import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.domain.chat.ChatMessage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class DataBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ResourceRepository resourceRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataBootstrap(
            UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            ResourceRepository resourceRepository,
            ChatRoomRepository chatRoomRepository,
            ChatMessageRepository chatMessageRepository,
            AssignmentRepository assigmentRepository,
            SubmissionRepository submissionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.resourceRepository = resourceRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.assignmentRepository = assigmentRepository;
        this.submissionRepository = submissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() > 0) {
            System.out.println("Database already populated. Skipping bootstrap data.");
            return;
        }

        System.out.println("Loading bootstrap data...");

        // Create professor
        User professor = new User(
                "Dr. John Smith",
                "professor@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.PROFESSOR
        );
        User savedProfessor = userRepository.save(professor);
        System.out.println("Created Professor: " + savedProfessor.getEmail());

// Create admin
        User admin = new User(
                "Admin User",
                "admin@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.ADMIN
        );
        User savedAdmin = userRepository.save(admin);
        System.out.println("Created Admin: " + savedAdmin.getEmail());

// Create students
        User student1 = new User(
                "Mary Johnson",
                "student1@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent1 = userRepository.save(student1);

        User student2 = new User(
                "Peter Brown",
                "student2@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent2 = userRepository.save(student2);

        User student3 = new User(
                "Emily Davis",
                "student3@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent3 = userRepository.save(student3);

        User student4 = new User(
                "Michael Wilson",
                "student4@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent4 = userRepository.save(student4);

        User student5 = new User(
                "Sophia Taylor",
                "student5@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent5 = userRepository.save(student5);

        User student6 = new User(
                "Daniel Anderson",
                "student6@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent6 = userRepository.save(student6);

        User student7 = new User(
                "Olivia Martinez",
                "student7@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent7 = userRepository.save(student7);

        User student8 = new User(
                "James Thomas",
                "student8@learningmore.com",
                passwordEncoder.encode("password123"),
                UserRole.STUDENT
        );
        User savedStudent8 = userRepository.save(student8);

        System.out.println("Created 8 students successfully");

        // Create courses
        Course course1 = new Course(
                "CYBERSEC-001",
                "Advanced Cybersecurity",
                "Covers vulnerability analysis, penetration testing, and secure system design.",
                savedProfessor.getId()
        );
        Course savedCourse1 = courseRepository.save(course1);

        Course course2 = new Course(
                "WEB-002",
                "Modern Web Development",
                "Frontend and backend development using modern web technologies.",
                savedProfessor.getId()
        );
        Course savedCourse2 = courseRepository.save(course2);

        Course course3 = new Course(
                "JAVA-003",
                "Java Backend Development",
                "Building REST APIs and backend systems using Spring Boot.",
                savedProfessor.getId()
        );
        Course savedCourse3 = courseRepository.save(course3);

        Course course4 = new Course(
                "DB-004",
                "Database Systems",
                "Relational databases, SQL optimization, and data modeling techniques.",
                savedProfessor.getId()
        );
        Course savedCourse4 = courseRepository.save(course4);

        Course course5 = new Course(
                "CLOUD-005",
                "Cloud Computing Fundamentals",
                "Introduction to cloud services, deployment models, and scalability.",
                savedProfessor.getId()
        );
        Course savedCourse5 = courseRepository.save(course5);

        Course course6 = new Course(
                "AI-006",
                "Introduction to Artificial Intelligence",
                "Basic concepts of AI, machine learning, and intelligent systems.",
                savedProfessor.getId()
        );
        Course savedCourse6 = courseRepository.save(course6);

        System.out.println("Created 6 courses successfully");


// =========================
// ENROLLMENTS
// =========================

        // STUDENT 1 - Mary Johnson
        enroll(savedStudent1.getId(), savedCourse1.getId());
        enroll(savedStudent1.getId(), savedCourse2.getId());
        enroll(savedStudent1.getId(), savedCourse3.getId());

        // STUDENT 2 - Peter Brown
        enroll(savedStudent2.getId(), savedCourse1.getId());
        enroll(savedStudent2.getId(), savedCourse4.getId());
        enroll(savedStudent2.getId(), savedCourse5.getId());

        // STUDENT 3 - Emily Davis
        enroll(savedStudent3.getId(), savedCourse2.getId());
        enroll(savedStudent3.getId(), savedCourse3.getId());
        enroll(savedStudent3.getId(), savedCourse6.getId());

        // STUDENT 4 - Michael Wilson
        enroll(savedStudent4.getId(), savedCourse1.getId());
        enroll(savedStudent4.getId(), savedCourse2.getId());
        enroll(savedStudent4.getId(), savedCourse5.getId());

        // STUDENT 5 - Sophia Taylor
        enroll(savedStudent5.getId(), savedCourse3.getId());
        enroll(savedStudent5.getId(), savedCourse4.getId());
        enroll(savedStudent5.getId(), savedCourse6.getId());

        // STUDENT 6 - Daniel Anderson
        enroll(savedStudent6.getId(), savedCourse1.getId());
        enroll(savedStudent6.getId(), savedCourse2.getId());
        enroll(savedStudent6.getId(), savedCourse3.getId());

        // STUDENT 7 - Olivia Martinez
        enroll(savedStudent7.getId(), savedCourse4.getId());
        enroll(savedStudent7.getId(), savedCourse5.getId());
        enroll(savedStudent7.getId(), savedCourse6.getId());

        // STUDENT 8 - James Thomas
        enroll(savedStudent8.getId(), savedCourse1.getId());
        enroll(savedStudent8.getId(), savedCourse3.getId());
        enroll(savedStudent8.getId(), savedCourse6.getId());

        System.out.println("Created enrollments for all students successfully");


        // =========================
        // CYBERSECURITY - COURSE 1
        // =========================
        resourceRepository.save(new Resource(
                savedCourse1.getId(),
                "Introduction_to_Cybersecurity.pdf",
                "/uploads/Introduction_to_Cybersecurity.pdf",
                2048000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse1.getId(),
                "Security_Best_Practices.pdf",
                "/uploads/Security_Best_Practices.pdf",
                1536000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse1.getId(),
                "OWASP_Top_10_Overview.pdf",
                "/uploads/OWASP_Top_10_Overview.pdf",
                3120000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse1.getId(),
                "Penetration_Testing_Guide.pdf",
                "/uploads/Penetration_Testing_Guide.pdf",
                4100000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse1.getId(),
                "Network_Security_Fundamentals.pdf",
                "/uploads/Network_Security_Fundamentals.pdf",
                2750000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        System.out.println("Created resources for Cybersecurity course");

        // =========================
        // WEB DEVELOPMENT - COURSE 2
        // =========================
        resourceRepository.save(new Resource(
                savedCourse2.getId(),
                "HTML_CSS_Fundamentals.pdf",
                "/uploads/HTML_CSS_Fundamentals.pdf",
                3072000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse2.getId(),
                "Advanced_JavaScript.pdf",
                "/uploads/Advanced_JavaScript.pdf",
                2560000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse2.getId(),
                "Responsive_Web_Design.pdf",
                "/uploads/Responsive_Web_Design.pdf",
                2890000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse2.getId(),
                "Frontend_Best_Practices.pdf",
                "/uploads/Frontend_Best_Practices.pdf",
                1980000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        System.out.println("Created resources for Web Development course");

        // =========================
        // JAVA BACKEND - COURSE 3
        // =========================
        resourceRepository.save(new Resource(
                savedCourse3.getId(),
                "Spring_Boot_Introduction.pdf",
                "/uploads/Spring_Boot_Introduction.pdf",
                3500000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse3.getId(),
                "REST_API_Design.pdf",
                "/uploads/REST_API_Design.pdf",
                2800000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse3.getId(),
                "Dependency_Injection_Guide.pdf",
                "/uploads/Dependency_Injection_Guide.pdf",
                2200000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        System.out.println("Created resources for Java Backend course");

        // =========================
        // DATABASE SYSTEMS - COURSE 4
        // =========================
        resourceRepository.save(new Resource(
                savedCourse4.getId(),
                "SQL_Fundamentals.pdf",
                "/uploads/SQL_Fundamentals.pdf",
                2400000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse4.getId(),
                "Database_Design_Normalization.pdf",
                "/uploads/Database_Design_Normalization.pdf",
                3100000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        System.out.println("Created resources for Database Systems course");

        // =========================
        // CLOUD COMPUTING - COURSE 5
        // =========================
        resourceRepository.save(new Resource(
                savedCourse5.getId(),
                "Cloud_Introduction.pdf",
                "/uploads/Cloud_Introduction.pdf",
                2600000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse5.getId(),
                "AWS_Basics.pdf",
                "/uploads/AWS_Basics.pdf",
                3300000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        System.out.println("Created resources for Cloud Computing course");

        // =========================
        // AI - COURSE 6
        // =========================
        resourceRepository.save(new Resource(
                savedCourse6.getId(),
                "AI_Introduction.pdf",
                "/uploads/AI_Introduction.pdf",
                2700000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        resourceRepository.save(new Resource(
                savedCourse6.getId(),
                "Machine_Learning_Basics.pdf",
                "/uploads/Machine_Learning_Basics.pdf",
                3600000L,
                "application/pdf",
                savedProfessor.getId()
        ));

        System.out.println("Created resources for AI course");


        // =========================
        // CHAT ROOMS
        // =========================
        ChatRoom savedChat1 = createChat(
                "General Chat - Cybersecurity",
                savedCourse1
        );
        System.out.println("Created Chat Room for Cybersecurity");

        ChatRoom savedChat2 = createChat(
                "General Chat - Web Development",
                savedCourse2
        );
        System.out.println("Created Chat Room for Web Development");

        ChatRoom savedChat3 = createChat(
                "General Chat - Java Backend",
                savedCourse3
        );
        System.out.println("Created Chat Room for Java Backend");

        ChatRoom savedChat4 = createChat(
                "General Chat - Database Systems",
                savedCourse4
        );
        System.out.println("Created Chat Room for Database Systems");

        ChatRoom savedChat5 = createChat(
                "General Chat - Cloud Computing",
                savedCourse5
        );
        System.out.println("Created Chat Room for Cloud Computing");

        ChatRoom savedChat6 = createChat(
                "General Chat - Artificial Intelligence",
                savedCourse6
        );
        System.out.println("Created Chat Room for AI");


        // =========================
        // CHAT MESSAGES
        // =========================

        // Cybersecurity chat
        sendMessage(savedChat1, "Welcome to the Advanced Cybersecurity course!");
        sendMessage(savedChat1, "Thank you, professor!");
        sendMessage(savedChat1, "Will we cover penetration testing in detail?");
        sendMessage(savedChat1, "Yes, we will go deep into it in the next modules.");
        sendMessage(savedChat1, "Looking forward to it!");

        // Web Development chat
        sendMessage(savedChat2, "Let's begin with HTML and CSS fundamentals!");
        sendMessage(savedChat2, "Are we going to use any frameworks?");
        sendMessage(savedChat2, "Yes, we will cover Bootstrap and React later.");
        sendMessage(savedChat2, "Great, I'm excited to learn React!");
        sendMessage(savedChat2, "Perfect, React is very important in modern development.");

        // Java Backend chat
        sendMessage(savedChat3, "Spring Boot is the main framework we will use.");
        sendMessage(savedChat3, "Do we need prior Java experience?");
        sendMessage(savedChat3, "Basic Java knowledge is enough to start.");
        sendMessage(savedChat3, "Good to know, thank you!");

        // Database chat
        sendMessage(savedChat4, "We will focus on relational databases first.");
        sendMessage(savedChat4, "Will we learn NoSQL too?");
        sendMessage(savedChat4, "Yes, MongoDB will also be covered later.");

        // Cloud chat
        sendMessage(savedChat5, "Cloud computing is essential for modern apps.");
        sendMessage(savedChat5, "Will we use AWS?");
        sendMessage(savedChat5, "Yes, AWS and Azure basics will be included.");

        // AI chat
        sendMessage(savedChat6, "AI is one of the fastest growing fields.");
        sendMessage(savedChat6, "Will we learn machine learning?");
        sendMessage(savedChat6, "Yes, we will start with ML fundamentals.");

        System.out.println("Created chat messages successfully");


        // =========================
        // ASSIGNMENTS
        // =========================
        Assignment savedAssignment1 = createAssignment(
                "Cybersecurity Practical Assignment",
                "Perform a vulnerability assessment of a web application.",
                15,
                savedCourse1,
                savedProfessor
        );

        Assignment savedAssignment2 = createAssignment(
                "Web Application Security Analysis",
                "Analyze common vulnerabilities in a web application and propose fixes.",
                20,
                savedCourse1,
                savedProfessor
        );

        Assignment savedAssignment3 = createAssignment(
                "HTML and CSS Project",
                "Develop a responsive website using HTML and CSS.",
                10,
                savedCourse2,
                savedProfessor
        );

        Assignment savedAssignment4 = createAssignment(
                "Frontend UI Clone Project",
                "Recreate a modern landing page using HTML, CSS, and responsive design.",
                14,
                savedCourse2,
                savedProfessor
        );

        Assignment savedAssignment5 = createAssignment(
                "Spring Boot REST API",
                "Build a REST API with Spring Boot including CRUD operations.",
                18,
                savedCourse3,
                savedProfessor
        );

        Assignment savedAssignment6 = createAssignment(
                "Database Design Project",
                "Design and normalize a relational database schema.",
                12,
                savedCourse4,
                savedProfessor
        );

        Assignment savedAssignment7 = createAssignment(
                "Cloud Deployment Exercise",
                "Deploy a sample application to a cloud provider.",
                16,
                savedCourse5,
                savedProfessor
        );

        Assignment savedAssignment8 = createAssignment(
                "Introduction to Machine Learning",
                "Implement a basic ML model using a dataset.",
                25,
                savedCourse6,
                savedProfessor
        );

        System.out.println("Created assignments successfully");


        // =========================
        // SUBMISSIONS
        // =========================

        // Cybersecurity Assignment submissions
        createSubmission(
                savedAssignment1,
                savedStudent1,
                SubmissionStatus.PENDING,
                "/submissions/mary_cybersecurity.zip",
                null,
                null,
                null,
                48
        );

        createSubmission(
                savedAssignment1,
                savedStudent2,
                SubmissionStatus.GRADED,
                "/submissions/peter_cybersecurity.zip",
                new BigDecimal("18.50"),
                "Good work. Vulnerabilities were well identified and explained.",
                savedProfessor,
                24
        );

        createSubmission(
                savedAssignment1,
                savedStudent3,
                SubmissionStatus.GRADED,
                "/submissions/emily_cybersecurity.zip",
                new BigDecimal("15.00"),
                "Good effort, but missing some key edge cases.",
                savedProfessor,
                30
        );

        // Web Development Assignment submissions
        createSubmission(
                savedAssignment3,
                savedStudent1,
                SubmissionStatus.LATE,
                "/submissions/mary_html_css.zip",
                null,
                null,
                null,
                5
        );

        createSubmission(
                savedAssignment3,
                savedStudent4,
                SubmissionStatus.PENDING,
                "/submissions/michael_html_css.zip",
                null,
                null,
                null,
                10
        );

        createSubmission(
                savedAssignment3,
                savedStudent6,
                SubmissionStatus.GRADED,
                "/submissions/daniel_html_css.zip",
                new BigDecimal("17.75"),
                "Very clean and responsive layout.",
                savedProfessor,
                12
        );

        // Backend Assignment submissions
        createSubmission(
                savedAssignment5,
                savedStudent5,
                SubmissionStatus.PENDING,
                "/submissions/sophia_springboot.zip",
                null,
                null,
                null,
                20
        );

        createSubmission(
                savedAssignment5,
                savedStudent3,
                SubmissionStatus.GRADED,
                "/submissions/emily_springboot.zip",
                new BigDecimal("19.00"),
                "Excellent REST API design and clean architecture.",
                savedProfessor,
                18
        );

        // AI Assignment submissions
        createSubmission(
                savedAssignment8,
                savedStudent7,
                SubmissionStatus.PENDING,
                "/submissions/olivia_ai.zip",
                null,
                null,
                null,
                8
        );

        // =========================
        // Web Application Security Analysis
        // Assignment 2
        // =========================
        createSubmission(
                savedAssignment2,
                savedStudent4,
                SubmissionStatus.GRADED,
                "/submissions/michael_web_security.zip",
                new BigDecimal("16.75"),
                "Good analysis of SQL Injection and XSS vulnerabilities.",
                savedProfessor,
                15
        );

        createSubmission(
                savedAssignment2,
                savedStudent5,
                SubmissionStatus.PENDING,
                "/submissions/sophia_web_security.zip",
                null,
                null,
                null,
                8
        );

        createSubmission(
                savedAssignment2,
                savedStudent6,
                SubmissionStatus.LATE,
                "/submissions/daniel_web_security.zip",
                null,
                null,
                null,
                3
        );


        // =========================
        // Frontend UI Clone Project
        // Assignment 4
        // =========================
        createSubmission(
                savedAssignment4,
                savedStudent2,
                SubmissionStatus.GRADED,
                "/submissions/peter_ui_clone.zip",
                new BigDecimal("18.00"),
                "Excellent responsiveness and visual accuracy.",
                savedProfessor,
                20
        );

        createSubmission(
                savedAssignment4,
                savedStudent7,
                SubmissionStatus.PENDING,
                "/submissions/olivia_ui_clone.zip",
                null,
                null,
                null,
                6
        );

        createSubmission(
                savedAssignment4,
                savedStudent1,
                SubmissionStatus.GRADED,
                "/submissions/mary_ui_clone.zip",
                new BigDecimal("14.50"),
                "Good implementation, but some spacing inconsistencies remain.",
                savedProfessor,
                10
        );


        // =========================
        // Database Design Project
        // Assignment 6
        // =========================
        createSubmission(
                savedAssignment6,
                savedStudent3,
                SubmissionStatus.GRADED,
                "/submissions/emily_database.zip",
                new BigDecimal("19.50"),
                "Database schema is well normalized and documented.",
                savedProfessor,
                24
        );

        createSubmission(
                savedAssignment6,
                savedStudent5,
                SubmissionStatus.PENDING,
                "/submissions/sophia_database.zip",
                null,
                null,
                null,
                12
        );

        createSubmission(
                savedAssignment6,
                savedStudent2,
                SubmissionStatus.LATE,
                "/submissions/peter_database.zip",
                null,
                null,
                null,
                2
        );


        // =========================
        // Cloud Deployment Exercise
        // Assignment 7
        // =========================
        createSubmission(
                savedAssignment7,
                savedStudent6,
                SubmissionStatus.GRADED,
                "/submissions/daniel_cloud.zip",
                new BigDecimal("17.25"),
                "Successful deployment and proper documentation.",
                savedProfessor,
                14
        );

        createSubmission(
                savedAssignment7,
                savedStudent4,
                SubmissionStatus.PENDING,
                "/submissions/michael_cloud.zip",
                null,
                null,
                null,
                7
        );

        createSubmission(
                savedAssignment7,
                savedStudent7,
                SubmissionStatus.GRADED,
                "/submissions/olivia_cloud.zip",
                new BigDecimal("18.75"),
                "Excellent use of cloud services and CI/CD pipeline.",
                savedProfessor,
                22
        );

        System.out.println("Created submissions successfully");
    }


    private void enroll(String studentId, String courseId) {
        enrollmentRepository.save(new Enrollment(studentId, courseId));
    }

    private ChatRoom createChat(String name, Course course) {
        ChatRoom chat = new ChatRoom(name);
        chat.setCourse(course);
        return chatRoomRepository.save(chat);
    }

    private void sendMessage(ChatRoom chat, String message) {
        chatMessageRepository.save(new ChatMessage(
                chat,
                message,
                new Date()
        ));
    }

    private Assignment createAssignment(String title, String description, int daysToDeadline, Course course, User professor) {
        Assignment assignment = new Assignment(
                title,
                description,
                LocalDateTime.now().plusDays(daysToDeadline),
                course.getId(),
                professor.getId()
        );
        return assignmentRepository.save(assignment);
    }

    private void createSubmission(Assignment assignment,
                                  User student,
                                  SubmissionStatus status,
                                  String filePath,
                                  BigDecimal grade,
                                  String feedback,
                                  User professor,
                                  long hoursOffset) {

        Submission submission = new Submission(
                assignment,
                student.getId(),
                LocalDateTime.now().minusHours(hoursOffset),
                status,
                filePath
        );

        if (grade != null) {
            submission.setGrade(grade);
        }

        if (feedback != null) {
            submission.setFeedback(feedback);
        }

        if (professor != null) {
            submission.setLastModifiedBy(professor.getId());
        }

        submissionRepository.save(submission);
    }
}