# Hệ thống Quản lý Cửa hàng Xe

Đây là một ứng dụng quản lý cửa hàng xe được phát triển bằng Java Swing, cung cấp các chức năng quản lý sản phẩm, khách hàng, hóa đơn và nhân sự.

## Mục lục
- [Giới thiệu](#giới-thiệu)
- [Tính năng chính](#tính-năng-chính)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Điểm nổi bật kỹ thuật](#điểm-nổi-bật-kỹ-thuật)
- [Luồng hoạt động chính](#luồng-hoạt-động-chính)
- [Cài đặt và Chạy ứng dụng](#cài-đặt-và-chạy-ứng-dụng)

## Giới thiệu
Ứng dụng này được thiết kế để hỗ trợ việc quản lý các hoạt động hàng ngày của một cửa hàng xe, từ quản lý thông tin sản phẩm, khách hàng, đến việc tạo và quản lý hóa đơn, cũng như quản lý nhân sự với phân quyền rõ ràng.

## Tính năng chính

### 1. Quản lý Người dùng/Nhân sự
- **Đăng nhập & Đăng xuất**: Hệ thống yêu cầu xác thực người dùng để truy cập.
- **Phân quyền**: Hỗ trợ các vai trò `ADMIN`, `MANAGER`, `STAFF` với các quyền truy cập khác nhau.
- **Quản lý tài khoản**: Tạo, xem, sửa, xóa tài khoản người dùng.
- **Khóa/Mở khóa tài khoản**: Quản trị viên có thể khóa hoặc mở khóa tài khoản người dùng.
- **Thay đổi mật khẩu**: Người dùng có thể thay đổi mật khẩu của mình.

### 2. Quản lý Sản phẩm
- **CRUD Sản phẩm**: Thêm, xem, sửa, xóa thông tin sản phẩm (tên, thương hiệu, danh mục, giá, số lượng tồn kho, mô tả).
- **Cập nhật tồn kho**: Quản lý số lượng sản phẩm trong kho.

### 3. Quản lý Khách hàng
- **CRUD Khách hàng**: Thêm, xem, sửa, xóa thông tin khách hàng (họ tên, email, số điện thoại, địa chỉ).
- **Tìm kiếm khách hàng**: Tìm kiếm khách hàng theo từ khóa.

### 4. Quản lý Hóa đơn
- **Tạo hóa đơn**: Lập hóa đơn mới cho khách hàng.
- **Quản lý mặt hàng hóa đơn**: Thêm, xóa sản phẩm khỏi hóa đơn.
- **Xem chi tiết hóa đơn**: Hiển thị thông tin chi tiết của một hóa đơn.
- **Tính tổng tiền**: Tự động tính tổng giá trị hóa đơn.
- **Tìm kiếm hóa đơn**: Tìm kiếm hóa đơn theo email khách hàng hoặc ID khách hàng.

### 5. Quản lý Thương hiệu và Danh mục
- **CRUD Thương hiệu**: Thêm, xem, sửa, xóa thông tin thương hiệu sản phẩm.
- **CRUD Danh mục**: Thêm, xem, sửa, xóa thông tin danh mục sản phẩm.

### 6. Ghi nhật ký Tồn kho
- **Theo dõi thay đổi**: Ghi lại các thay đổi về số lượng tồn kho của sản phẩm với lý do cụ thể.

## Cấu trúc dự án

Dự án được tổ chức theo kiến trúc phân lớp, sử dụng các gói chính sau:

-   `com.quanlycuahangxe`
    -   `Main.java`: Điểm khởi chạy ứng dụng.
    -   `FlywayMigrator.java`: Quản lý migration cơ sở dữ liệu.
-   `com.quanlycuahangxe.config`: Cấu hình ứng dụng (ví dụ: tải biến môi trường).
-   `com.quanlycuahangxe.dao`: Lớp truy cập dữ liệu (Data Access Objects) để tương tác với cơ sở dữ liệu.
-   `com.quanlycuahangxe.db`: Quản lý kết nối cơ sở dữ liệu PostgreSQL.
-   `com.quanlycuahangxe.gui`: Chứa tất cả các giao diện người dùng (Swing Forms và Panels).
    -   `LoginForm`: Giao diện đăng nhập.
    -   `MainForm`: Giao diện chính sau khi đăng nhập.
    -   `ProductManagementPanel`, `CustomerManagementPanel`, `InvoiceManagementPanel`, `UserManagementPanel`: Các panel quản lý chức năng.
    -   Các form và dialog khác cho các thao tác cụ thể.
-   `com.quanlycuahangxe.model`: Định nghĩa các đối tượng dữ liệu (POJOs) như `User`, `Product`, `Customer`, `Invoice`, `Brand`, `Category`, `Staff`, `InventoryLog`, `InvoiceItem`, `Role`.
-   `com.quanlycuahangxe.service`: Chứa các giao diện và triển khai logic nghiệp vụ.
    -   `interfaces`: Định nghĩa các hợp đồng dịch vụ.
    -   `impl`: Triển khai các dịch vụ.
-   `com.quanlycuahangxe.utils`: Các lớp tiện ích chung (ví dụ: `IconHelper` để tải icon, `CurrencyHelper` để định dạng tiền tệ, `Hash` để mã hóa mật khẩu).

## Điểm nổi bật kỹ thuật
Để hiểu rõ hơn về các kỹ thuật và kiến trúc được sử dụng trong dự án, vui lòng tham khảo thư mục [docs/technical_knowledge](docs/technical_knowledge).

## Luồng hoạt động chính

1.  **Khởi động ứng dụng**:
    *   Khi ứng dụng được chạy, `Main.java` sẽ thiết lập kết nối đến cơ sở dữ liệu PostgreSQL.
    *   `FlywayMigrator` sẽ tự động chạy để đảm bảo cấu trúc cơ sở dữ liệu được cập nhật.
2.  **Đăng nhập**:
    *   `LoginForm` sẽ hiển thị, yêu cầu người dùng nhập tên tài khoản và mật khẩu.
    *   `UserService` sẽ xác thực thông tin đăng nhập.
3.  **Giao diện chính**:
    *   Nếu đăng nhập thành công, `MainForm` sẽ được hiển thị. `MainForm` có một menu điều hướng ở bên trái và một khu vực nội dung chính.
    *   Thông tin người dùng hiện tại (tên, vai trò) được hiển thị ở phía trên.
4.  **Điều hướng chức năng**:
    *   Người dùng chọn một chức năng từ menu bên trái (ví dụ: "Quản lý sản phẩm", "Quản lý khách hàng").
    *   `MainForm` sẽ tải panel quản lý tương ứng (ví dụ: `ProductManagementPanel`) vào khu vực nội dung chính.
    *   Đối với "Quản lý nhân sự", hệ thống sẽ kiểm tra quyền của người dùng (`ADMIN` hoặc `MANAGER`) trước khi hiển thị panel.
5.  **Thực hiện nghiệp vụ**:
    *   Trong mỗi panel quản lý, người dùng có thể thực hiện các thao tác như thêm, sửa, xóa, tìm kiếm dữ liệu.
    *   Các thao tác này sẽ gọi đến các phương thức trong các lớp `Service` tương ứng (ví dụ: `ProductService` cho quản lý sản phẩm).
    *   Các lớp `Service` sẽ sử dụng các `DAO` để tương tác với cơ sở dữ liệu.
6.  **Đăng xuất và Thoát**:
    *   Người dùng có thể chọn "Đăng xuất" từ menu thông tin người dùng để quay lại `LoginForm`.
    *   Nút "Thoát chương trình" sẽ đóng ứng dụng.

## Cài đặt và Chạy ứng dụng

Để cài đặt và chạy ứng dụng, bạn cần:

1. **Cấu hình kết nối DB**:
    *   Tạo một file `.env` trong thư mục gốc của dự án (nếu chưa có) và cấu hình các biến môi trường sau:
        ```
        DB_URL=jdbc:postgresql://localhost:5432/product
        DB_USER=postgres
        DB_PASSWORD=1234
        DB_POSTGRES=product
        DB_PORTS=5432
        ```
    *   (Lưu ý: Thay đổi `DB_URL`, `DB_USER`, `DB_PASSWORD` nếu cấu hình PostgreSQL của bạn khác.)
2.  **Khởi động Cơ sở dữ liệu PostgreSQL bằng Docker Compose**:
    *   Mở terminal trong thư mục gốc của dự án.
    *   Chạy lệnh sau để khởi động dịch vụ PostgreSQL:
        ```bash
        docker-compose up -d
        ```
    *   Đảm bảo dịch vụ PostgreSQL đã chạy thành công trước khi tiếp tục.
3.  **Biên dịch và chạy ứng dụng Java**:
    *   Sử dụng IDE (như IntelliJ IDEA, NetBeans) để biên dịch và chạy lớp `Main.java`.

### Chạy ứng dụng bằng script (khuyến nghị)

Để đơn giản hóa quá trình xây dựng và chạy, bạn có thể sử dụng các script được cung cấp:
**Lưu ý:** Đảm bảo rằng cơ sở dữ liệu PostgreSQL đã được khởi động(docker) và file `.env` đã được cấu hình đúng trước khi chạy script.

*   **Trên Linux/macOS:**
    Mở terminal trong thư mục gốc của dự án và chạy:
    ```bash
    chmod +x run.sh # Cấp quyền thực thi cho script
    ./run.sh
    ```
*   **Trên Windows:**
    Mở Command Prompt hoặc PowerShell trong thư mục gốc của dự án và chạy:
    ```cmd
    run.bat
    ```
Các script này sẽ tự động thực hiện các bước sau:
1.  Build dự án bằng Maven, tạo ra một file JAR thực thi (`quan-ly-cua-hang-xe-1.0-SNAPSHOT.jar`).
2.  Kiểm tra sự tồn tại của file `.env`.
3.  Chạy ứng dụng Java từ file JAR đã tạo.

### Tài khoản Admin mặc định
- **Tên đăng nhập**: `admin`
- **Mật khẩu**: `Admin@123`
