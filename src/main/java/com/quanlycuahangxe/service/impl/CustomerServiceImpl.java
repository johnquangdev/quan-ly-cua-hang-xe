package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.dao.CustomerDAO;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.service.interfaces.CustomerService;
import com.quanlycuahangxe.utils.ServiceResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class CustomerServiceImpl implements CustomerService {

    private final DataSource dataSource;

    public CustomerServiceImpl() {
        this.dataSource = NewConnectPostgres.getDataSource();
    }

    @Override
    public ServiceResult<Customer> createCustomer(String fullName, String email, String phone,
            String address) {
        try (Connection conn = dataSource.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAO();
            // Validate tên
            if (fullName == null || fullName.isBlank()) {
                return ServiceResult.failure("Họ tên không được để trống");
            }

            // Validate email
            if (email == null || !email.matches("^[\\w.+\\-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                return ServiceResult.failure("Email không hợp lệ");
            }

            // Validate phone (10 chữ số, bắt đầu bằng 0)
            if (phone == null || !phone.matches("^0\\d{9}$")) {
                return ServiceResult.failure("Số điện thoại không hợp lệ");
            }

            // Check trùng email hoặc phone (nếu có các hàm này)
            if (customerDAO.isEmailExists(conn, email)) {
                return ServiceResult.failure("Email đã tồn tại");
            }

            if (customerDAO.isPhoneExists(conn, phone)) {
                return ServiceResult.failure("Số điện thoại đã tồn tại");
            }

            Customer customer = new Customer();
            customer.setFullName(fullName.trim());
            customer.setEmail(email.trim());
            customer.setPhone(phone.trim());
            customer.setAddress(address != null ? address.trim() : "");

            boolean success = customerDAO.createCustomer(conn, customer);

            if (success) {
                return ServiceResult.success(customer);
            } else {
                return ServiceResult.failure("Tạo khách hàng thất bại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tạo khách hàng: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Customer> getCustomerById(int id) {
        try (Connection conn = dataSource.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getCustomerById(conn, id);
            if (customer != null) {
                return ServiceResult.success(customer);
            } else {
                return ServiceResult.failure("Customer not found with id: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy khách hàng: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<Customer>> getAllCustomers() {
        try (Connection conn = dataSource.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customers = customerDAO.getAllCustomers(conn);
            return ServiceResult.success(customers);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy danh sách khách hàng: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Customer> updateCustomer(int id, String fullName, String email,
            String phone, String address) {
        try (Connection conn = dataSource.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getCustomerById(conn, id);
            if (customer == null) {
                return ServiceResult.failure("Không tìm thấy khách hàng với ID: " + id);
            }

            // Validate tương tự như khi tạo
            if (fullName == null || fullName.isBlank()) {
                return ServiceResult.failure("Họ tên không được để trống");
            }

            if (email == null || !email.matches("^[\\w.+\\-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                return ServiceResult.failure("Email không hợp lệ");
            }

            if (phone == null || !phone.matches("^0\\d{9}$")) {
                return ServiceResult.failure("Số điện thoại không hợp lệ");
            }

            // Kiểm tra email/phone trùng với khách khác
            Customer byEmail = customerDAO.getCustomerByEmail(conn, email);
            if (byEmail != null && byEmail.getId() != id) {
                return ServiceResult.failure("Email đã được sử dụng bởi khách hàng khác");
            }

            Customer byPhone = customerDAO.getCustomerByPhone(conn, phone);
            if (byPhone != null && byPhone.getId() != id) {
                return ServiceResult.failure("Số điện thoại đã được sử dụng bởi khách hàng khác");
            }

            customer.setFullName(fullName.trim());
            customer.setEmail(email.trim());
            customer.setPhone(phone.trim());
            customer.setAddress(address != null ? address.trim() : "");

            boolean success = customerDAO.updateCustomer(conn, customer);
            if (success) {
                return ServiceResult.success(customer);
            } else {
                return ServiceResult.failure("Cập nhật khách hàng thất bại với ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi cập nhật khách hàng: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> deleteCustomer(int id) {
        try (Connection conn = dataSource.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAO();
            boolean success = customerDAO.deleteCustomer(conn, id);
            if (success) {
                return ServiceResult.success(null);
            } else {
                return ServiceResult.failure("Vui lòng xoá hoá đơn của khách trước: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi xóa khách hàng: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<Customer>> searchCustomers(String keyword) {
        try (Connection conn = dataSource.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customers = customerDAO.searchCustomers(conn, keyword);
            return ServiceResult.success(customers);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi khi tìm kiếm khách hàng: " + e.getMessage());
        }
    }
}
