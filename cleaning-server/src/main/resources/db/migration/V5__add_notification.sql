CREATE TABLE IF NOT EXISTS notifications
(
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL REFERENCES orders(id)
);
