-- Create assignments table
CREATE TABLE assignments
(
    id          VARCHAR(64) PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    deadline    TIMESTAMP    NOT NULL,
    course_id   VARCHAR(64)         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(64)         NOT NULL,
    version     INT          NOT NULL DEFAULT 0,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_assignments_course_id ON assignments (course_id);
CREATE INDEX idx_assignments_created_by ON assignments (created_by);
CREATE INDEX idx_assignments_deadline ON assignments (deadline);

-- Create submissions table
CREATE TABLE submissions
(
    id               VARCHAR(64) PRIMARY KEY,
    assignment_id    VARCHAR(64)        NOT NULL,
    user_id          VARCHAR(64)        NOT NULL,
    submitted_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    file_path        VARCHAR(500),
    grade            NUMERIC(5, 2),
    feedback         TEXT,
    version          INT         NOT NULL DEFAULT 0,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(64),
    FOREIGN KEY (assignment_id) REFERENCES assignments (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (last_modified_by) REFERENCES users (id),
    UNIQUE (assignment_id, user_id)
);

CREATE INDEX idx_submissions_assignment_id ON submissions (assignment_id);
CREATE INDEX idx_submissions_user_id ON submissions (user_id);
CREATE INDEX idx_submissions_status ON submissions (status);
CREATE INDEX idx_submissions_submitted_at ON submissions (submitted_at);

-- Create assignment audit log table
CREATE TABLE assignment_audit_logs
(
    id            VARCHAR(64) PRIMARY KEY,
    assignment_id VARCHAR(64)        NOT NULL,
    action        VARCHAR(50) NOT NULL,
    actor_id      VARCHAR(64)        NOT NULL,
    old_values    TEXT,
    new_values    TEXT,
    timestamp     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignment_id) REFERENCES assignments (id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES users (id)
);

CREATE INDEX idx_assignment_audit_logs_assignment_id ON assignment_audit_logs (assignment_id);
CREATE INDEX idx_assignment_audit_logs_timestamp ON assignment_audit_logs (timestamp);

-- Create submission audit log table
CREATE TABLE submission_audit_logs
(
    id            VARCHAR(64) PRIMARY KEY,
    submission_id VARCHAR(64)        NOT NULL,
    action        VARCHAR(50) NOT NULL,
    actor_id      VARCHAR(64)        NOT NULL,
    old_values    TEXT,
    new_values    TEXT,
    timestamp     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES submissions (id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES users (id)
);

CREATE INDEX idx_submission_audit_logs_submission_id ON submission_audit_logs (submission_id);
CREATE INDEX idx_submission_audit_logs_timestamp ON submission_audit_logs (timestamp);
