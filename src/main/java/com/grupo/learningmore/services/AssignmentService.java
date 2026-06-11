package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.assignment.AssignmentAuditLog;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.AssignmentAuditLogRepository;
import com.grupo.learningmore.repositories.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentAuditLogRepository assignmentAuditLogRepository;
    private final CourseService courseService;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            AssignmentAuditLogRepository assignmentAuditLogRepository,
            CourseService courseService
    ) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentAuditLogRepository = assignmentAuditLogRepository;
        this.courseService = courseService;
    }

    @Transactional
    public Assignment createAssignment(String courseId, String title, String description, LocalDateTime deadline, String actorId, boolean isAdmin) {

        log.info("Creating assignment for course {} by user {}", courseId, actorId);

        if (deadline == null || !deadline.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline must be in the future");
        }

        var course = courseService.findById(courseId);
        if (!isAdmin && !course.getCreatedBy().equals(actorId)) {
            throw new AccessDeniedException("Only the course owner can create assignments");
        }

        Assignment assignment = new Assignment(title, description, deadline, courseId, actorId);
        Assignment saved = assignmentRepository.save(assignment);

        assignmentAuditLogRepository.save(new AssignmentAuditLog(
                saved.getId(),
                "CREATE",
                actorId,
                null,
                "title=" + saved.getTitle() + ",deadline=" + saved.getDeadline(),
                LocalDateTime.now()
        ));

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Assignment> findByCourseId(String courseId) {

        log.info("Fetching assignments for course {}", courseId);

        courseService.findById(courseId);
        return assignmentRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public Assignment findById(String assignmentId) {

        log.info("Fetching assignment {}", assignmentId);

        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
    }

    @Transactional
    public Assignment updateAssignment(
            String courseId,
            String assignmentId,
            String title,
            String description,
            LocalDateTime deadline,
            String actorId,
            boolean isAdmin
    ) {
        Assignment assignment = assignmentRepository.findByIdAndCourseId(assignmentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found in course"));

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            throw new AccessDeniedException("Only the assignment owner can update it");
        }

        String oldValues = "title=" + assignment.getTitle() + ",deadline=" + assignment.getDeadline();

        if (title != null && !title.isBlank()) {
            assignment.setTitle(title);
        }

        if (description != null) {
            assignment.setDescription(description);
        }

        if (deadline != null) {
            if (!deadline.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Deadline must be in the future");
            }
            assignment.setDeadline(deadline);
        }

        Assignment saved = assignmentRepository.save(assignment);

        assignmentAuditLogRepository.save(new AssignmentAuditLog(
                saved.getId(),
                "UPDATE",
                actorId,
                oldValues,
                "title=" + saved.getTitle() + ",deadline=" + saved.getDeadline(),
                LocalDateTime.now()
        ));

        return saved;
    }

    @Transactional
    public void deleteAssignment(String courseId, String assignmentId, String actorId, boolean isAdmin) {

        log.warn("Deleting assignment {} from course {} by user {}", assignmentId, courseId, actorId);

        Assignment assignment = assignmentRepository.findByIdAndCourseId(assignmentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found in course"));

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            throw new AccessDeniedException("Only the assignment owner can delete it");
        }

        assignmentAuditLogRepository.save(new AssignmentAuditLog(
                assignment.getId(),
                "DELETE",
                actorId,
                "title=" + assignment.getTitle() + ",deadline=" + assignment.getDeadline(),
                null,
                LocalDateTime.now()
        ));

        assignmentRepository.delete(assignment);
    }
}
