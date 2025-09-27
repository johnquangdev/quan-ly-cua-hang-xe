# Xử lý tiền tệ chính xác với `CurrencyHelper`

Trong các ứng dụng kinh doanh, việc định dạng và hiển thị tiền tệ một cách chính xác và phù hợp với từng khu vực là rất quan trọng. Dự án Quản lý Cửa hàng Xe đã triển khai lớp tiện ích `CurrencyHelper` để xử lý việc định dạng số `double` thành chuỗi tiền tệ Việt Nam Đồng (VNĐ).

## Mục đích của `CurrencyHelper`

Lớp `CurrencyHelper` được thiết kế để:
*   Đảm bảo rằng các giá trị tiền tệ được hiển thị theo định dạng chuẩn của Việt Nam.
*   Tránh các lỗi định dạng phổ biến khi làm việc với số thập phân và tiền tệ.
*   Cung cấp một phương thức tập trung để định dạng tiền tệ, giúp mã nguồn nhất quán và dễ bảo trì.

## Triển khai trong dự án

Lớp `CurrencyHelper` sử dụng `java.text.NumberFormat` với `Locale` của Việt Nam để định dạng tiền tệ.

```java
package com.quanlycuahangxe.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyHelper {

    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);

    // format số thành vnđ
    public static String formatVND(double amount) {
        String formatted = CURRENCY_FORMATTER.format(amount);
        // Thay thế ký hiệu tiền tệ mặc định của Locale thành "VNĐ"
        return formatted.replace("₫", "VNĐ").trim();
    }
}
```

## Cách sử dụng

Để định dạng một số `double` thành chuỗi VNĐ, chỉ cần gọi phương thức `formatVND`:

```java
double price = 15000000.50;
String formattedPrice = CurrencyHelper.formatVND(price);
System.out.println(formattedPrice); // Output: 15.000.000,50 VNĐ
```

## Lợi ích

*   **Chính xác và nhất quán**: Đảm bảo tất cả các giá trị tiền tệ trong ứng dụng đều được định dạng theo cùng một chuẩn.
*   **Dễ sử dụng**: Cung cấp một API đơn giản để định dạng tiền tệ.
*   **Khả năng quốc tế hóa (I18n)**: Mặc dù hiện tại chỉ tập trung vào VNĐ, cấu trúc này dễ dàng mở rộng để hỗ trợ các loại tiền tệ và `Locale` khác nếu cần.
