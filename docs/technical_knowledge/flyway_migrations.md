# Quản lý Migration cơ sở dữ liệu với Flyway

Trong quá trình phát triển phần mềm, cấu trúc cơ sở dữ liệu thường xuyên thay đổi. Việc quản lý các thay đổi này một cách có hệ thống là rất quan trọng để đảm bảo tính nhất quán giữa các môi trường phát triển, kiểm thử và sản phẩm. Dự án Quản lý Cửa hàng Xe sử dụng **Flyway** để quản lý các migration cơ sở dữ liệu.

## Flyway là gì?

Flyway là một công cụ migration cơ sở dữ liệu mã nguồn mở, đơn giản và mạnh mẽ. Nó cho phép bạn quản lý các thay đổi của cơ sở dữ liệu bằng cách sử dụng các file SQL (hoặc Java) có phiên bản. Khi ứng dụng khởi động, Flyway sẽ kiểm tra trạng thái cơ sở dữ liệu hiện tại và áp dụng các migration còn thiếu theo thứ tự.

## Lợi ích của việc sử dụng Flyway

1.  **Kiểm soát phiên bản**: Mỗi thay đổi cấu trúc cơ sở dữ liệu được lưu trữ trong một file riêng biệt với một số phiên bản, giúp dễ dàng theo dõi lịch sử thay đổi.
2.  **Tính nhất quán**: Đảm bảo rằng tất cả các môi trường (dev, test, prod) đều có cùng cấu trúc cơ sở dữ liệu.
3.  **Tự động hóa**: Các migration được áp dụng tự động khi ứng dụng khởi động, loại bỏ các bước thủ công dễ gây lỗi.
4.  **Dễ dàng rollback/forward**: Mặc dù Flyway không có tính năng rollback tự động, việc có các file migration rõ ràng giúp dễ dàng quản lý các thay đổi.
5.  **Hỗ trợ nhiều cơ sở dữ liệu**: Flyway hỗ trợ nhiều hệ quản trị cơ sở dữ liệu khác nhau.

## Triển khai trong dự án

Trong dự án này, các file migration SQL được đặt trong thư mục `src/main/resources/migration`. Mỗi file được đặt tên theo quy ước `V[số_phiên_bản]__[mô_tả].sql` (ví dụ: `V1__create_roles_users_staff_customers.sql`).

Lớp `com.quanlycuahangxe.FlywayMigrator` chịu trách nhiệm khởi tạo và chạy Flyway khi ứng dụng bắt đầu:

```java
// Ví dụ về cách Flyway được khởi tạo (có thể nằm trong Main.java hoặc một lớp cấu hình)
public class FlywayMigrator {
    public static void migrate() {
        // Cấu hình Flyway
        Flyway flyway = Flyway.configure()
            .dataSource(LoadEnv.getDbUrl(), LoadEnv.getDbUser(), LoadEnv.getDbPassword())
            .locations("classpath:migration") // Thư mục chứa các file migration
            .load();

        // Chạy migration
        flyway.migrate();
        System.out.println("Database migration completed successfully.");
    }
}
```

Khi ứng dụng khởi động, phương thức `migrate()` sẽ được gọi, và Flyway sẽ tự động kiểm tra và áp dụng các migration cần thiết.

## Các file migration chính

Dưới đây là danh sách các file migration SQL trong dự án, thể hiện lịch sử phát triển cấu trúc cơ sở dữ liệu:

*   `V1__create_roles_users_staff_customers.sql`: Tạo các bảng ban đầu cho vai trò, người dùng, nhân viên và khách hàng.
*   `V2__create_brands_categories_products.sql`: Tạo các bảng cho thương hiệu, danh mục và sản phẩm.
*   `V3__create_discounts_invoices_items.sql`: Tạo các bảng cho giảm giá, hóa đơn và chi tiết hóa đơn.
*   `V4__create_inventory_log.sql`: Tạo bảng ghi nhật ký tồn kho.
*   `V5__insert_default_roles.sql`: Chèn dữ liệu vai trò mặc định.
*   `V6__remove_roles_table.sql`: Xóa bảng `roles` (thay đổi kiến trúc phân quyền).
*   `V7__update_users_add_role_column.sql`: Cập nhật bảng `users` để thêm cột `role` trực tiếp.
*   `V8__drop_table_Staff.sql`: Xóa bảng `staffs`.
*   `V9__update_invoices_relations.sql`: Cập nhật các quan hệ trong bảng `invoices` (thay thế `staff_id` bằng `user_id`).

Việc sử dụng Flyway giúp quản lý cơ sở dữ liệu một cách có tổ chức và đáng tin cậy, là một phần không thể thiếu trong quy trình phát triển hiện đại.
