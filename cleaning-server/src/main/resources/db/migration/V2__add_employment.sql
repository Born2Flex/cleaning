CREATE TABLE IF NOT EXISTS employment
(
    id              BIGSERIAL PRIMARY KEY,
    applicant_id    BIGINT NOT NULL,
    creation_time   TIMESTAMP(6) NOT NULL,
    motivation_list VARCHAR(1000)
);
