CREATE TABLE resources
(
    id           VARCHAR(64) PRIMARY KEY,
    course_id    VARCHAR(64)         NOT NULL,
    filename     VARCHAR(255) NOT NULL,
    file_path    VARCHAR(512) NOT NULL,
    file_size    BIGINT       NOT NULL,
    content_type VARCHAR(100),
    uploaded_at  TIMESTAMP    NOT NULL,
    uploaded_by  VARCHAR(64)         NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (uploaded_by) REFERENCES users (id)
);

CREATE INDEX idx_resources_course_id ON resources (course_id);
CREATE INDEX idx_resources_uploaded_by ON resources (uploaded_by);
