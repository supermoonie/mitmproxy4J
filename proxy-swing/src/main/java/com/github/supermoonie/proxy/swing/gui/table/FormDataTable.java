package com.github.supermoonie.proxy.swing.gui.table;

import com.github.supermoonie.proxy.swing.mime.MimeMappings;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/12/9
 */
public class FormDataTable extends CurdTable {

    private final Class<?>[] columnTypes = new Class<?>[]{String.class, FormDataValue.class, String.class, String.class, Object.class};
    private final ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());

    public FormDataTable() {
        super(new DefaultTableModel(null, new String[]{"Name", "Value", "File", "ContentType", ""}));
        super.setShowHorizontalLines(true);
        super.setShowVerticalLines(true);
        super.setRowSelectionAllowed(true);
        super.setColumnSelectionAllowed(true);
        JTextField nameTextField = new JTextField();
        DefaultCellEditor nameCellEditor = new DefaultCellEditor(nameTextField);
        nameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                nameCellEditor.stopCellEditing();
            }
        });
        nameCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(0).setCellEditor(nameCellEditor);
        JTextField valueTextField = new JTextField() {
            {
                addActionListener(e -> {
                    int selectedRow = FormDataTable.this.getSelectedRow();
                    if (-1 == selectedRow) {
                        return;
                    }
                    FormDataTable.this.getModel().setValueAt("Select...", selectedRow, 2);
                });
            }
        };
        DefaultCellEditor valueCellEditor = new DefaultCellEditor(valueTextField);
        valueTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                valueCellEditor.stopCellEditing();
            }
        });
        valueCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(1).setCellEditor(valueCellEditor);
        super.getColumnModel().getColumn(2).setCellEditor(new FileChooserCellEditor(this, 2, JFileChooser.FILES_ONLY));
        List<String> contentTypeList = MimeMappings.DEFAULT.getAll().stream().map(MimeMappings.Mapping::getMimeType).sorted().collect(Collectors.toList());
        contentTypeList.add(0, "Auto");
        String[] contentTypes = contentTypeList.toArray(new String[]{});
        JComboBox<String> contentTypeComboBox = new JComboBox<>(new DefaultComboBoxModel<>(contentTypes));
        contentTypeComboBox.setEditable(true);
        AutoCompleteDecorator.decorate(contentTypeComboBox);
        super.getColumnModel().getColumn(3).setCellEditor(new ComboBoxCellEditor(contentTypeComboBox));
        super.getColumnModel().getColumn(4).setCellEditor(buttonEditor);
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
    public void addRow() {
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


    public ButtonEditor getButtonEditor() {
        return buttonEditor;
    }
}