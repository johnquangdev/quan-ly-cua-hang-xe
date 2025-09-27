package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.dao.CategoryDAO;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.Category;
import com.quanlycuahangxe.service.interfaces.CategoryService;
import com.quanlycuahangxe.utils.ServiceResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class CategoryServiceImpl implements CategoryService {

    private final DataSource dataSource;

    public CategoryServiceImpl() {
        this.dataSource = NewConnectPostgres.getDataSource();
    }

    @Override
    public ServiceResult<Category> createCategory(String name, String description) {
        try (Connection conn = dataSource.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO();
            if (name == null || name.isBlank()) {
                return ServiceResult.failure("Tên category không được để trống");
            }
            Category c = new Category(name, description);
            boolean created = categoryDAO.createCategory(conn, c);
            return created ? ServiceResult.success(c, "Tạo category thành công")
                    : ServiceResult.failure("Tạo category thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tạo category: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Category> getCategoryById(int id) {
        try (Connection conn = dataSource.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO();
            Category c = categoryDAO.getCategoryById(conn, id);
            return c != null ? ServiceResult.success(c)
                    : ServiceResult.failure("Không tìm thấy category");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy category theo ID: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<Category>> getAllCategories() {
        try (Connection conn = dataSource.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO();
            return ServiceResult.success(categoryDAO.getAllCategories(conn));
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy tất cả category: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Category> updateCategory(int id, String name, String description) {
        try (Connection conn = dataSource.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO();
            Category c = categoryDAO.getCategoryById(conn, id);
            if (c == null) {
                return ServiceResult.failure("Không tìm thấy category");
            }
            if (name != null) {
                c.setName(name);
            }
            if (description != null) {
                c.setDescription(description);
            }
            boolean updated = categoryDAO.updateCategory(conn, c);
            return updated ? ServiceResult.success(c, "Cập nhật thành công")
                    : ServiceResult.failure("Cập nhật thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi cập nhật category: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> deleteCategory(int id) {
        try (Connection conn = dataSource.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO();
            boolean deleted = categoryDAO.deleteCategory(conn, id);
            return deleted ? ServiceResult.success(null, "Xóa category thành công")
                    : ServiceResult.failure("Xóa category thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi xóa category: " + e.getMessage());
        }
    }
}
