-- addresses

INSERT INTO addresses (city, street, house_number, flat_number, zip, user_id)
VALUES ('Київ', 'Хрещатик', '1Д', '1', '01001', 2);

INSERT INTO addresses (city, street, house_number, flat_number, zip, user_id)
VALUES ('Київ', 'Гончаренка', '10Г', '12', '02345', 2);

INSERT INTO addresses (city, street, house_number, zip, user_id)
VALUES ('Львів', 'Петлюри', '10', '01231', null);

INSERT INTO addresses (city, street, house_number, flat_number, zip, user_id)
VALUES ('Київ', 'Лумумби', '10', '40К', '01671', null);

INSERT INTO addresses (city, street, house_number, flat_number, zip, user_id)
VALUES ('Дніпро', 'Тичини', '10В', '15К', '01501', 3);

-- reviews

INSERT INTO reviews (cleaning_rate, employee_rate, details)
VALUES (5, 5, 'Все було ідеально');

INSERT INTO reviews (cleaning_rate, employee_rate, details)
VALUES (4, 5, 'Все було добре');

INSERT INTO reviews (cleaning_rate, employee_rate, details)
VALUES (2, 1, 'Все було жахливо. Хамське выдношення та погана якість прибирання');

-- orders

INSERT INTO orders (duration, price, address, client, creation_time, order_time, review, status)
VALUES (3650000000000, 1100, 5, 2, '2023-8-22 18:00', '2023-9-1 12:00', 2, 'DONE');

INSERT INTO orders (duration, price, address, client, creation_time, order_time, review, comment, status)
VALUES (3100000000000, 1000, 3, 2, '2023-9-22 18:00', '2023-9-27 12:00', 3, 'Нікого не буде вдома під час прибирання', 'DONE');

INSERT INTO orders (duration, price, address, client, creation_time, order_time, review, comment, status)
VALUES (3600000000000, 1900, 4, 3, '2023-10-22 18:00', '2023-10-27 12:00', 1, '-', 'DONE');

INSERT INTO orders (duration, price, address, client, creation_time, order_time, comment, status)
VALUES (3600000000000, 1900, 4, 4, NOW(), '2024-1-1 15:00', 'Не чистити подушки', 'NOT_VERIFIED');

-- order proposal

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (1, 1, 2);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (1, 2, 2);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (1, 3, 6);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (2, 4, 7);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (2, 5, 1);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (2, 6, 9);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (3, 7, 27);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (3, 8, 7);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (4, 3, 2);

INSERT INTO order_commercial_proposals_mapping (order_id, commercial_proposal_id, quantity)
VALUES (4, 8, 4);

-- executors

INSERT INTO executors (order_id, user_id)
VALUES (1, 3);

INSERT INTO executors (order_id, user_id)
VALUES (1, 6);

INSERT INTO executors (order_id, user_id)
VALUES (1, 7);

INSERT INTO executors (order_id, user_id)
VALUES (2, 3);

INSERT INTO executors (order_id, user_id)
VALUES (2, 6);

INSERT INTO executors (order_id, user_id)
VALUES (3, 7);

INSERT INTO executors (order_id, user_id)
VALUES (4, 6);
