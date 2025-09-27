package com.quanlycuahangxe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.quanlycuahangxe.model.Brand;

public class BrandDAO {

    // CRUD Methods

    public boolean createBrand(Connection connection, Brand brand) {
        String sql = "INSERT INTO brands (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getDescription());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    brand.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Brand getBrandById(Connection connection, int id) {
        String sql = "SELECT * FROM brands WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Brand> getAllBrands(Connection connection) {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM brands ORDER BY name ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateBrand(Connection connection, Brand brand) {
        String sql = "UPDATE brands SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getDescription());
            stmt.setInt(3, brand.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBrand(Connection connection, int id) {
        String sql = "DELETE FROM brands WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper Methods
    private Brand mapResultSet(ResultSet rs) throws SQLException {
        return new Brand(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
    }
}
