package com.github.supermoonie.proxy.swing.gui.table;

import com.github.supermoonie.proxy.swing.mime.MimeMappings;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/12/9
 */
public class FormDataTable extends CurdTable {

    private final Class<?>[] columnTypes = new Class<?>[]{String.class, FormDataValue.class, String.class, String.class, Object.class};

    public FormDataTable() {
        super(new DefaultTableModel(null, new String[]{"Name", "Value", "File", "ContentType", ""}));
        super.setShowHorizontalLines(true);
        super.setShowVerticalLines(true);
        super.setRowSelectionAllowed(true);
        super.setColumnSelectionAllowed(true);
        DefaultCellEditor nameCellEditor = new DefaultCellEditor(new JTextField());
        nameCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(0).setCellEditor(nameCellEditor);
        DefaultCellEditor valueCellEditor = new DefaultCellEditor(new JTextField() {
            {
                addActionListener(e -> {
                    int selectedRow = FormDataTable.this.getSelectedRow();
                    if (-1 == selectedRow) {
                        return;
                    }
                    FormDataTable.this.getModel().setValueAt("Select...", selectedRow, 2);
                });
            }
        });
        valueCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(1).setCellEditor(valueCellEditor);
        super.getColumnModel().getColumn(2).setCellEditor(new FileChooserCellEditor());
        List<String> contentTypeList = MimeMappings.DEFAULT.getAll().stream().map(MimeMappings.Mapping::getMimeType).sorted().collect(Collectors.toList());
        contentTypeList.add(0, "Auto");
        String[] contentTypes = contentTypeList.toArray(new String[]{});
        JComboBox<String> contentTypeComboBox = new JComboBox<>(new DefaultComboBoxModel<>(contentTypes));
        contentTypeComboBox.setEditable(true);
        AutoCompleteDecorator.decorate(contentTypeComboBox);
        super.getColumnModel().getColumn(3).setCellEditor(new ComboBoxCellEditor(contentTypeComboBox));
        super.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        super.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Del"));
        super.getColumnModel().getColumn(0).setPreferredWidth(600);
        super.getColumnModel().getColumn(1).setPreferredWidth(600);
        super.getColumnModel().getColumn(2).setPreferredWidth(600);
        super.getColumnModel().getColumn(3).setPreferredWidth(600);
        super.getColumnModel().getColumn(4).setPreferredWidth(200);
        getModel().addTableModelListener(e -> {
            if ((e.getLastRow() + 1) == getModel().getRowCount()) {
                addRow();
            }
        });
        ((DefaultTableModel) getModel()).addRow(new Object[]{"", "", "Select...", "Auto", "Del"});
    }

    @Override
    protected void addRow() {
        int rowCount = getModel().getRowCount();
        int columnCount = getModel().getColumnCount();
        boolean addFlag = false;
        if (rowCount > 0) {
            for (int i = 0; i < 2; i++) {
                Object value = getModel().getValueAt(rowCount - 1, i);
                if (null == value || !value.toString().equals("")) {
                    addFlag = true;
                    break;
                }
            }
        } else {
            addFlag = true;
        }
        if (addFlag) {
            Object[] rowData = new Object[columnCount];
            for (int i = 0; i < columnCount - 1; i++) {
                rowData[i] = "";
            }
            rowData[columnCount - 1] = "Del";
            rowData[2] = "Select...";
            rowData[3] = "Auto";
            ((DefaultTableModel) getModel()).addRow(rowData);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    private class FileChooserCellEditor extends DefaultCellEditor implements TableCellEditor {

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

        /**
         * Constructor.
         */
        public FileChooserCellEditor() {
            super(new JTextField());
            setClickCountToStart(CLICK_COUNT_TO_START);
            // Using a JButton as the editor component
            button = new JButton();
            button.setBackground(Color.white);
            button.setFont(button.getFont().deriveFont(Font.PLAIN));
            button.setBorder(null);
            // Dialog which will do the actual editing
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
                    int selectedRow = FormDataTable.this.getSelectedRow();
                    if (-1 == selectedRow) {
                        return;
                    }
                    FormDataTable.this.getModel().setValueAt("", selectedRow, 1);
                }
                fireEditingStopped();
            });
            button.setText(file);
            button.setToolTipText(file);
            return button;
        }
    }
}