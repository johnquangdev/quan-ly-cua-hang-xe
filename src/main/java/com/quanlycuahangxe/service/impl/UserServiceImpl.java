/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.impl;

/**
 *
 * @author gunnguyen
 */
import com.quanlycuahangxe.dao.RoleDAO;
import com.quanlycuahangxe.dao.StaffDAO;
import com.quanlycuahangxe.dao.UserDAO;
import com.quanlycuahangxe.model.Role;
import com.quanlycuahangxe.model.Staff;
import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.Hash;
import com.quanlycuahangxe.utils.ServiceResult;
import java.util.List;
import java.util.regex.Pattern;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private StaffDAO staffDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
        this.staffDAO = new StaffDAO();
    }
    // Email pattern để validate
    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    public UserServiceImpl(UserDAO userDAO, RoleDAO roleDAO, StaffDAO staffDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.staffDAO = staffDAO;
    }

    @Override
    public ServiceResult<User> createUser(String username, String password, String fullName,
            String email, String roleName, String position, String phone) {
        // Giả định: Admin toàn quyền
        User admin = new User();
        admin.setRoleId(1); // role_id = 1 nghĩa là ADMIN
        // Nếu là MANAGER thì position là
        if ("MANAGER".equalsIgnoreCase(roleName) && (position == null || position.trim().isEmpty())) {
            position = "Quản lý chi nhánh";
        }

        // Nếu là STAFF thì position là
        if ("STAFF".equalsIgnoreCase(roleName) && (position == null || position.trim().isEmpty())) {
            position = "Nhân viên bán hàng";
        }
        return createUser(admin, username, password, fullName, email, roleName, position, phone);
    }

    @Override
    public ServiceResult<User> createUser(User currentUser, String username, String password, String fullName,
            String email, String roleName, String position, String phone) {
        // Validate đầu vào
        ServiceResult<Void> validationResult = validateUserInput(username, password, fullName, email, roleName);
        if (!validationResult.isSuccess()) {
            return ServiceResult.failure(validationResult.getMessage());
        }

        // Lấy role hiện tại của người tạo
        Role currentRole = roleDAO.getRoleById(currentUser.getRoleId());
        if (currentRole == null) {
            return ServiceResult.failure("Không xác định được vai trò người tạo");
        }

        // Phân quyền tạo user
        if ("MANAGER".equalsIgnoreCase(currentRole.getName())) {
            if (!"STAFF".equalsIgnoreCase(roleName)) {
                return ServiceResult.failure("MANAGER chỉ được tạo STAFF");
            }
        } else if ("STAFF".equalsIgnoreCase(currentRole.getName())) {
            return ServiceResult.failure("STAFF không được phép tạo user");
        }

        // Kiểm tra trùng username/email
        if (userDAO.isUsernameExists(username)) {
            return ServiceResult.failure("Username đã tồn tại");
        }
        if (email != null && !email.trim().isEmpty() && userDAO.isEmailExists(email)) {
            return ServiceResult.failure("Email đã tồn tại");
        }

        // Lấy role ID
        Role role = roleDAO.getRoleByName(roleName);
        if (role == null) {
            return ServiceResult.failure("Role không tồn tại");
        }

        // Mã hóa mật khẩu
        String hashedPassword = Hash.HashPassword(password);
        if (hashedPassword == null) {
            return ServiceResult.failure("Không thể mã hóa mật khẩu");
        }

        // Tạo user object
        User user = new User(username, hashedPassword, fullName, email, role.getId(), false);

        // Lưu user vào DB
        boolean userCreated = userDAO.createUser(user);
        if (!userCreated) {
            return ServiceResult.failure("Không thể tạo user");
        }

        // Lấy lại ID user nếu cần
        int newUserId = userDAO.getUserIdByUsername(username);
        user.setId(newUserId);

        // Nếu là STAFF hoặc MANAGER → tạo record trong bảng staffs
        if (("STAFF".equalsIgnoreCase(roleName) || "MANAGER".equalsIgnoreCase(roleName)) && position != null) {
            Staff staff = new Staff(newUserId, position, phone);
            boolean staffCreated = staffDAO.createStaff(staff);
            if (!staffCreated) {
                userDAO.deleteUser(newUserId);
                return ServiceResult.failure("Không thể tạo thông tin staff");
            }
        }

        return ServiceResult.success(user, "Tạo user thành công");
    }

    @Override
    public ServiceResult<User> getUserById(int id) {
        if (id <= 0) {
            return ServiceResult.failure("ID không hợp lệ");
        }

        User user = userDAO.getUserById(id);
        if (user == null) {
            return ServiceResult.failure("Không tìm thấy user");
        }

        return ServiceResult.success(user);
    }

    @Override
    public ServiceResult<User> getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ServiceResult.failure("Username không được rỗng");
        }

        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            return ServiceResult.failure("Không tìm thấy user");
        }

        return ServiceResult.success(user);
    }

    @Override
    public ServiceResult<List<User>> getAllUsers() {
        List<User> users = userDAO.getAllUsers();
        return ServiceResult.success(users);
    }

    @Override
    public ServiceResult<User> authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return ServiceResult.failure("Username không được rỗng");
        }

        if (password == null || password.isEmpty()) {
            return ServiceResult.failure("Password không được rỗng");
        }

        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            return ServiceResult.failure("Username hoặc password không đúng");
        }

        if (user.isLocked()) {
            return ServiceResult.failure("Tài khoản đã bị khóa");
        }

        String hashedPassword = Hash.HashPassword(password);
        if (hashedPassword == null || !hashedPassword.equals(user.getPassword())) {
            return ServiceResult.failure("Username hoặc password không đúng");
        }

        return ServiceResult.success(user, "Đăng nhập thành công");
    }

    @Override
    public ServiceResult<Void> toggleUserLock(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return ServiceResult.failure("Không tìm thấy user");
        }

        user.setLocked(!user.isLocked());
        boolean updated = userDAO.updateUser(user);

        if (updated) {
            String message = user.isLocked() ? "Đã khóa user" : "Đã mở khóa user";
            return ServiceResult.success(null, message);
        } else {
            return ServiceResult.failure("Không thể cập nhật trạng thái user");
        }
    }

    @Override
    public ServiceResult<User> updateUser(int id, String fullName, String email, String roleName) {
        User user = userDAO.getUserById(id);
        if (user == null) {
            return ServiceResult.failure("Không tìm thấy user");
        }

        // Validate input
        if (fullName != null && !fullName.trim().isEmpty()) {
            if (fullName.length() > 100) {
                return ServiceResult.failure("Họ tên không được vượt quá 100 ký tự");
            }
            user.setFullName(fullName);
        }

        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return ServiceResult.failure("Email không đúng định dạng");
            }
            if (email.length() > 100) {
                return ServiceResult.failure("Email không được vượt quá 100 ký tự");
            }
            // Kiểm tra email đã tồn tại (trừ user hiện tại)
            User existingUser = userDAO.getUserByEmail(email);
            if (existingUser != null && existingUser.getId() != id) {
                return ServiceResult.failure("Email đã được sử dụng");
            }
            user.setEmail(email);
        }

        if (roleName != null && !roleName.trim().isEmpty()) {
            Role role = roleDAO.getRoleByName(roleName);
            if (role == null) {
                return ServiceResult.failure("Role không tồn tại");
            }
            user.setRoleId(role.getId());
        }

        boolean updated = userDAO.updateUser(user);
        if (updated) {
            return ServiceResult.success(user, "Cập nhật user thành công");
        } else {
            return ServiceResult.failure("Không thể cập nhật user");
        }
    }

    @Override
    public ServiceResult<Void> deleteUser(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return ServiceResult.failure("Không tìm thấy user");
        }

        // Xóa staff trước (nếu có)
        Staff staff = staffDAO.getStaffByUserId(userId);
        if (staff != null) {
            staffDAO.deleteStaff(userId);
        }
        // Xóa user
        boolean deleted = userDAO.deleteUser(userId);
        if (deleted) {
            return ServiceResult.success(null, "Xóa user thành công");
        } else {
            return ServiceResult.failure("Không thể xóa user");
        }
    }

    @Override
    public ServiceResult<Void> changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return ServiceResult.failure("Không tìm thấy user");
        }

        // Validate old password
        String hashedOldPassword = Hash.HashPassword(oldPassword);
        if (hashedOldPassword == null || !hashedOldPassword.equals(user.getPassword())) {
            return ServiceResult.failure("Mật khẩu cũ không đúng");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            return ServiceResult.failure("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        // Hash new password
        String hashedNewPassword = Hash.HashPassword(newPassword);
        if (hashedNewPassword == null) {
            return ServiceResult.failure("Lỗi mã hóa mật khẩu mới");
        }

        user.setPassword(hashedNewPassword);
        boolean updated = userDAO.updateUser(user);

        if (updated) {
            return ServiceResult.success(null, "Đổi mật khẩu thành công");
        } else {
            return ServiceResult.failure("Không thể đổi mật khẩu");
        }
    }

    // Validation helper methods
    private ServiceResult<Void> validateUserInput(String username, String password, String fullName,
            String email, String roleName) {
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            return ServiceResult.failure("Username không được rỗng");
        }
        if (username.length() < 3 || username.length() > 100) {
            return ServiceResult.failure("Username phải có độ dài từ 3-100 ký tự");
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            return ServiceResult.failure("Password không được rỗng");
        }
        if (password.length() < 6) {
            return ServiceResult.failure("Password phải có ít nhất 6 ký tự");
        }

        // Validate full name
        if (fullName == null || fullName.trim().isEmpty()) {
            return ServiceResult.failure("Họ tên không được rỗng");
        }
        if (fullName.length() > 100) {
            return ServiceResult.failure("Họ tên không được vượt quá 100 ký tự");
        }

        // Validate email (optional)
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return ServiceResult.failure("Email không đúng định dạng");
            }
            if (email.length() > 100) {
                return ServiceResult.failure("Email không được vượt quá 100 ký tự");
            }
        }

        // Validate role
        if (roleName == null || roleName.trim().isEmpty()) {
            return ServiceResult.failure("Role không được rỗng");
        }
        if (!"ADMIN".equals(roleName) && !"MANAGER".equals(roleName) && !"STAFF".equals(roleName)) {
            return ServiceResult.failure("Role không hợp lệ");
        }

        return ServiceResult.success(null);
    }

    @Override
    public ServiceResult<Void> logout(User user) {
        // check status
        if (user == null || user.getId() <= 0 || user.getUsername() == null) {
            return ServiceResult.failure("Không có user hợp lệ đang đăng nhập");
        }
        return ServiceResult.success(null, "Đăng xuất thành công");
    }
}
