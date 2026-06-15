CREATE TABLE courses
(
    id          VARCHAR(64) PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    created_by  VARCHAR(64)         NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_courses_code ON courses (code);
