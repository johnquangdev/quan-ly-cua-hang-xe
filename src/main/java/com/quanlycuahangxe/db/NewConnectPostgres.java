package com.quanlycuahangxe.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.quanlycuahangxe.config.LoadEnv;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class NewConnectPostgres {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(LoadEnv.getDbUrl()); // Cấu hình URL database
            config.setUsername(LoadEnv.getDbUser()); // Cấu hình tên người dùng database
            config.setPassword(LoadEnv.getDbPassword()); // Cấu hình mật khẩu database
            config.setDriverClassName("org.postgresql.Driver");

            // Cấu hình pool kết nối
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(60000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(600000);

            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP DataSource initialized successfully.");
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo DataSource: " + e.getMessage()); // Ghi log lỗi
            e.printStackTrace();
        }
    }

    // Lấy kết nối từ pool
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("Lỗi lấy kết nối từ pool: " + e.getMessage()); // Ghi log lỗi
            e.printStackTrace();
            return null;
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    // Đóng kết nối (trả về pool)
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // Trả kết nối về pool
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage()); // Ghi log lỗi
                e.printStackTrace();
            }
        }
    }

    // Đóng datasource khi ứng dụng tắt
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("DataSource HikariCP closed.");
        }
    }
}
