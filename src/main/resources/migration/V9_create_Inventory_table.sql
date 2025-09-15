/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Minh
 * Created: Sep 6, 2025
 */
CREATE TABLE inventory (
    inventory_id SERIAL PRIMARY KEY,
    car_id INTEGER NOT NULL UNIQUE REFERENCES cars(car_id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    min_stock_level INTEGER NOT NULL DEFAULT 0 CHECK (min_stock_level >= 0),
    max_stock_level INTEGER NOT NULL DEFAULT 100 CHECK (max_stock_level >= 0),
    last_restocked_date DATE,
    next_restock_date DATE,
    status VARCHAR(20) DEFAULT 'IN_STOCK' CHECK (status IN ('IN_STOCK', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_car ON inventory(car_id);
CREATE INDEX idx_inventory_status ON inventory(status);
CREATE INDEX idx_inventory_quantity ON inventory(quantity);

CREATE OR REPLACE FUNCTION update_inventory_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;

    IF NEW.quantity = 0 THEN
        NEW.status = 'OUT_OF_STOCK';
    ELSIF NEW.quantity <= NEW.min_stock_level THEN
        NEW.status = 'LOW_STOCK';
    ELSE
        NEW.status = 'IN_STOCK';
    END IF;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_inventory_updated_at 
    BEFORE UPDATE ON inventory 
    FOR EACH ROW EXECUTE FUNCTION update_inventory_updated_at();

CREATE OR REPLACE FUNCTION create_inventory_for_new_car()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO inventory (car_id, quantity, min_stock_level, max_stock_level, status)
    VALUES (NEW.car_id, 0, 5, 50, 'OUT_OF_STOCK');
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER create_inventory_for_new_car
    AFTER INSERT ON cars
    FOR EACH ROW EXECUTE FUNCTION create_inventory_for_new_car();

CREATE VIEW inventory_details AS
SELECT 
    i.inventory_id,
    i.quantity,
    i.min_stock_level,
    i.max_stock_level,
    i.last_restocked_date,
    i.next_restock_date,
    i.status,
    i.notes,
    i.created_at,
    i.updated_at,
    c.car_id,
    c.name as car_name,
    c.brand as car_brand,
    c.model as car_model,
    c.year as car_year,
    c.price as car_price
FROM inventory i
JOIN cars c ON i.car_id = c.car_id;