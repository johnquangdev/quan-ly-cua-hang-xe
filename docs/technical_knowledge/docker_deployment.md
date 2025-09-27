# Đóng gói và Triển khai dễ dàng với Docker và Docker Compose

Để đơn giản hóa quá trình cài đặt và triển khai ứng dụng Quản lý Cửa hàng Xe, dự án đã tận dụng sức mạnh của Docker và Docker Compose. Điều này cho phép đóng gói ứng dụng cùng với môi trường cơ sở dữ liệu của nó vào các container độc lập, đảm bảo tính nhất quán và khả năng di động trên các môi trường khác nhau.

## Docker là gì?

Docker là một nền tảng cho phép các nhà phát triển đóng gói ứng dụng và tất cả các phụ thuộc của nó vào một "container" tiêu chuẩn hóa. Container là một đơn vị phần mềm độc lập, nhẹ và có thể thực thi được, chứa mọi thứ cần thiết để chạy một ứng dụng: mã nguồn, runtime, thư viện hệ thống, công cụ hệ thống và cài đặt.

## Docker Compose là gì?

Docker Compose là một công cụ để định nghĩa và chạy các ứng dụng Docker đa container. Với Compose, bạn sử dụng một file YAML để cấu hình các dịch vụ của ứng dụng. Sau đó, với một lệnh duy nhất, bạn có thể tạo và khởi động tất cả các dịch vụ từ cấu hình đó.

## Lợi ích của việc sử dụng Docker và Docker Compose

1.  **Tính nhất quán môi trường**: Đảm bảo rằng ứng dụng và cơ sở dữ liệu hoạt động giống nhau trên mọi môi trường (phát triển, kiểm thử, sản phẩm), loại bỏ vấn đề "nó hoạt động trên máy của tôi".
2.  **Triển khai đơn giản**: Chỉ cần một vài lệnh để khởi động toàn bộ ứng dụng, bao gồm cả cơ sở dữ liệu.
3.  **Khả năng di động**: Dễ dàng di chuyển ứng dụng giữa các máy chủ hoặc môi trường khác nhau.
4.  **Cô lập**: Mỗi dịch vụ chạy trong container riêng của nó, cô lập các phụ thuộc và tránh xung đột.
5.  **Quản lý tài nguyên**: Docker cung cấp các cơ chế để quản lý tài nguyên (CPU, RAM) cho từng container.

## Triển khai trong dự án

Dự án sử dụng file `docker-compose.yaml` để định nghĩa dịch vụ cơ sở dữ liệu PostgreSQL.

```yaml
# docker-compose.yaml
version: '3.8'

services:
  postgres:
    image: postgres:13
    container_name: quanlycuahangxe_postgres
    environment:
      POSTGRES_DB: product
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 1234
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Cách khởi động cơ sở dữ liệu bằng Docker Compose:

1.  Đảm bảo Docker Desktop đang chạy.
2.  Mở terminal trong thư mục gốc của dự án.
3.  Chạy lệnh:
    ```bash
    docker-compose up -d postgres
    ```
    Lệnh này sẽ tải xuống image PostgreSQL (nếu chưa có), tạo và khởi động container `quanlycuahangxe_postgres` ở chế độ nền.

### Chạy ứng dụng Java:

Ứng dụng Java sẽ kết nối đến cơ sở dữ liệu PostgreSQL đang chạy trong Docker container thông qua các biến môi trường được cấu hình trong file `.env`.

```
DB_URL=jdbc:postgresql://localhost:5432/product
DB_USER=postgres
DB_PASSWORD=1234
```

Việc tích hợp Docker và Docker Compose giúp đơn giản hóa đáng kể quy trình phát triển và triển khai, cho phép các nhà phát triển tập trung vào việc xây dựng tính năng thay vì lo lắng về cấu hình môi trường.
