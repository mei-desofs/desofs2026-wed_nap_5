package com.grupo.learningmore.domain.assignment;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentUnitTest {

    @Test
    public void testCanBeSubmittedAndAddSubmission() {
        UUID courseId = UUID.randomUUID();
        UUID creator = UUID.randomUUID();

        Assignment assignment = new Assignment(
                "Title",
                "Desc",
                LocalDateTime.now().plusDays(2),
                courseId,
                creator
        );

        // should accept submissions when deadline in future
        assertTrue(assignment.canBeSubmitted());

        // add a submission and verify retrieval by id and user
        UUID studentId = UUID.randomUUID();
        Submission submission = new Submission(
                assignment,
                studentId,
                LocalDateTime.now(),
                SubmissionStatus.PENDING,
                "uploads/test.pdf"
        );
        UUID subId = UUID.randomUUID();
        submission.setId(subId);

        assignment.addSubmission(submission);

        assertEquals(1, assignment.getSubmissions().size());
        assertEquals(submission, assignment.findSubmissionById(subId));
        assertEquals(submission, assignment.findSubmissionByUserId(studentId));

        // adding null should be ignored
        assignment.addSubmission(null);
        assertEquals(1, assignment.getSubmissions().size());
    }

    @Test
    public void testCanBeSubmittedWhenDeadlineExpired() {
        Assignment past = new Assignment(
                "Old",
                "Desc",
                LocalDateTime.now().minusDays(1),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        assertFalse(past.canBeSubmitted());
    }
}
