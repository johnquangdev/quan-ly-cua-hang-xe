# Tổng quan về Kiến thức Kỹ thuật trong Dự án

Dự án Quản lý Cửa hàng Xe được xây dựng với mục tiêu cung cấp một giải pháp quản lý toàn diện, hiệu quả và dễ bảo trì. Để đạt được điều này, chúng tôi đã áp dụng nhiều kỹ thuật và kiến trúc phần mềm hiện đại. Tài liệu này sẽ tổng hợp các điểm nổi bật về mặt kỹ thuật, giúp các nhà phát triển và người bảo trì hệ thống hiểu rõ hơn về cách ứng dụng được xây dựng và hoạt động.

## Các điểm nổi bật chính

Dưới đây là một số khía cạnh kỹ thuật quan trọng đã được triển khai trong dự án:

*   **Quản lý kết nối cơ sở dữ liệu hiệu quả với HikariCP**: Đảm bảo hiệu suất và độ ổn định khi tương tác với cơ sở dữ liệu.
*   **Kiến trúc phân lớp (Service-DAO Pattern)**: Tách biệt rõ ràng các mối quan tâm, giúp mã nguồn dễ quản lý và mở rộng.
*   **Quản lý Migration cơ sở dữ liệu với Flyway**: Đảm bảo cấu trúc cơ sở dữ liệu luôn nhất quán và có phiên bản.
*   **Đóng gói và triển khai dễ dàng với Docker và Docker Compose**: Đơn giản hóa quá trình cài đặt và chạy ứng dụng.
*   **Quản lý biến môi trường an toàn với `LoadEnv`**: Bảo mật thông tin nhạy cảm và linh hoạt trong cấu hình.
*   **Xử lý tiền tệ chính xác với `CurrencyHelper`**: Đảm bảo định dạng tiền tệ Việt Nam Đồng chuẩn xác.
*   **Xử lý ngoại lệ mạnh mẽ với `try-catch` và `SQLException`**: Tăng cường độ tin cậy và khả năng phục hồi của ứng dụng.

Các phần tiếp theo sẽ đi sâu vào từng điểm nổi bật này, cung cấp cái nhìn chi tiết về cách chúng được triển khai và lợi ích mang lại.
