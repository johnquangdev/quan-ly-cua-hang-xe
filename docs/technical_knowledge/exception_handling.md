# Xử lý ngoại lệ mạnh mẽ với `try-catch` và `SQLException`

Trong phát triển ứng dụng, việc xử lý lỗi và ngoại lệ là cực kỳ quan trọng để đảm bảo ứng dụng hoạt động ổn định và đáng tin cậy. Dự án Quản lý Cửa hàng Xe đã áp dụng các kỹ thuật xử lý ngoại lệ hiệu quả, đặc biệt là khi tương tác với cơ sở dữ liệu.

## Tầm quan trọng của `try-catch`

Cấu trúc `try-catch` là một phần cơ bản của Java để xử lý các ngoại lệ. Nó cho phép ứng dụng "bắt" và xử lý các lỗi có thể xảy ra trong quá trình thực thi, thay vì để chúng làm sập toàn bộ chương trình.

### Lợi ích:

*   **Độ tin cậy**: Ứng dụng có thể tiếp tục hoạt động ngay cả khi có lỗi xảy ra.
*   **Trải nghiệm người dùng**: Người dùng nhận được thông báo lỗi rõ ràng thay vì ứng dụng bị đóng đột ngột.
*   **Gỡ lỗi dễ dàng**: Các thông báo lỗi được ghi lại giúp nhà phát triển dễ dàng xác định và khắc phục sự cố.

## Xử lý `SQLException`

Khi làm việc với cơ sở dữ liệu thông qua JDBC, `SQLException` là loại ngoại lệ phổ biến nhất. Nó được ném ra khi có lỗi xảy ra trong quá trình tương tác với cơ sở dữ liệu (ví dụ: lỗi kết nối, lỗi cú pháp SQL, vi phạm ràng buộc).

### Cách xử lý `SQLException` trong dự án:

Dự án sử dụng `try-catch` để bao bọc các thao tác liên quan đến cơ sở dữ liệu, đặc biệt là khi lấy và đóng kết nối, hoặc thực hiện các truy vấn.

Ví dụ trong `NewConnectPostgres.java`:

```java
public class NewConnectPostgres {
    // ... (khởi tạo dataSource) ...

    public static Connection getConnection() {
        try {
            return dataSource.getConnection(); // Thử lấy kết nối từ pool
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy connection từ pool: " + e.getMessage());
            e.printStackTrace(); // In stack trace để gỡ lỗi
            return null; // Trả về null hoặc ném một ngoại lệ tùy chỉnh
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // Thử trả kết nối về pool
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
```

### Điểm nổi bật:

1.  **Tránh tạo nhiều connection tới DB khiến đứng chương trình**: Bằng cách sử dụng `try-catch` xung quanh `dataSource.getConnection()`, ứng dụng có thể xử lý các trường hợp không thể lấy kết nối từ pool (ví dụ: pool đã cạn, cơ sở dữ liệu không khả dụng) mà không làm sập ứng dụng. Điều này giúp duy trì sự ổn định và tránh tình trạng "đứng chương trình" do cố gắng tạo quá nhiều kết nối mới khi tài nguyên hạn chế.
2.  **Sử dụng `SQLException` để mở 1 connection cho 1 vấn đề cần nhiều method xử lí**: Mặc dù tiêu đề này có thể gây hiểu lầm, ý chính ở đây là khi một `SQLException` xảy ra trong một chuỗi các thao tác liên quan đến cơ sở dữ liệu (có thể trải dài qua nhiều phương thức), việc bắt và xử lý ngoại lệ này một cách tập trung (ví dụ, ở lớp Service hoặc DAO) cho phép ứng dụng quản lý trạng thái giao dịch (rollback) và thông báo lỗi một cách hiệu quả. Điều này đảm bảo rằng các thao tác phức tạp vẫn giữ được tính toàn vẹn dữ liệu.

Việc áp dụng các kỹ thuật xử lý ngoại lệ này giúp ứng dụng Quản lý Cửa hàng Xe trở nên mạnh mẽ hơn, có khả năng phục hồi tốt hơn trước các sự cố và cung cấp trải nghiệm người dùng mượt mà hơn.
