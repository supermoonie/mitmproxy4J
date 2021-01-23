package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AllowBlock;
import com.github.supermoonie.proxy.swing.entity.RequestMap;
import com.github.supermoonie.proxy.swing.gui.table.BooleanRenderer;
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
public class RemoteMapDialog extends JDialog {

    private final JCheckBox enableCheckBox = new JCheckBox("Enable Remote Map");
    private final DefaultTableModel remoteMapTableModel = new DefaultTableModel(null, new String[]{"Enable", "From", "To"});
    private final JTable requestMapTable = new JTable(remoteMapTableModel) {
        private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class, String.class};

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }
    };
    private final JButton addButton = new JButton("Add");
    private final JButton removeButton = new JButton("Remove");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton okButton = new JButton("OK");

    public RemoteMapDialog(Frame owner, String title, boolean modal) {
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
        // remote map table panel
        initTable();
        JPanel allowPanel = new JPanel(new BorderLayout() {{
            setVgap(10);
        }});
        allowPanel.add(new JScrollPane(requestMapTable), BorderLayout.CENTER);
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

        boolean enable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_REMOTE_MAP_ENABLE, ApplicationPreferences.DEFAULT_REMOTE_MAP_ENABLE);
        enableCheckBox.setSelected(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        requestMapTable.setEnabled(enable);
        Dao<RequestMap, Integer> requestMapDao = DaoCollections.getDao(RequestMap.class);
        try {
            List<RequestMap> requestMapList = requestMapDao.queryForAll();
            for (RequestMap reqMap : requestMapList) {
                if (reqMap.getMapType().equals(RequestMap.TYPE_REMOTE)) {
                    remoteMapTableModel.addRow(new Object[]{reqMap.getEnable().equals(AllowBlock.ENABLE), reqMap.getFromUrl(), reqMap.getToUrl()});
                }
            }
        } catch (SQLException e) {
            Application.showError(e);
        }
        requestMapTable.setShowHorizontalLines(true);
        requestMapTable.setShowVerticalLines(true);

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    private void initTable() {
        requestMapTable.setShowHorizontalLines(true);
        requestMapTable.setShowVerticalLines(true);
        requestMapTable.setShowGrid(false);
        requestMapTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        requestMapTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        requestMapTable.getColumnModel().getColumn(2).setPreferredWidth(600);
        requestMapTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanRenderer());
        requestMapTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        requestMapTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        JTextField fromTextField = new JTextField();
        DefaultCellEditor fromCellEditor = new DefaultCellEditor(fromTextField);
        fromTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fromCellEditor.stopCellEditing();
            }
        });
        fromCellEditor.setClickCountToStart(2);
        requestMapTable.getColumnModel().getColumn(1).setCellEditor(fromCellEditor);
        JTextField toTextField = new JTextField();
        DefaultCellEditor toCellEditor = new DefaultCellEditor(toTextField);
        toTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                toCellEditor.stopCellEditing();
            }
        });
        toCellEditor.setClickCountToStart(2);
        requestMapTable.getColumnModel().getColumn(2).setCellEditor(toCellEditor);
    }

    public JCheckBox getEnableCheckBox() {
        return enableCheckBox;
    }

    public DefaultTableModel getRemoteMapTableModel() {
        return remoteMapTableModel;
    }

    public JTable getRequestMapTable() {
        return requestMapTable;
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
