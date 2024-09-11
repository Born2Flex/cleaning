CREATE TABLE IF NOT EXISTS `notifications`
(
    `id`            bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `order_id`       bigint(255)        NOT NULL REFERENCES `orders` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;