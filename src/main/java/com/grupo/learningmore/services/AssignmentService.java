package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.assignment.Assignment;
import com.grupo.learningmore.domain.assignment.AssignmentAuditLog;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.AssignmentAuditLogRepository;
import com.grupo.learningmore.repositories.AssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentService.class);

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
            log.warn("Invalid deadline provided for assignment creation by user {}", actorId);
            throw new IllegalArgumentException("Deadline must be in the future");
        }

        var course = courseService.findById(courseId);

        if (!isAdmin && !course.getCreatedBy().equals(actorId)) {
            log.warn("Unauthorized assignment creation attempt by user {} for course {}", actorId, courseId);
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

        log.info("Assignment created successfully with id {} for course {}", saved.getId(), courseId);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Assignment> findByCourseId(String courseId) {

        log.info("Fetching assignments for course {}", courseId);

        courseService.findById(courseId);

        List<Assignment> result = assignmentRepository.findByCourseId(courseId);

        log.info("Found {} assignments for course {}", result.size(), courseId);

        return result;
    }

    @Transactional(readOnly = true)
    public Assignment findById(String assignmentId) {

        log.info("Fetching assignment {}", assignmentId);

        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.warn("Assignment not found: {}", assignmentId);
                    return new IllegalArgumentException("Assignment not found");
                });
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

        log.info("Updating assignment {} in course {} by user {}", assignmentId, courseId, actorId);

        Assignment assignment = assignmentRepository.findByIdAndCourseId(assignmentId, courseId)
                .orElseThrow(() -> {
                    log.warn("Assignment not found in course {}: {}", courseId, assignmentId);
                    return new IllegalArgumentException("Assignment not found in course");
                });

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            log.warn("Unauthorized update attempt on assignment {} by user {}", assignmentId, actorId);
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
                log.warn("Invalid deadline update attempt for assignment {} by user {}", assignmentId, actorId);
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

        log.info("Assignment {} updated successfully", assignmentId);

        return saved;
    }

    @Transactional
    public void deleteAssignment(String courseId, String assignmentId, String actorId, boolean isAdmin) {

        log.warn("Deleting assignment {} from course {} by user {}", assignmentId, courseId, actorId);

        Assignment assignment = assignmentRepository.findByIdAndCourseId(assignmentId, courseId)
                .orElseThrow(() -> {
                    log.warn("Assignment not found for delete: {}", assignmentId);
                    return new IllegalArgumentException("Assignment not found in course");
                });

        if (!isAdmin && !assignment.getCreatedBy().equals(actorId)) {
            log.warn("Unauthorized delete attempt on assignment {} by user {}", assignmentId, actorId);
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

        log.info("Assignment {} deleted successfully", assignmentId);
    }
}