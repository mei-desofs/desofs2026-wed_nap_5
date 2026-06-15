CREATE TABLE enrollments
(
    id          VARCHAR(64) PRIMARY KEY,
    user_id     VARCHAR(64) NOT NULL,
    course_id   VARCHAR(64) NOT NULL,
    enrolled_at TIMESTAMP NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_user_id ON enrollments (user_id);
CREATE INDEX idx_enrollments_course_id ON enrollments (course_id);
