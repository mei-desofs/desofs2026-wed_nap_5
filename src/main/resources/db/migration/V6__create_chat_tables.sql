CREATE TABLE chat_room
(
    id        VARCHAR(64) PRIMARY KEY,
    name      VARCHAR(255),
    course_id VARCHAR(64),
    FOREIGN KEY (course_id) REFERENCES courses (id)
);

CREATE TABLE chat_message
(
    id           VARCHAR(64) PRIMARY KEY,
    chat_room_id VARCHAR(64),
    content      TEXT,
    sent_at      TIMESTAMP,
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id)
);

CREATE INDEX idx_chat_room_course_id ON chat_room (course_id);
CREATE INDEX idx_chat_message_chat_room_id ON chat_message (chat_room_id);
