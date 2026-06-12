package com.grupo.learningmore.domain.assignment;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
 

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentUnitTest {

    @Test
    public void testCanBeSubmittedAndAddSubmission() {
        String courseId = "course-" + System.nanoTime();
        String creator = "professor-" + System.nanoTime();

        Assignment assignment = new Assignment(
                "Title",
                "Desc",
                LocalDateTime.now().plusDays(2),
                courseId,
                creator
        );

        assertTrue(assignment.canBeSubmitted());

        String studentId = "student-" + System.nanoTime();
        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        String subId = "sub-" + System.nanoTime();;
        submission.setId(subId);

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
                "course-" + System.nanoTime(),
                "professor-" + System.nanoTime()
        );

        assertFalse(past.canBeSubmitted());
    }
}
