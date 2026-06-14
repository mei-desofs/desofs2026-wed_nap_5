package com.grupo.learningmore.domain.assignment;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;
 
import static org.junit.jupiter.api.Assertions.*;

public class AssignmentUnitTest {

    @Test
    public void testCanBeSubmittedAndAddSubmission() {
        String courseId = "course123";
        String creator = "professor123";

        Assignment assignment = new Assignment(
                "Title",
                "Desc",
                LocalDateTime.now().plusDays(2),
                courseId,
                creator
        );

        assertTrue(assignment.canBeSubmitted());

        String studentId = "student123";
        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        String subId = "submission123";

        ReflectionTestUtils.setField(submission, "id", subId);
         

        assignment.addSubmission(submission);

        assertEquals(1, assignment.getSubmissions().size());
        assertEquals(submission, assignment.findSubmissionById(subId));
        assertEquals(submission, assignment.findSubmissionByUserId(studentId));

        assignment.addSubmission(null);
        assertEquals(1, assignment.getSubmissions().size());
    }

    @Test
    public void testCanBeSubmittedWhenDeadlineExpired() {
        Assignment past = new Assignment(
                "Old",
                "Desc",
                LocalDateTime.now().minusDays(1),
                "course123",
                "professor123"
        );

        assertFalse(past.canBeSubmitted());
    }
}
