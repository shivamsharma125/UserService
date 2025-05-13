CREATE TABLE sessions
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_at       datetime              NULL,
    last_modified_at datetime              NULL,
    status           SMALLINT              NULL,
    token            VARCHAR(255)          NULL,
    user_id          BIGINT                NULL,
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

ALTER TABLE sessions
    ADD CONSTRAINT FK_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);