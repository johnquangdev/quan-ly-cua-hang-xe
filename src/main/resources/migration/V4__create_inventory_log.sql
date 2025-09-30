CREATE TABLE inventory_logs (
    id SERIAL PRIMARY KEY,
    product_id INTEGER,
    change_amount INTEGER,
    reason TEXT,
    created_at TIMESTAMP,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
);