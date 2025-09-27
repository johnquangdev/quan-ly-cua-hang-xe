package com.quanlycuahangxe.utils;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class IconHelper {

    public static ImageIcon loadIcon(String iconName, int width, int height) {
        // Đường dẫn tương đối đến thư mục icons từ classpath
        String path = "/icons/" + iconName;
        URL iconURL = IconHelper.class.getResource(path);

        if (iconURL != null) {
            ImageIcon originalIcon = new ImageIcon(iconURL);
            Image scaledImage
                    = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("Không tìm thấy tài nguyên icon: " + path);
            return null; // Trả về null nếu không tìm thấy icon
        }
    }

    public static ImageIcon loadIcon(String iconName) {
        return loadIcon(iconName, 16, 16);
    }
}
