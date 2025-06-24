/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cuahangxe.quanlycuahangxe.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author gunnguyen
 */
public class NewConnectPostgres {

    private static final String URL = com.cuahangxe.quanlycuahangxe.config.LoadEnv.getDbUrl();
    private static final String USERNAME = com.cuahangxe.quanlycuahangxe.config.LoadEnv.getDbUser();
    private static final String PASSWORD = com.cuahangxe.quanlycuahangxe.config.LoadEnv.getDbPassword();

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null) {
                Class.forName("org.postgresql.Driver");
                connection = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy PostgreSQL Driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Đã đóng kết nối database!");
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
}
