ALTER SEQUENCE orders_id_seq OWNED BY orders.id; TRUNCATE TABLE orders RESTART IDENTITY CASCADE;
ALTER SEQUENCE reviews_id_seq OWNED BY reviews.id; TRUNCATE TABLE reviews RESTART IDENTITY CASCADE;
