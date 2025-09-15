/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.model.SystemManagement;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.exception.DuplicateSystemManagementException;
import com.quanlycuahangxe.exception.SystemManagementFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Minh
 */
public class SystemManagementDao {
    private final Connection conn;

    public SystemManagementDao() {
        this.conn = NewConnectPostgres.getConnection();
    }

    public SystemManagement createSystem(SystemManagement system) throws DuplicateSystemManagementException {
        try {
            if (existsByName(system.getSystemName())) {
                throw DuplicateSystemManagementException.duplicateName(system.getSystemName());
            }

            String sql = "INSERT INTO system_management (system_name, description, is_active, created_at, updated_at) " +
                         "VALUES (?, ?, ?, ?, ?) RETURNING id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, system.getSystemName());
            ps.setString(2, system.getDescription());
            ps.setBoolean(3, system.isActive());
            ps.setTimestamp(4, Timestamp.valueOf(system.getCreatedAt()));
            ps.setTimestamp(5, Timestamp.valueOf(system.getUpdatedAt()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                system.setId(rs.getLong("id"));
            }
            rs.close();
            ps.close();
            return system;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo SystemManagement: " + e.getMessage(), e);
        }
    }

    public SystemManagement getSystemById(Long id) throws SystemManagementFoundException {
        try {
            String sql = "SELECT * FROM system_management WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SystemManagement sm = map(rs);
                rs.close(); ps.close();
                return sm;
            } else {
                throw SystemManagementFoundException.withId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy SystemManagement: " + e.getMessage(), e);
        }
    }

    public List<SystemManagement> getAllSystems() {
        List<SystemManagement> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM system_management ORDER BY created_at DESC";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(map(rs));
            }
            rs.close(); st.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách SystemManagement: " + e.getMessage(), e);
        }
        return list;
    }

    public SystemManagement updateSystem(Long id, SystemManagement system) throws SystemManagementFoundException {
        if (!existsById(id)) {
            throw SystemManagementFoundException.withId(id);
        }
        try {
            String sql = "UPDATE system_management SET system_name=?, description=?, is_active=?, updated_at=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, system.getSystemName());
            ps.setString(2, system.getDescription());
            ps.setBoolean(3, system.isActive());
            ps.setTimestamp(4, Timestamp.valueOf(system.getUpdatedAt()));
            ps.setLong(5, id);

            ps.executeUpdate();
            ps.close();
            return getSystemById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật SystemManagement: " + e.getMessage(), e);
        }
    }

    public void deleteSystem(Long id) throws SystemManagementFoundException {
        if (!existsById(id)) {
            throw SystemManagementFoundException.withId(id);
        }
        try {
            String sql = "DELETE FROM system_management WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa SystemManagement: " + e.getMessage(), e);
        }
    }

    public boolean existsByName(String systemName) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM system_management WHERE LOWER(system_name) = LOWER(?)");
            ps.setString(1, systemName);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close(); ps.close();
            return exists;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existsById(Long id) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM system_management WHERE id=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close(); ps.close();
            return exists;
        } catch (SQLException e) {
            return false;
        }
    }

    private SystemManagement map(ResultSet rs) throws SQLException {
        SystemManagement sm = new SystemManagement();
        sm.setId(rs.getLong("id"));
        sm.setSystemName(rs.getString("system_name"));
        sm.setDescription(rs.getString("description"));
        sm.setActive(rs.getBoolean("is_active"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) sm.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) sm.setUpdatedAt(updated.toLocalDateTime());
        return sm;
    }
}