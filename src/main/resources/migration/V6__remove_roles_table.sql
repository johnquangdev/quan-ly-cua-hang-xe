-- Gỡ FK trước (nếu có)
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_id_fkey;

-- Xoá cột role_id trong bảng users
ALTER TABLE users DROP COLUMN IF EXISTS role_id;

-- Xoá bảng roles
DROP TABLE IF EXISTS roles;
