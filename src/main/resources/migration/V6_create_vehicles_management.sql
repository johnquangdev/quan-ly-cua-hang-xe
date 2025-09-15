/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Minh
 * Created: Aug 8, 2025
 */

CREATE TABLE vehicles (ff
    license_plate VARCHAR(15) NOT NULL COMMENT 'Biển số xe - Primary Key',
    vehicle_type VARCHAR(50) NOT NULL COMMENT 'Loại phương tiện (ô tô, xe máy, xe tải, etc.)',
    brand VARCHAR(100) NOT NULL COMMENT 'Hãng xe',
    model VARCHAR(100) NOT NULL COMMENT 'Model/dòng xe',
    manufacture_year INT NOT NULL COMMENT 'Năm sản xuất',

    owner_name VARCHAR(200) NOT NULL COMMENT 'Tên chủ sở hữu',

    color VARCHAR(50) COMMENT 'Màu xe',
    engine_number VARCHAR(50) COMMENT 'Số máy',
    chassis_number VARCHAR(50) COMMENT 'Số khung/số VIN',

    registration_date DATE COMMENT 'Ngày đăng ký lần đầu',
    inspection_expiry DATE COMMENT 'Ngày hết hạn đăng kiểm',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo record',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',

    PRIMARY KEY (license_plate),

    CONSTRAINT chk_manufacture_year 
        CHECK (manufacture_year >= 1900 AND manufacture_year <= YEAR(CURDATE()) + 1),

    CONSTRAINT chk_license_plate_not_empty 
        CHECK (LENGTH(TRIM(license_plate)) > 0),

    CONSTRAINT chk_owner_name_not_empty 
        CHECK (LENGTH(TRIM(owner_name)) > 0)
) ENGINE=InnoDB 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci 
  COMMENT='Bảng lưu trữ thông tin phương tiện giao thông';

CREATE INDEX idx_vehicle_type ON vehicle(vehicle_type);

CREATE INDEX idx_owner_name ON vehicle(owner_name);

CREATE INDEX idx_brand ON vehicle(brand);

CREATE INDEX idx_manufacture_year ON vehicle(manufacture_year);

CREATE INDEX idx_inspection_expiry ON vehicle(inspection_expiry);

CREATE INDEX idx_type_brand_year ON vehicle(vehicle_type, brand, manufacture_year);

DELIMITER $$

CREATE TRIGGER trg_vehicle_before_update
    BEFORE UPDATE ON vehicle
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

DELIMITER ;

INSERT INTO vehicle (license_plate, vehicle_type, brand, model, manufacture_year, owner_name, color, engine_number, chassis_number, registration_date, inspection_expiry) VALUES
('30A-12345', 'Ô tô', 'Toyota', 'Camry', 2020, 'Nguyễn Văn A', 'Trắng', 'TYT20001', 'VIN20001', '2020-01-15', '2025-01-15'),
('29B-67890', 'Ô tô', 'Honda', 'Civic', 2019, 'Trần Thị B', 'Đen', 'HND19001', 'VIN19001', '2019-03-20', '2024-03-20'),
('51F-11111', 'Xe máy', 'Honda', 'Wave Alpha', 2021, 'Lê Văn C', 'Đỏ', 'HND21001', 'CHS21001', '2021-05-10', '2026-05-10'),
('59K-22222', 'Xe máy', 'Yamaha', 'Exciter', 2022, 'Phạm Thị D', 'Xanh', 'YMH22001', 'CHS22001', '2022-02-28', '2027-02-28'),
('77S-33333', 'Xe tải', 'Hyundai', 'HD65', 2018, 'Hoàng Văn E', 'Xám', 'HYD18001', 'VIN18001', '2018-07-12', '2023-07-12'),
('43C-44444', 'Ô tô', 'Mazda', 'CX-5', 2023, 'Vũ Thị F', 'Bạc', 'MZD23001', 'VIN23001', '2023-01-05', '2028-01-05'),
('61D-55555', 'Xe máy', 'Suzuki', 'Raider', 2020, 'Đỗ Văn G', 'Đen', 'SUZ20001', 'CHS20001', '2020-09-15', '2025-09-15'),
('14A-66666', 'Ô tô', 'Kia', 'Morning', 2021, 'Bùi Thị H', 'Trắng', 'KIA21001', 'VIN21001', '2021-11-30', '2026-11-30');

CREATE VIEW v_vehicles_expiring_soon AS
SELECT 
    license_plate,
    vehicle_type,
    brand,
    model,
    owner_name,
    inspection_expiry,
    DATEDIFF(inspection_expiry, CURDATE()) AS days_until_expiry
FROM vehicle 
WHERE inspection_expiry IS NOT NULL 
  AND inspection_expiry BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER BY inspection_expiry;

CREATE VIEW v_vehicle_stats_by_type AS
SELECT 
    vehicle_type,
    COUNT(*) as total_count,
    AVG(manufacture_year) as avg_year,
    MIN(manufacture_year) as oldest_year,
    MAX(manufacture_year) as newest_year
FROM vehicle 
GROUP BY vehicle_type
ORDER BY total_count DESC;

CREATE VIEW v_vehicle_stats_by_brand AS
SELECT 
    brand,
    COUNT(*) as total_count,
    COUNT(DISTINCT vehicle_type) as type_variety,
    AVG(manufacture_year) as avg_year
FROM vehicle 
GROUP BY brand
ORDER BY total_count DESC;

DELIMITER $$

CREATE PROCEDURE sp_search_vehicles(
    IN p_keyword VARCHAR(255),
    IN p_vehicle_type VARCHAR(50),
    IN p_brand VARCHAR(100),
    IN p_from_year INT,
    IN p_to_year INT
)
BEGIN
    SELECT * FROM vehicle
    WHERE (p_keyword IS NULL OR 
           license_plate LIKE CONCAT('%', p_keyword, '%') OR
           owner_name LIKE CONCAT('%', p_keyword, '%') OR
           model LIKE CONCAT('%', p_keyword, '%'))
      AND (p_vehicle_type IS NULL OR vehicle_type = p_vehicle_type)
      AND (p_brand IS NULL OR brand = p_brand)
      AND (p_from_year IS NULL OR manufacture_year >= p_from_year)
      AND (p_to_year IS NULL OR manufacture_year <= p_to_year)
    ORDER BY license_plate;
END$$

CREATE PROCEDURE sp_get_vehicle_statistics()
BEGIN
    SELECT 
        COUNT(*) as total_vehicles,
        COUNT(DISTINCT vehicle_type) as total_types,
        COUNT(DISTINCT brand) as total_brands,
        AVG(manufacture_year) as avg_year,
        COUNT(CASE WHEN inspection_expiry < CURDATE() THEN 1 END) as expired_inspection_count
    FROM vehicle;
END$$

DELIMITER ;

SHOW CREATE TABLE vehicle;
SHOW INDEX FROM vehicle;
SELECT COUNT(*) as 'Total records' FROM vehicle;