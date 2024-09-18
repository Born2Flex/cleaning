CREATE TABLE IF NOT EXISTS employment
(
    id              BIGSERIAL PRIMARY KEY,
    applicant       BIGINT NOT NULL,
    creation_time   TIMESTAMP(6) NOT NULL,
    motivation_list VARCHAR(1000),
    CONSTRAINT FKs9vfdbexrmy2c2l4kdyw33mca FOREIGN KEY (applicant) REFERENCES users (id)
);
