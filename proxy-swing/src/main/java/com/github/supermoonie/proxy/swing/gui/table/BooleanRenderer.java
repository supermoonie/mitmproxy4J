package com.github.supermoonie.proxy.swing.gui.table;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2021/1/20
 */
public class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public BooleanRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPainted(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getForeground());
            Color color = UIManager.getColor(table.hasFocus() ? "Table.focusCellBackgroundColor" : "Table.noFocusCellBackgroundColor");
            setBackground(new Color(color.getRGB()));
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelected(((value != null) && (Boolean) value));
        setBorder(noFocusBorder);
        return this;
    }
}
