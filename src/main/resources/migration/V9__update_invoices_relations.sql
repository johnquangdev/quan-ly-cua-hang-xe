-- V9__update_invoices_relations.sql

-- 1. Xóa bảng staff nếu tồn tại
DROP TABLE IF EXISTS staff CASCADE;

-- 2. Cập nhật bảng invoices
ALTER TABLE invoices
    -- Nếu trước đó có staff_id thì xóa đi
    DROP COLUMN IF EXISTS staff_id,
    -- Thêm cột user_id để thay thế staff
    ADD COLUMN IF NOT EXISTS user_id BIGINT,
    -- Thêm cột customer_id nếu chưa có
    ADD COLUMN IF NOT EXISTS customer_id BIGINT;

-- 3. Tạo ràng buộc khóa ngoại
ALTER TABLE invoices
    ADD CONSTRAINT fk_invoices_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE invoices
    ADD CONSTRAINT fk_invoices_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL;

-- 4. Đảm bảo invoice_items liên kết đúng
ALTER TABLE invoice_items
    ADD CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE;

ALTER TABLE invoice_items
    ADD CONSTRAINT fk_invoice_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT;
