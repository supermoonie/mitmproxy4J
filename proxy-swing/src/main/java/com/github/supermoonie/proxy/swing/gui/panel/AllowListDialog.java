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
 * @since 2021/1/18
 */
public class AllowListDialog extends JDialog {

    private final JCheckBox enableCheckBox = new JCheckBox("Enable Allow List");
    private final DefaultTableModel allowTableModel = new DefaultTableModel(null, new String[]{"Enable", "Location Regex"});
    private final JTable allowTable = new JTable(allowTableModel) {
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

    public AllowListDialog(Frame owner, String title, boolean modal) {
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
        // allow list table panel
        initTable();
        JPanel allowPanel = new JPanel(new BorderLayout() {{
            setVgap(10);
        }});
        allowPanel.add(new JScrollPane(allowTable), BorderLayout.CENTER);
        JPanel operatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(10);
        }});
        operatePanel.add(addButton);
        operatePanel.add(removeButton);
        allowPanel.add(operatePanel, BorderLayout.SOUTH);
        container.add(allowPanel);
        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.borderColor")));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(2));
        buttonPanel.add(okButton);
        container.add(Box.createVerticalStrut(10));
        container.add(buttonPanel);

        boolean enable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_ALLOW_LIST_ENABLE, ApplicationPreferences.VALUE_ALLOW_LIST_ENABLE);
        enableCheckBox.setSelected(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        allowTable.setEnabled(enable);
        Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
        try {
            List<AllowBlock> allowBlockList = allowBlockDao.queryForAll();
            for (AllowBlock allowBlock : allowBlockList) {
                if (allowBlock.getType().equals(AllowBlock.TYPE_ALLOW)) {
                    allowTableModel.addRow(new Object[]{allowBlock.getEnable().equals(AllowBlock.ENABLE), allowBlock.getLocation()});
                }
            }
        } catch (SQLException e) {
            Application.showError(e);
        }
        allowTable.setShowHorizontalLines(true);
        allowTable.setShowVerticalLines(true);

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    private void initTable() {
        allowTable.setShowHorizontalLines(true);
        allowTable.setShowVerticalLines(true);
        allowTable.setShowGrid(false);
        allowTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        allowTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        allowTable.getColumnModel().getColumn(0).setCellRenderer(new PreferencesDialog.BooleanRenderer());
        allowTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
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
        allowTable.getColumnModel().getColumn(1).setCellEditor(locationCellEditor);
    }

    public JCheckBox getEnableCheckBox() {
        return enableCheckBox;
    }

    public DefaultTableModel getAllowTableModel() {
        return allowTableModel;
    }

    public JTable getAllowTable() {
        return allowTable;
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
