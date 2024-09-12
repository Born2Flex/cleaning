CREATE TABLE IF NOT EXISTS `employment`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `applicant`       bigint      NOT NULL,
    `creation_time`   datetime(6) NOT NULL,
    `motivation_list` varchar(1000),
    PRIMARY KEY (`id`),
    KEY `FKs9vfdbexrmy2c2l4kdyw33mca` (`applicant`),
    CONSTRAINT `FKs9vfdbexrmy2c2l4kdyw33mca` FOREIGN KEY (`applicant`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;