# Kiến trúc phân lớp (Service-DAO Pattern)

Dự án Quản lý Cửa hàng Xe được thiết kế theo kiến trúc phân lớp, cụ thể là sử dụng mô hình Service-DAO (Data Access Object). Kiến trúc này giúp tách biệt các mối quan tâm (separation of concerns) trong ứng dụng, làm cho mã nguồn dễ hiểu, dễ bảo trì, kiểm thử và mở rộng hơn.

## Các lớp chính trong kiến trúc

1.  **Lớp GUI (Presentation Layer)**:
    *   **Mục đích**: Chịu trách nhiệm về giao diện người dùng và tương tác với người dùng.
    *   **Thành phần**: Các lớp trong gói `com.quanlycuahangxe.gui` (ví dụ: `MainForm`, `ProductManagementPanel`, `LoginForm`).
    *   **Vai trò**: Hiển thị dữ liệu, thu thập đầu vào từ người dùng và gửi các yêu cầu xử lý đến lớp Service.

2.  **Lớp Service (Business Logic Layer)**:
    *   **Mục đích**: Chứa toàn bộ logic nghiệp vụ của ứng dụng. Nó điều phối các thao tác, thực hiện các quy tắc nghiệp vụ và xử lý các giao dịch.
    *   **Thành phần**: Các giao diện trong `com.quanlycuahangxe.service.interfaces` (ví dụ: `ProductService`, `UserService`) và các lớp triển khai trong `com.quanlycuahangxe.service.impl` (ví dụ: `ProductServiceImpl`, `UserServiceImpl`).
    *   **Vai trò**: Nhận yêu cầu từ lớp GUI, thực hiện các kiểm tra hợp lệ, gọi các phương thức từ lớp DAO để truy cập dữ liệu và trả về kết quả cho lớp GUI. Lớp Service không trực tiếp tương tác với cơ sở dữ liệu mà thông qua lớp DAO.

3.  **Lớp DAO (Data Access Layer)**:
    *   **Mục đích**: Cung cấp một giao diện trừu tượng để tương tác với cơ sở dữ liệu. Nó ẩn đi các chi tiết triển khai của việc truy cập dữ liệu (ví dụ: JDBC, Hibernate) khỏi các lớp nghiệp vụ.
    *   **Thành phần**: Các lớp trong gói `com.quanlycuahangxe.dao` (ví dụ: `ProductDAO`, `CustomerDAO`).
    *   **Vai trò**: Thực hiện các thao tác CRUD (Create, Read, Update, Delete) trên cơ sở dữ liệu. Mỗi DAO thường tương ứng với một thực thể (entity) hoặc một tập hợp các thực thể liên quan.

4.  **Lớp Model (Domain Layer)**:
    *   **Mục đích**: Định nghĩa cấu trúc dữ liệu và các đối tượng nghiệp vụ (POJOs - Plain Old Java Objects).
    *   **Thành phần**: Các lớp trong gói `com.quanlycuahangxe.model` (ví dụ: `User`, `Product`, `Customer`, `Invoice`).
    *   **Vai trò**: Đại diện cho dữ liệu và trạng thái của ứng dụng.

## Lợi ích của kiến trúc phân lớp

*   **Tách biệt mối quan tâm**: Mỗi lớp có một trách nhiệm rõ ràng, giúp dễ dàng hiểu và quản lý mã nguồn.
*   **Dễ bảo trì**: Thay đổi trong một lớp ít ảnh hưởng đến các lớp khác. Ví dụ, thay đổi cơ sở dữ liệu chỉ cần sửa đổi lớp DAO mà không ảnh hưởng đến lớp Service hay GUI.
*   **Dễ kiểm thử**: Mỗi lớp có thể được kiểm thử độc lập, giúp phát hiện lỗi sớm và dễ dàng hơn.
*   **Khả năng mở rộng**: Dễ dàng thêm các tính năng mới hoặc thay đổi công nghệ mà không cần viết lại toàn bộ ứng dụng.
*   **Tái sử dụng mã**: Các lớp Service và DAO có thể được tái sử dụng trong các phần khác của ứng dụng hoặc trong các ứng dụng khác.

Kiến trúc Service-DAO là một mẫu thiết kế mạnh mẽ, giúp xây dựng các ứng dụng Java có cấu trúc tốt, hiệu quả và bền vững.
