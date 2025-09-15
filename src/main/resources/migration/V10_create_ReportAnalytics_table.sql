/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Minh
 * Created: Sep 10, 2025
 */

CREATE TABLE reports_analytics (
    report_id SERIAL PRIMARY KEY,
    report_type VARCHAR(50) NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    data TEXT NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    generated_by VARCHAR(100) NOT NULL
);

CREATE INDEX idx_report_type ON reports_analytics(report_type);
CREATE INDEX idx_generated_at ON reports_analytics(generated_at);
CREATE INDEX idx_date_range ON reports_analytics(start_date, end_date);

INSERT INTO reports_analytics (report_type, start_date, end_date, data, generated_by) VALUES
('SALES', '2024-01-01 00:00:00', '2024-01-31 23:59:59', '{"totalSales": 150000000, "totalOrders": 45}', 'admin'),
('INVENTORY', NULL, NULL, '{"totalItems": 120, "outOfStock": 5, "lowStock": 12}', 'admin'),
('CUSTOMER', '2024-01-01 00:00:00', '2024-01-31 23:59:59', '{"totalCustomers": 89, "newCustomers": 15}', 'admin');