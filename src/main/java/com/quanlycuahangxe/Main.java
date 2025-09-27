package com.quanlycuahangxe;

import java.sql.Connection;

import javax.swing.SwingUtilities;

import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.gui.LoginForm;

public class Main {

    public static void main(String[] args) {
        Connection conn = NewConnectPostgres.getConnection();
        if (conn != null) {
            System.out.println("Connected to PostgreSQL!");
        } else {
            System.out.println("Connection failed!");
            return;
        }
        // làm mới database
        FlywayMigrator.runMigration(); // Migration database
        // mở cửa sổ login
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true)); // Mở cửa sổ đăng nhập
    }
}
