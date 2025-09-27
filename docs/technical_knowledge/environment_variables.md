# Quản lý biến môi trường an toàn với `LoadEnv`

Trong các ứng dụng hiện đại, việc quản lý các thông tin cấu hình nhạy cảm như thông tin kết nối cơ sở dữ liệu, khóa API, hoặc các cài đặt khác cần được thực hiện một cách an toàn và linh hoạt. Dự án Quản lý Cửa hàng Xe sử dụng lớp tiện ích `LoadEnv` để tải các biến môi trường từ file `.env`.

## Tại sao cần quản lý biến môi trường?

1.  **Bảo mật**: Tránh việc mã hóa cứng các thông tin nhạy cảm trực tiếp vào mã nguồn, giúp ngăn chặn rò rỉ thông tin khi mã nguồn được chia sẻ hoặc đưa lên các hệ thống kiểm soát phiên bản công khai.
2.  **Linh hoạt cấu hình**: Cho phép dễ dàng thay đổi cấu hình ứng dụng giữa các môi trường khác nhau (phát triển, kiểm thử, sản phẩm) mà không cần sửa đổi mã nguồn.
3.  **Thực hành tốt**: Tuân thủ nguyên tắc "Twelve-Factor App" về cấu hình, khuyến khích lưu trữ cấu hình trong môi trường.

## Triển khai trong dự án

Lớp `com.quanlycuahangxe.config.LoadEnv` chịu trách nhiệm đọc các biến môi trường từ file `.env` (nếu tồn tại) hoặc từ môi trường hệ thống.

```java
package com.quanlycuahangxe.config;

import io.github.cdimascio.dotenv.Dotenv;

public class LoadEnv {

    private static final Dotenv dotenv = Dotenv.load();

    public static String getDbUrl() {
        return dotenv.get("DB_URL");
    }

    public static String getDbUser() {
        return dotenv.get("DB_USER");
    }

    public static String getDbPassword() {
        return dotenv.get("DB_PASSWORD");
    }

    // Có thể thêm các phương thức khác để lấy các biến môi trường khác
}
```

### File `.env` ví dụ:

```
# .env
DB_URL=jdbc:postgresql://localhost:5432/product
DB_USER=postgres
DB_PASSWORD=1234
```

## Cách sử dụng

Các lớp trong ứng dụng có thể truy cập các biến môi trường thông qua các phương thức tĩnh của `LoadEnv`. Ví dụ, lớp `NewConnectPostgres` sử dụng `LoadEnv` để lấy thông tin kết nối cơ sở dữ liệu:

```java
public class NewConnectPostgres {
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(LoadEnv.getDbUrl()); // Lấy DB_URL từ biến môi trường
            config.setUsername(LoadEnv.getDbUser()); // Lấy DB_USER từ biến môi trường
            config.setPassword(LoadEnv.getDbPassword()); // Lấy DB_PASSWORD từ biến môi trường
            // ...
        } catch (Exception e) {
            // ...
        }
    }
    // ...
}
```

## Lợi ích

*   **Bảo mật thông tin**: Giữ các thông tin nhạy cảm tách biệt khỏi mã nguồn.
*   **Dễ dàng cấu hình**: Thay đổi cấu hình chỉ bằng cách chỉnh sửa file `.env` mà không cần biên dịch lại mã.
*   **Tái sử dụng**: Cùng một mã nguồn có thể được triển khai trên nhiều môi trường với các cấu hình khác nhau.

Việc sử dụng `LoadEnv` và file `.env` là một phương pháp hiệu quả để quản lý cấu hình ứng dụng, đặc biệt là trong các dự án có nhiều môi trường hoặc yêu cầu bảo mật cao.
