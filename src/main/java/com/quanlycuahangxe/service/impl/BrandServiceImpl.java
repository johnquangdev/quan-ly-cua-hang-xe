/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.impl;

/**
 *
 * @author gunnguyen
 */
import com.quanlycuahangxe.dao.BrandDAO;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.Brand;
import com.quanlycuahangxe.service.interfaces.BrandService;
import com.quanlycuahangxe.utils.ServiceResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class BrandServiceImpl implements BrandService {

    private final DataSource dataSource;

    public BrandServiceImpl() {
        this.dataSource = NewConnectPostgres.getDataSource();
    }

    @Override
    public ServiceResult<Brand> createBrand(String name, String description) {
        try (Connection conn = dataSource.getConnection()) {
            BrandDAO brandDAO = new BrandDAO();
            Brand brand = new Brand(); // dùng constructor mặc định
            brand.setName(name);
            brand.setDescription(description);

            boolean created = brandDAO.createBrand(conn, brand);
            return created ? ServiceResult.success(brand, "Tạo brand thành công")
                    : ServiceResult.failure("Tạo brand thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tạo brand: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Brand> getBrandById(int id) {
        try (Connection conn = dataSource.getConnection()) {
            BrandDAO brandDAO = new BrandDAO();
            Brand b = brandDAO.getBrandById(conn, id);
            return b != null ? ServiceResult.success(b)
                    : ServiceResult.failure("Không tìm thấy brand");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy brand theo ID: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<Brand>> getAllBrands() {
        try (Connection conn = dataSource.getConnection()) {
            BrandDAO brandDAO = new BrandDAO();
            return ServiceResult.success(brandDAO.getAllBrands(conn));
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy tất cả brand: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Brand> updateBrand(int id, String name, String description) {
        try (Connection conn = dataSource.getConnection()) {
            BrandDAO brandDAO = new BrandDAO();
            Brand b = brandDAO.getBrandById(conn, id);
            if (b == null) {
                return ServiceResult.failure("Không tìm thấy brand");
            }

            if (name != null) {
                b.setName(name);
            }
            if (description != null) {
                b.setDescription(description);
            }

            boolean updated = brandDAO.updateBrand(conn, b);
            return updated ? ServiceResult.success(b, "Cập nhật thành công")
                    : ServiceResult.failure("Cập nhật thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi cập nhật brand: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> deleteBrand(int id) {
        try (Connection conn = dataSource.getConnection()) {
            BrandDAO brandDAO = new BrandDAO();
            boolean deleted = brandDAO.deleteBrand(conn, id);
            return deleted ? ServiceResult.success(null, "Xóa brand thành công")
                    : ServiceResult.failure("Xóa brand thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi xóa brand: " + e.getMessage());
        }
    }
}
