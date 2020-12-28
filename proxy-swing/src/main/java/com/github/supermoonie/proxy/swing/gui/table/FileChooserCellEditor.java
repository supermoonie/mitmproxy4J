package com.github.supermoonie.proxy.swing.gui.table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.io.File;

/**
 * @author supermoonie
 * @since 2020/12/28
 */
public class FileChooserCellEditor extends DefaultCellEditor implements TableCellEditor {

    /**
     * Number of clicks to start editing
     */
    private static final int CLICK_COUNT_TO_START = 2;
    /**
     * Editor component
     */
    private final JButton button;
    /**
     * File chooser
     */
    private final JFileChooser fileChooser;
    /**
     * Selected file
     */
    private String file = "";

    private final JTable target;

    private final int columnIndex;

    /**
     * Constructor.
     */
    public FileChooserCellEditor(JTable target, int columnIndex, int mode) {
        super(new JTextField());
        this.target = target;
        this.columnIndex = columnIndex;
        setClickCountToStart(CLICK_COUNT_TO_START);
        // Using a JButton as the editor component
        button = new JButton();
        button.setBackground(Color.white);
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
        button.setBorder(null);
        // Dialog which will do the actual editing
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(mode);
        fileChooser.setMultiSelectionEnabled(false);
    }

    @Override
    public Object getCellEditorValue() {
        return file;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        file = value.toString();
        SwingUtilities.invokeLater(() -> {
            fileChooser.setSelectedFile(new File(file));
            if (fileChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile().getAbsolutePath();
                int selectedRow = target.getSelectedRow();
                if (-1 == selectedRow) {
                    return;
                }
                target.getModel().setValueAt("", selectedRow, columnIndex);
            }
            fireEditingStopped();
        });
        button.setText(file);
        button.setToolTipText(file);
        return button;
    }
}