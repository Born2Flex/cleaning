CREATE TABLE IF NOT EXISTS `users`
(
    `id`           bigint                           NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) DEFAULT NULL,
    `surname`      varchar(255) DEFAULT NULL,
    `patronymic`   varchar(255) DEFAULT NULL,
    `email`        varchar(255)                     NOT NULL,
    `password`     varchar(255)                     NOT NULL,
    `role`         enum ('USER','EMPLOYEE','ADMIN') NOT NULL,
    `phone_number` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
    UNIQUE KEY `UK_4bgmpi98dylab6qdvf9xyaxu4` (`phone_number`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `addresses`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `city`         varchar(255) NOT NULL,
    `street`       varchar(255) NOT NULL,
    `house_number` varchar(255) NOT NULL,
    `flat_number`  varchar(255) DEFAULT NULL,
    `zip`          varchar(255) DEFAULT NULL,
    `user_id`      bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKda8tuywtf0gb6sedwk7la1pgi` (`user_id`),
    CONSTRAINT `FKda8tuywtf0gb6sedwk7la1pgi` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `commercial_proposals`
(
    `id`                bigint                       NOT NULL AUTO_INCREMENT,
    `name`              varchar(255)                 NOT NULL,
    `short_description` varchar(100) DEFAULT NULL,
    `full_description`  varchar(500) DEFAULT NULL,
    `price`             double                       NOT NULL,
    `duration`          decimal(21, 0)               NOT NULL,
    `count_of_employee` int                          NOT NULL,
    `deleted`           bit(1)       DEFAULT FALSE,
    `type`              enum ('PER_AREA','PER_ITEM') NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_a2l3whnjhqv37ftofa7vn66q8` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `reviews`
(
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `cleaning_rate` bigint NOT NULL,
    `employee_rate` bigint NOT NULL,
    `details`       varchar(700) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `orders`
(
    `id`            bigint                                                                                      NOT NULL AUTO_INCREMENT,
    `price`         double                                                                                      NOT NULL,
    `order_time`    datetime(6)                                                                                 NOT NULL,
    `creation_time` datetime(6)                                                                                 NOT NULL,
    `client`        bigint       DEFAULT NULL,
    `comment`       varchar(500) DEFAULT NULL,
    `address`       bigint                                                                                      NOT NULL,
    `review`        bigint       DEFAULT NULL,
    `status`        enum ('NOT_VERIFIED','VERIFIED','NOT_STARTED','PREPARING','IN_PROGRESS','DONE','CANCELLED') NOT NULL,
    `duration`      decimal(21, 0)                                                                              NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_g9w287a4g2eo1hy2fkhg81dm` (`review`),
    KEY `FKg8nkrjl2e7h9vmisqk3wb6mf4` (`client`),
    CONSTRAINT `FKg8nkrjl2e7h9vmisqk3wb6mf4` FOREIGN KEY (`client`) REFERENCES `users` (`id`),
    CONSTRAINT `FKm5koajka35938tnksntkrm9mf` FOREIGN KEY (`review`) REFERENCES `reviews` (`id`),
    CONSTRAINT `FKqqw5cd6q594ac1ifjxbq1cian` FOREIGN KEY (`address`) REFERENCES `addresses` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `executors`
(
    `order_id` bigint NOT NULL,
    `user_id`  bigint NOT NULL,
    PRIMARY KEY (`order_id`, `user_id`),
    KEY `FKs9vfdbexrmy2c2l4kdyw33mcm` (`user_id`),
    CONSTRAINT `FK50l4atwr0jkewagj8xeod1kbr` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    CONSTRAINT `FKs9vfdbexrmy2c2l4kdyw33mcm` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `order_commercial_proposals_mapping`
(
    `commercial_proposal_id` bigint NOT NULL,
    `order_id`               bigint NOT NULL,
    `quantity`               int DEFAULT NULL,
    PRIMARY KEY (`commercial_proposal_id`, `order_id`),
    KEY `FKmrtujudo0hvqv397xk1n5pk86` (`order_id`),
    CONSTRAINT `FKj2n80auouo3faxnku0h3nk0vb` FOREIGN KEY (`commercial_proposal_id`) REFERENCES `commercial_proposals` (`id`),
    CONSTRAINT `FKmrtujudo0hvqv397xk1n5pk86` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)
VALUES ('Leonid', 'Petrenko', 'Ihorovich', '$2a$10$6DI5oh7MbZX7DSkdHOfdlOc6GXj2gH8Qgyo5VCmuldGnAkEMlo3GO', 'admin', '+380930000000', 'ADMIN');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: Qw3rty*
VALUES ('Maizie', 'Burnett', 'Viktorovivna', '$2a$10$lFW0pKbU24UkSlBFoANN0uE/FETJJCf66iDUOMZS8JYgmgeVvx6L2', 'm.burnatt@gmail.com', '+380931234567','USER');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: P4ssw()rd
VALUES ('Chaya', 'Burnett', 'Petrivna', '$2a$10$3ezDfbsXuVb817/MgR9D5e2ERNHZDckq/0kqx1SwWHnYTdnSmZz7y', 'c.burnett@outlook.com', '+380685812781', 'EMPLOYEE');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: password
VALUES ('Bobby', 'Durham', 'Ihorovich', '$2a$10$khRH0cGfqeo6S8uux6o.suCG32m1qxxj60mP3m7eIK3ibWjkB4nXW', 'b.durman@gmail.com', '+380503215691', 'USER');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: us3r
VALUES ('Micheal', 'Jacobson', 'Olegovich', '$2a$10$jt6bt5yQowuPz.W0KFvqu.Q1LdJpl0C0nRaTd2VQkby194BitHoBO', 'm.jacobs@gmail.com', '+380521785665', 'USER');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: qwerty
VALUES ('Kallum', 'Charles', 'Ivanovna', '$2a$10$mLur1uQN0ZRORjvPMAo.OeBOYsTbz4h3fp/hOEoaWvBEiiNDr/5S2', 'k.charles@i.ua', '+380951234567', 'EMPLOYEE');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: password
VALUES ('Alys', 'Bonner', 'Semenivna', '$2a$10$khRH0cGfqeo6S8uux6o.suCG32m1qxxj60mP3m7eIK3ibWjkB4nXW', 'a.bonner@gmail.com', '+380679831471', 'EMPLOYEE');

INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 15m/900s (+000000000)
VALUES (1, 'Подушка велика', 'Велика подушка з легкоочищуваного матеріалу', 'Подушка 70х70 з екопуху/пуху/бамбуку, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні подушки не підпадають у цю категорію.', 1, 900000000000, 220, 'PER_ITEM');
INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 30m/1800s
VALUES (2, 'Подушка велика+', 'Білосніжні або шовкові подушки', 'Подушка 70х70 з шовку, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні подушки підпадають у цю категорію.', 1, 1800000000000, 570, 'PER_ITEM');
INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 1h/3600s
VALUES (3, 'Диван середній', 'Диван 200х100х70', 'Диван 200х100х70 з легкоочищуваного матеріалу, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні дивани не підпадають у цю категорію.', 2, 3600000000000, 1100, 'PER_ITEM');
INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 1h/3600s
VALUES (4, 'Диван середній+', 'Диван 200х100х70 з складних матеріалів', 'Диван 200х100х70 з шовку, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні дивани підпадають у цю категорію.', 2, 3600000000000, 1500, 'PER_ITEM');
INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 3h/10800s
VALUES (5, 'Офіс (плитка)', 'Вологе прибирання офісу', 'Офіс з кам\`яною підлогою або плиткою, без складних забруднень, до 3 санвузлів, прибирання коли офіс пустий.', 4, 10800000000000, 200, 'PER_AREA');
INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 3h/10800s
VALUES (6, 'Офіс (паркет)', 'Вологе прибирання офісу', 'Офіс з паркетом, без складних забруднень, до 2 санвузлів, прибирання коли офіс пустий.', 6, 10800000000000, 250, 'PER_AREA');
INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 3h/10800s
VALUES (7, 'Ремонт', 'Вологе прибирання після ремонту', 'Прибирання після ремонту – фінальна точка перед приїздом на місце нову квартиру. Навіть якщо ви проводили косметичний ремонт, на меблях, техніці, підлозі та інших поверхнях міг залишитися будівельний пил, від якого складно позбутися самостійно.', 3, 10800000000000, 110, 'PER_AREA');
INSERT INTO commercial_proposals (id, name, short_description, full_description, count_of_employee, duration, price, type) -- 30m/1800s
VALUES (8, 'Вікна', 'Миття вікон', 'Миття вікон, віконних рам, віконних жалюзі, віконних решіток, віконних москітних сіток, підвіконь', 1, 1800000000000, 170, 'PER_AREA');
