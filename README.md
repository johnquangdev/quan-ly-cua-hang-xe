---

### Dự án: Quản Lý Bán Hàng (Java Swing)

### Công Nghệ Sử Dụng:
- Java (JDK 17)
- Java Swing (Giao diện người dùng)
- NetBeans IDE 26
- JDBC (kết nối cơ sở dữ liệu)
- Git (quản lý phiên bản)
- Database (Postgres)
- Cấu hình kết nối cơ sở dữ liệu bằng `.env`

## Đặt tên theo chuẩn Java (naming convention)

### Tổng hợp
| Loại         | Quy tắc                             | Ví dụ                                   |
| ------------ | ----------------------------------- | --------------------------------------- |
| Class        | PascalCase                          | `LoginForm`, `ProductServiceImpl`       |
| Interface    | PascalCase, thường bắt đầu bằng `I` | `IUserService`, `IProductDAO`           |
| Method       | camelCase                           | `loginUser()`, `validateInput()`        |
| Biến thường  | camelCase                           | `userList`, `invoiceId`                 |
| Constant     | UPPER\_SNAKE\_CASE                  | `MAX_LOGIN_ATTEMPTS`                    |
| Component UI | `prefix` + Pascal                   | `btnLogin`, `txtUsername`, `tblProduct` |
Component UI – theo kiểu Hungarian Notation biến thể
| Tiền tố | Loại component |
| ------- | -------------- |
| `btn`   | JButton        |
| `txt`   | JTextField     |
| `lbl`   | JLabel         |
| `cb`    | JComboBox      |
| `tbl`   | JTable         |
| `pnl`   | JPanel         |
| `chk`   | JCheckBox      |
| `rdo`   | JRadioButton   |
