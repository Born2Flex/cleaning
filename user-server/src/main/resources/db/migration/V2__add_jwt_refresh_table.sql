CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id              BIGSERIAL PRIMARY KEY,
    refresh_token   VARCHAR(38) NOT NULL UNIQUE,
    expiry_date     TIMESTAMP(6),
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE  ON UPDATE CASCADE
);
