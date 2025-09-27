package com.quanlycuahangxe.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {

    private static final Color EVEN_ROW_COLOR = Color.WHITE;
    private static final Color ODD_ROW_COLOR = new Color(240, 240, 240); // Màu xám nhạt

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                column);

        if (!isSelected) {
            if (row % 2 == 0) {
                c.setBackground(EVEN_ROW_COLOR);
            } else {
                c.setBackground(ODD_ROW_COLOR);
            }
        }
        // If selected, use default selection color

        setText(value != null ? value.toString() : "");
        return c;
    }
}
