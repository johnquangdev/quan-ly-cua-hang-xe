CREATE TABLE discounts (
    id SERIAL PRIMARY KEY,
    name VARCHAR,
    percent INTEGER,
    start_date DATE,
    end_date DATE
);

CREATE TABLE invoices (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP,
    total_amount NUMERIC,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE invoice_items (
    id SERIAL PRIMARY KEY,
    invoice_id INTEGER,
    product_id INTEGER,
    quantity INTEGER NOT NULL,
    price NUMERIC NOT NULL,
    discount_id INTEGER,
    CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_discount FOREIGN KEY (discount_id) REFERENCES discounts(id)
);