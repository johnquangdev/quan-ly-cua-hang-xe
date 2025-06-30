/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.Staff;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gunnguyen
 */
public class StaffDAO {

    private Connection connection;

    public StaffDAO() {
        this.connection = NewConnectPostgres.getConnection();
    }

    // Tạo staff mới
    public boolean createStaff(Staff staff) {
        String sql = "INSERT INTO staffs (user_id, position, phone) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, staff.getUserId());
            stmt.setString(2, staff.getPosition());
            stmt.setString(3, staff.getPhone());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    staff.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy staff theo user_id
    public Staff getStaffByUserId(int userId) {
        String sql = "SELECT * FROM staffs WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Staff staff = new Staff();
                staff.setId(rs.getInt("id"));
                staff.setUserId(rs.getInt("user_id"));
                staff.setPosition(rs.getString("position"));
                staff.setPhone(rs.getString("phone"));
                return staff;
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteStaff(int userId) {
        String sql = "DELETE FROM staffs WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
