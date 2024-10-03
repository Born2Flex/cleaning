CREATE TABLE IF NOT EXISTS commercial_proposals
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(255) NOT NULL UNIQUE,
    short_description VARCHAR(100) DEFAULT NULL,
    full_description  VARCHAR(500) DEFAULT NULL,
    price             DOUBLE PRECISION NOT NULL,
    duration          NUMERIC(21, 0) NOT NULL,
    count_of_employee INT NOT NULL,
    deleted           BOOLEAN DEFAULT FALSE,
    type              VARCHAR(255) NOT NULL CHECK (type IN ('PER_AREA', 'PER_ITEM'))
);

CREATE TABLE IF NOT EXISTS reviews
(
    id            BIGSERIAL PRIMARY KEY,
    cleaning_rate BIGINT NOT NULL,
    employee_rate BIGINT NOT NULL,
    details       VARCHAR(700) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    id            BIGSERIAL PRIMARY KEY,
    price         DOUBLE PRECISION NOT NULL,
    order_time    TIMESTAMP(6) NOT NULL,
    creation_time TIMESTAMP(6) NOT NULL,
    client_email  VARCHAR(50) NOT NULL,
    comment       VARCHAR(500) DEFAULT NULL,
    address       VARCHAR(600) NOT NULL,
    review        BIGINT,
    status        VARCHAR(255) NOT NULL CHECK (status IN ('NOT_VERIFIED', 'VERIFIED', 'NOT_STARTED', 'PREPARING', 'IN_PROGRESS', 'DONE', 'CANCELLED')),
    duration      NUMERIC(21, 0) NOT NULL,
    UNIQUE (review),
    CONSTRAINT FKm5koajka35938tnksntkrm9mf FOREIGN KEY (review) REFERENCES reviews (id)
);

CREATE TABLE IF NOT EXISTS executors
(
    order_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    PRIMARY KEY (order_id, user_id),
    CONSTRAINT FK50l4atwr0jkewagj8xeod1kbr FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE IF NOT EXISTS order_commercial_proposals_mapping
(
    commercial_proposal_id BIGINT NOT NULL,
    order_id               BIGINT NOT NULL,
    quantity               INT DEFAULT NULL,
    PRIMARY KEY (commercial_proposal_id, order_id),
    CONSTRAINT FKj2n80auouo3faxnku0h3nk0vb FOREIGN KEY (commercial_proposal_id) REFERENCES commercial_proposals (id),
    CONSTRAINT FKmrtujudo0hvqv397xk1n5pk86 FOREIGN KEY (order_id) REFERENCES orders (id)
);

INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 15m/900s (+000000000)
VALUES ('Подушка велика', 'Велика подушка з легкоочищуваного матеріалу', 'Подушка 70х70 з екопуху/пуху/бамбуку, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні подушки не підпадають у цю категорію.', 1, 900000000000, 220, 'PER_ITEM');
INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 30m/1800s
VALUES ('Подушка велика+', 'Білосніжні або шовкові подушки', 'Подушка 70х70 з шовку, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні подушки підпадають у цю категорію.', 1, 1800000000000, 570, 'PER_ITEM');
INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 1h/3600s
VALUES ('Диван середній', 'Диван 200х100х70', 'Диван 200х100х70 з легкоочищуваного матеріалу, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні дивани не підпадають у цю категорію.', 2, 3600000000000, 1100, 'PER_ITEM');
INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 1h/3600s
VALUES ('Диван середній+', 'Диван 200х100х70 з складних матеріалів', 'Диван 200х100х70 з шовку, без глибоких складних забруднень, без пошкоджень, що вимагають делікатної чистки. Білосніжні дивани підпадають у цю категорію.', 2, 3600000000000, 1500, 'PER_ITEM');
INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 3h/10800s
VALUES ('Офіс (плитка)', 'Вологе прибирання офісу', 'Офіс з кам\`яною підлогою або плиткою, без складних забруднень, до 3 санвузлів, прибирання коли офіс пустий.', 4, 10800000000000, 200, 'PER_AREA');
INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 3h/10800s
VALUES ('Офіс (паркет)', 'Вологе прибирання офісу', 'Офіс з паркетом, без складних забруднень, до 2 санвузлів, прибирання коли офіс пустий.', 6, 10800000000000, 250, 'PER_AREA');
INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 3h/10800s
VALUES ('Ремонт', 'Вологе прибирання після ремонту', 'Прибирання після ремонту – фінальна точка перед приїздом на місце нову квартиру. Навіть якщо ви проводили косметичний ремонт, на меблях, техніці, підлозі та інших поверхнях міг залишитися будівельний пил, від якого складно позбутися самостійно.', 3, 10800000000000, 110, 'PER_AREA');
INSERT INTO commercial_proposals (name, short_description, full_description, count_of_employee, duration, price, type) -- 30m/1800s
VALUES ('Вікна', 'Миття вікон', 'Миття вікон, віконних рам, віконних жалюзі, віконних решіток, віконних москітних сіток, підвіконь', 1, 1800000000000, 170, 'PER_AREA');
