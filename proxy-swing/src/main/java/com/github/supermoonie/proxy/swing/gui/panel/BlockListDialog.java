package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AllowBlock;
import com.j256.ormlite.dao.Dao;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2021/1/20
 */
public class BlockListDialog extends JDialog {

    private final JCheckBox enableCheckBox = new JCheckBox("Enable Block List");
    private final DefaultTableModel blockTableModel = new DefaultTableModel(null, new String[]{"Enable", "Location Regex"});
    private final JTable blockTable = new JTable(blockTableModel) {
        private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class};

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }
    };
    private final JButton addButton = new JButton("Add");
    private final JButton removeButton = new JButton("Remove");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton okButton = new JButton("OK");

    public BlockListDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        // container
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        // enable panel
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enablePanel.add(enableCheckBox);
        container.add(enablePanel);
        // block list table panel
        initTable();
        JPanel blockPanel = new JPanel(new BorderLayout() {{
            setVgap(10);
        }});
        blockPanel.add(new JScrollPane(blockTable), BorderLayout.CENTER);
        JPanel operatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(10);
        }});
        operatePanel.add(addButton);
        operatePanel.add(removeButton);
        blockPanel.add(operatePanel, BorderLayout.SOUTH);
        container.add(blockPanel);
        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.borderColor")));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(2));
        buttonPanel.add(okButton);
        container.add(Box.createVerticalStrut(10));
        container.add(buttonPanel);

        boolean enable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_BLOCK_LIST_ENABLE, ApplicationPreferences.VALUE_BLOCK_LIST_ENABLE);
        enableCheckBox.setSelected(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        blockTable.setEnabled(enable);
        Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
        try {
            List<AllowBlock> allowBlockList = allowBlockDao.queryForAll();
            for (AllowBlock allowBlock : allowBlockList) {
                if (allowBlock.getType().equals(AllowBlock.TYPE_BLOCK)) {
                    blockTableModel.addRow(new Object[]{allowBlock.getEnable().equals(AllowBlock.ENABLE), allowBlock.getLocation()});
                }
            }
        } catch (SQLException e) {
            Application.showError(e);
        }
        blockTable.setShowHorizontalLines(true);
        blockTable.setShowVerticalLines(true);

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    private void initTable() {
        blockTable.setShowHorizontalLines(true);
        blockTable.setShowVerticalLines(true);
        blockTable.setShowGrid(false);
        blockTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        blockTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        blockTable.getColumnModel().getColumn(0).setCellRenderer(new PreferencesDialog.BooleanRenderer());
        blockTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        JTextField locationTextField = new JTextField();
        DefaultCellEditor locationCellEditor = new DefaultCellEditor(locationTextField);
        locationTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                locationCellEditor.stopCellEditing();
            }
        });
        locationCellEditor.setClickCountToStart(2);
        blockTable.getColumnModel().getColumn(1).setCellEditor(locationCellEditor);
    }

    public JCheckBox getEnableCheckBox() {
        return enableCheckBox;
    }

    public DefaultTableModel getBlockTableModel() {
        return blockTableModel;
    }

    public JTable getBlockTable() {
        return blockTable;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getOkButton() {
        return okButton;
    }
}
