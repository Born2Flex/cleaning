CREATE TABLE IF NOT EXISTS users
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) DEFAULT NULL,
    surname      VARCHAR(255) DEFAULT NULL,
    patronymic   VARCHAR(255) DEFAULT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL CHECK (role IN ('USER', 'EMPLOYEE', 'ADMIN', 'CLEANING_SERVER')),
    phone_number VARCHAR(255) DEFAULT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS addresses
(
    id           BIGSERIAL PRIMARY KEY,
    city         VARCHAR(255) NOT NULL,
    street       VARCHAR(255) NOT NULL,
    house_number VARCHAR(255) NOT NULL,
    flat_number  VARCHAR(255) DEFAULT NULL,
    zip          VARCHAR(255) DEFAULT NULL
);

INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)
VALUES ('Leonid', 'Petrenko', 'Ihorovich', '$2a$10$6DI5oh7MbZX7DSkdHOfdlOc6GXj2gH8Qgyo5VCmuldGnAkEMlo3GO', 'admin', '+380930000000', 'ADMIN');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: Qw3rty*
VALUES ('Maizie', 'Burnett', 'Viktorovivna', '$2a$10$lFW0pKbU24UkSlBFoANN0uE/FETJJCf66iDUOMZS8JYgmgeVvx6L2', 'm.burnatt@gmail.com', '+380931234567','USER');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: Qw3rty*
VALUES ('Danylo', 'Shlapak', 'Vitaliyovych', '$2a$10$lFW0pKbU24UkSlBFoANN0uE/FETJJCf66iDUOMZS8JYgmgeVvx6L2', 'd64566994@gmail.com', '+380931234561','USER');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: Qw3rty*
VALUES ('Myhailo', 'Shevchenko', 'Grygorovych', '$2a$10$lFW0pKbU24UkSlBFoANN0uE/FETJJCf66iDUOMZS8JYgmgeVvx6L2', 'muhailo11111@gmail.com', '+380931234562','USER');
INSERT INTO users (name, surname, patronymic, password, email, phone_number, role)-- password: Qw3rty*
VALUES ('Oleksandr', 'Semytsky', 'Igorovych', '$2a$10$lFW0pKbU24UkSlBFoANN0uE/FETJJCf66iDUOMZS8JYgmgeVvx6L2', 'ssemitskiy@gmail.com', '+380945234563','USER');
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
INSERT INTO users (password, email, role)-- password: 0#H)e2LXz{H1
VALUES ('$2y$10$SOrESJVQVMTAgY.9brTdgOBw5tRG4koemj4tyJKtGXO5NiI.puDOG', 'cleaning.server@system.com', 'CLEANING_SERVER');