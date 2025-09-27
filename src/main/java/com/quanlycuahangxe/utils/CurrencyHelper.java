package com.quanlycuahangxe.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyHelper {

    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);

    //format số thành vnđ
    public static String formatVND(double amount) {
        String formatted = CURRENCY_FORMATTER.format(amount);
        return formatted.replace("₫", "VNĐ").trim();
    }
}
