# Quản lý Kết nối Cơ sở dữ liệu hiệu quả với HikariCP

Trong các ứng dụng Java, việc quản lý kết nối đến cơ sở dữ liệu là một yếu tố quan trọng ảnh hưởng đến hiệu suất và độ ổn định. Việc tạo và đóng các kết nối liên tục là một quá trình tốn kém tài nguyên. Để giải quyết vấn đề này, dự án đã triển khai **HikariCP**, một thư viện connection pooling hiệu suất cao.

## HikariCP là gì?

HikariCP là một trong những thư viện connection pooling nhanh nhất và nhẹ nhất cho Java. Nó cung cấp một pool các kết nối cơ sở dữ liệu đã được tạo sẵn và sẵn sàng để sử dụng. Khi ứng dụng cần một kết nối, nó sẽ lấy một kết nối từ pool thay vì tạo mới. Sau khi sử dụng xong, kết nối sẽ được trả về pool để tái sử dụng.

## Lợi ích của việc sử dụng HikariCP

1.  **Tăng cường hiệu suất**: Giảm đáng kể thời gian và tài nguyên cần thiết để thiết lập kết nối, giúp các thao tác truy vấn dữ liệu nhanh hơn.
2.  **Cải thiện độ ổn định**: Ngăn chặn tình trạng quá tải cơ sở dữ liệu do tạo quá nhiều kết nối đồng thời.
3.  **Quản lý tài nguyên hiệu quả**: Tái sử dụng các kết nối, giảm thiểu việc cấp phát và giải phóng tài nguyên hệ thống.
4.  **Cấu hình linh hoạt**: Cho phép tùy chỉnh các thông số của pool như kích thước tối đa, thời gian chờ, v.v., để phù hợp với yêu cầu của ứng dụng.

## Triển khai trong dự án

Trong dự án này, HikariCP được cấu hình và khởi tạo trong lớp `com.quanlycuahangxe.db.NewConnectPostgres`.

```java
public class NewConnectPostgres {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(LoadEnv.getDbUrl());
            config.setUsername(LoadEnv.getDbUser());
            config.setPassword(LoadEnv.getDbPassword());
            config.setDriverClassName("org.postgresql.Driver");

            // Cấu hình thêm để pool ổn định
            config.setMaximumPoolSize(10); // Số lượng kết nối tối đa trong pool
            config.setMinimumIdle(2);     // Số lượng kết nối tối thiểu luôn duy trì trong pool
            config.setIdleTimeout(60000); // Thời gian một kết nối không hoạt động có thể tồn tại trong pool (ms)
            config.setConnectionTimeout(30000); // Thời gian chờ tối đa để lấy một kết nối từ pool (ms)
            config.setMaxLifetime(600000); // Thời gian tối đa một kết nối có thể tồn tại trong pool (ms)

            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP DataSource initialized successfully.");
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo DataSource: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy connection từ pool: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // Trả connection về pool
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
```

Việc sử dụng `try-catch` khi lấy và đóng kết nối đảm bảo rằng các lỗi liên quan đến cơ sở dữ liệu được xử lý một cách an toàn, tránh làm sập chương trình và giữ cho pool kết nối hoạt động ổn định.
