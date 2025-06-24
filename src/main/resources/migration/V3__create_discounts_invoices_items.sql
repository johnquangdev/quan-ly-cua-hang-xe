-- Bảng discounts
CREATE TABLE discounts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    percent INTEGER CHECK (percent >= 0 AND percent <= 100),
    start_date DATE,
    end_date DATE
);

-- Bảng invoices
CREATE TABLE invoices (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(id),
    staff_id INTEGER REFERENCES staffs(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount NUMERIC(12,2)
);

-- Bảng invoice_items
CREATE TABLE invoice_items (
    id SERIAL PRIMARY KEY,
    invoice_id INTEGER REFERENCES invoices(id),
    product_id INTEGER REFERENCES products(id),
    quantity INTEGER NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    discount_id INTEGER REFERENCES discounts(id)
);