package com.quanlycuahangxe.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.quanlycuahangxe.utils.CurrencyHelper;

public class CurrencyRenderer extends DefaultTableCellRenderer {

    public CurrencyRenderer() {
        super();
        setHorizontalAlignment(JLabel.LEFT);
    }

    private static final Color EVEN_ROW_COLOR = Color.WHITE;
    private static final Color ODD_ROW_COLOR = new Color(240, 240, 240); // Màu xám nhạt

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                column);

        if (value instanceof Number) {
            value = CurrencyHelper.formatVND(((Number) value).doubleValue());
        }

        if (!isSelected) {
            if (row % 2 == 0) {
                c.setBackground(EVEN_ROW_COLOR);
            } else {
                c.setBackground(ODD_ROW_COLOR);
            }
        }
        setText(value != null ? value.toString() : "");
        return c;
    }
}
