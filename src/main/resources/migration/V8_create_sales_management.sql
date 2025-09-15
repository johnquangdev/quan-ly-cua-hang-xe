/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Minh
 * Created: Sep 5, 2025
 */
CREATE TABLE sales (
    sale_id SERIAL PRIMARY KEY,
    car_id INTEGER NOT NULL REFERENCES cars(car_id) ON DELETE CASCADE,
    customer_id INTEGER NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
    employee_id INTEGER NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    sale_date DATE NOT NULL DEFAULT CURRENT_DATE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(15, 2) NOT NULL CHECK (unit_price > 0),
    total_amount NUMERIC(15, 2) NOT NULL CHECK (total_amount > 0),
    payment_method VARCHAR(50) CHECK (payment_method IN ('CASH', 'CREDIT_CARD', 'BANK_TRANSFER')),
    status VARCHAR(20) DEFAULT 'COMPLETED' CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sales_date ON sales(sale_date);
CREATE INDEX idx_sales_customer ON sales(customer_id);
CREATE INDEX idx_sales_employee ON sales(employee_id);
CREATE INDEX idx_sales_car ON sales(car_id);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_sales_updated_at 
    BEFORE UPDATE ON sales 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION update_car_quantity_after_sale()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'COMPLETED' THEN
        UPDATE cars 
        SET quantity = quantity - NEW.quantity 
        WHERE car_id = NEW.car_id;
    END IF;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_car_quantity 
    AFTER INSERT OR UPDATE ON sales 
    FOR EACH ROW EXECUTE FUNCTION update_car_quantity_after_sale();

CREATE VIEW sales_details AS
SELECT 
    s.sale_id,
    s.sale_date,
    s.quantity,
    s.unit_price,
    s.total_amount,
    s.payment_method,
    s.status,
    s.created_at,
    s.updated_at,
    c.car_id,
    c.name as car_name,
    c.brand as car_brand,
    c.model as car_model,
    c.year as car_year,
    cust.customer_id,
    cust.name as customer_name,
    cust.phone as customer_phone,
    e.employee_id,
    e.name as employee_name,
    e.position as employee_position
FROM sales s
JOIN cars c ON s.car_id = c.car_id
JOIN customers cust ON s.customer_id = cust.customer_id
JOIN employees e ON s.employee_id = e.employee_id;