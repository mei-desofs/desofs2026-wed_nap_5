CREATE TABLE enrollments
(
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,
    course_id   UUID NOT NULL,
    enrolled_at TIMESTAMP NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_user_id ON enrollments (user_id);
CREATE INDEX idx_enrollments_course_id ON enrollments (course_id);
