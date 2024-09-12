CREATE TABLE IF NOT EXISTS `refresh_tokens`
(
    `id`            bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `refresh_token` varchar(38)        NOT NULL UNIQUE,
    `expiry_date`    datetime(6) DEFAULT NULL,
    `user_id`       bigint(255) NOT NULL REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;