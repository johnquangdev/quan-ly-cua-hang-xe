package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.dao.UserDAO;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.Hash;
import com.quanlycuahangxe.utils.ServiceResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class UserServiceImpl implements UserService {

    private final DataSource dataSource;

    public UserServiceImpl() {
        this.dataSource = NewConnectPostgres.getDataSource();
    }

    @Override
    public ServiceResult<User> createUser(User currentUser, String username, String password,
            String fullName, String email, String roleName, String position, String phone) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            // Validate cơ bản
            if (username == null || username.isBlank() || password == null
                    || password.length() < 6) {
                return ServiceResult
                        .failure("Password phải đủ 6 kí tự, username không được để trống");
            }

            if (userDAO.isUsernameExists(conn, username)) {
                return ServiceResult.failure("Username đã tồn tại");
            }

            if (email != null && !email.isBlank() && userDAO.isEmailExists(conn, email)) {
                return ServiceResult.failure("Email đã tồn tại");
            }

            // Validate email phải đúng định dạng @gmail.com
            if (email == null || !email.matches("^[\\w.+\\-]+@gmail\\.com$")) {
                return ServiceResult.failure("Email phải đúng định dạng @gmail.com");
            }

            // Validate roleName
            String role = roleName.toUpperCase();
            if (!role.equals("ADMIN") && !role.equals("MANAGER") && !role.equals("STAFF")) {
                return ServiceResult.failure("Role không hợp lệ");
            }

            // Phân quyền tạo role dựa trên currentUser
            if (currentUser != null) {
                String creatorRole = currentUser.getRole().toUpperCase();

                switch (creatorRole) {
                    case "ADMIN":
                        if (role.equals("ADMIN")) {
                            return ServiceResult
                                    .failure("ADMIN không được tạo user với role ADMIN");
                        }
                        break;
                    case "MANAGER":
                        if (!role.equals("STAFF")) {
                            return ServiceResult
                                    .failure("MANAGER chỉ được tạo user với role STAFF");
                        }
                        break;
                    default:
                        return ServiceResult.failure("Bạn không có quyền tạo user");
                }
            } else {
                // Nếu currentUser null, không cho tạo ADMIN
                if (role.equals("ADMIN")) {
                    return ServiceResult.failure("Bạn không có quyền");
                }
            }
            // Hash password
            String hashed = Hash.HashPassword(password);
            if (hashed == null) {
                return ServiceResult.failure("Lỗi hash mật khẩu");
            }

            User user = new User(username, hashed, fullName, email, role, false);
            boolean created = userDAO.createUser(conn, user);

            if (!created) {
                return ServiceResult.failure("Tạo user thất bại");
            }

            return ServiceResult.success(user, "Tạo user thành công");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tạo user: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<User> getUserById(int id) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User u = userDAO.getUserById(conn, id);
            if (u == null) {
                return ServiceResult.failure("Không tìm thấy user");
            }
            return ServiceResult.success(u);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy user theo ID: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<User> getUserByUsername(String username) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User u = userDAO.getUserByUsername(conn, username);
            if (u == null) {
                return ServiceResult.failure("Không tìm thấy user");
            }
            return ServiceResult.success(u);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy user theo username: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<User>> getAllUsers() {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            return ServiceResult.success(userDAO.getAllUsers(conn));
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy tất cả user: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<User> authenticateUser(String username, String password) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User u = userDAO.getUserByUsername(conn, username);
            if (u == null || !Hash.HashPassword(password).equals(u.getPassword())) {
                return ServiceResult.failure("Username hoặc password không đúng");
            }
            if (u.isLocked()) {
                return ServiceResult.failure("Tài khoản đã bị khóa");
            }
            return ServiceResult.success(u, "Đăng nhập thành công");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi xác thực user: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> logout(User user) {
        return ServiceResult.success(null, "Đăng xuất thành công");
    }

    @Override
    public ServiceResult<Void> toggleUserLock(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User u = userDAO.getUserById(conn, userId);
            if (u == null) {
                return ServiceResult.failure("Không tìm thấy user");
            }
            u.setLocked(!u.isLocked());
            boolean updated = userDAO.updateUser(conn, u);
            return updated
                    ? ServiceResult.success(null, u.isLocked() ? "Đã khóa user" : "Đã mở khóa user")
                    : ServiceResult.failure("Cập nhật thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi khóa/mở khóa user: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<User> updateUser(int id, String fullName, String email, String roleName) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User u = userDAO.getUserById(conn, id);
            if (u == null) {
                return ServiceResult.failure("Không tìm thấy user");
            }

            if (fullName != null) {
                u.setFullName(fullName);
            }
            if (email != null) {
                u.setEmail(email);
            }
            if (roleName != null) {
                u.setRole(roleName);
            }

            boolean updated = userDAO.updateUser(conn, u);
            return updated ? ServiceResult.success(u, "Cập nhật thành công")
                    : ServiceResult.failure("Cập nhật thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi cập nhật user: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> deleteUser(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            boolean deleted = userDAO.deleteUser(conn, userId);
            return deleted ? ServiceResult.success(null, "Xóa user thành công")
                    : ServiceResult.failure("Xóa user thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi xóa user: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> changePassword(int userId, String oldPassword, String newPassword) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User u = userDAO.getUserById(conn, userId);
            if (u == null) {
                return ServiceResult.failure("Không tìm thấy user");
            }
            if (!Hash.HashPassword(oldPassword).equals(u.getPassword())) {
                return ServiceResult.failure("Mật khẩu cũ không đúng");
            }

            u.setPassword(Hash.HashPassword(newPassword));
            boolean updated = userDAO.updateUser(conn, u);
            return updated ? ServiceResult.success(null, "Đổi mật khẩu thành công")
                    : ServiceResult.failure("Đổi mật khẩu thất bại");
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi đổi mật khẩu: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<User>> searchUsers(String keyword) {
        try (Connection conn = dataSource.getConnection()) {
            UserDAO userDAO = new UserDAO();
            if (keyword == null || keyword.trim().isEmpty()) {
                return ServiceResult.success(userDAO.getAllUsers(conn));
            }
            return ServiceResult.success(userDAO.searchUsers(conn, keyword));
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tìm kiếm user: " + e.getMessage());
        }
    }
}
