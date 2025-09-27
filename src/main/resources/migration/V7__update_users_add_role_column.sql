-- Bước 1: thêm cột role cho users nhưng cho phép NULL tạm thời
ALTER TABLE users
    ADD COLUMN role VARCHAR(20);

-- Bước 2: gán giá trị mặc định cho các user cũ (nếu có)
UPDATE users
SET role = 'STAFF'
WHERE role IS NULL;

-- Bước 3: sửa cột role thành NOT NULL
ALTER TABLE users
    ALTER COLUMN role SET NOT NULL;
