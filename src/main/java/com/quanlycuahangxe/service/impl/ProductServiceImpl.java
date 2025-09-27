package com.quanlycuahangxe.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.quanlycuahangxe.dao.BrandDAO;
import com.quanlycuahangxe.dao.CategoryDAO;
import com.quanlycuahangxe.dao.ProductDAO;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.Brand;
import com.quanlycuahangxe.model.Category;
import com.quanlycuahangxe.model.Product;
import com.quanlycuahangxe.service.interfaces.ProductService;
import com.quanlycuahangxe.utils.ServiceResult;

public class ProductServiceImpl implements ProductService {

    private final DataSource dataSource;

    public ProductServiceImpl() {
        this.dataSource = NewConnectPostgres.getDataSource();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    // Product 
    @Override
    public ServiceResult<Product> createProduct(String name, int brandId, int categoryId,
            double price, int stockQty, String description) {
        try (Connection conn = dataSource.getConnection()) {
            ProductDAO productDAO = new ProductDAO();
            Product p = new Product(0, name, brandId, categoryId, price, stockQty, description);
            boolean created = productDAO.create(conn, p);
            return created ? ServiceResult.success(p, "Tạo sản phẩm thành công")
                    : ServiceResult.failure("Tạo sản phẩm thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tạo sản phẩm: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Product> getProductById(int id) {
        try (Connection conn = dataSource.getConnection()) {
            return getProductById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy sản phẩm theo ID: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Product> getProductById(Connection conn, int id) {
        ProductDAO productDAO = new ProductDAO();
        Product p = productDAO.getById(conn, id);
        return p != null ? ServiceResult.success(p)
                : ServiceResult.failure("Không tìm thấy sản phẩm");
    }

    @Override
    public ServiceResult<List<Product>> getAllProducts() {
        try (Connection conn = dataSource.getConnection()) {
            return getAllProducts(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy tất cả sản phẩm: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<Product>> getAllProducts(Connection conn) {
        ProductDAO productDAO = new ProductDAO();
        List<Product> list = productDAO.getAll(conn);
        return ServiceResult.success(list);
    }

    @Override
    public ServiceResult<Product> updateProduct(int id, String name, int brandId, int categoryId,
            double price, String description) {
        try (Connection conn = dataSource.getConnection()) {
            return updateProduct(conn, id, name, brandId, categoryId, price, description);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi cập nhật sản phẩm: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Product> updateProduct(Connection conn, int id, String name, int brandId,
            int categoryId, double price, String description) {
        ProductDAO productDAO = new ProductDAO();
        Product p = productDAO.getById(conn, id);
        if (p == null) {
            return ServiceResult.failure("Không tìm thấy sản phẩm");
        }

        p.setName(name);
        p.setBrandId(brandId);
        p.setCategoryId(categoryId);
        p.setPrice(price);
        p.setDescription(description);

        boolean updated = productDAO.update(conn, p);
        return updated ? ServiceResult.success(p, "Cập nhật thành công")
                : ServiceResult.failure("Cập nhật thất bại");
    }

    @Override
    public ServiceResult<Void> deleteProduct(int id) {
        try (Connection conn = dataSource.getConnection()) {
            return deleteProduct(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi xóa sản phẩm: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> deleteProduct(Connection conn, int id) {
        ProductDAO productDAO = new ProductDAO();
        boolean deleted = productDAO.delete(conn, id);
        return deleted ? ServiceResult.success(null, "Xóa sản phẩm thành công")
                : ServiceResult.failure("Xóa sản phẩm thất bại");
    }

    @Override
    public ServiceResult<Product> updateStock(int productId, int changeAmount) {
        try (Connection conn = dataSource.getConnection()) {
            return updateStock(conn, productId, changeAmount);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi cập nhật tồn kho: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Product> updateStock(Connection conn, int productId, int changeAmount) {
        ProductDAO productDAO = new ProductDAO();
        Product p = productDAO.getById(conn, productId);
        if (p == null) {
            return ServiceResult.failure("Không tìm thấy sản phẩm");
        }

        p.setStockQuantity(p.getStockQuantity() + changeAmount);
        boolean updated = productDAO.update(conn, p);

        return updated ? ServiceResult.success(p, "Cập nhật tồn kho thành công")
                : ServiceResult.failure("Cập nhật tồn kho thất bại");
    }

    //  Category & Brand 
    public ServiceResult<List<Category>> getAllCategories() {
        try (Connection conn = dataSource.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO();
            return ServiceResult.success(categoryDAO.getAllCategories(conn));
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy tất cả danh mục: " + e.getMessage());
        }
    }

    public ServiceResult<List<Brand>> getAllBrands() {
        try (Connection conn = dataSource.getConnection()) {
            BrandDAO brandDAO = new BrandDAO();
            return ServiceResult.success(brandDAO.getAllBrands(conn));
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy tất cả thương hiệu: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<Product>> searchProducts(String keyword) {
        try (Connection conn = dataSource.getConnection()) {
            ProductDAO productDAO = new ProductDAO();
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllProducts();
            }
            return ServiceResult.success(productDAO.searchProducts(conn, keyword));
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tìm kiếm sản phẩm: " + e.getMessage());
        }
    }
}
