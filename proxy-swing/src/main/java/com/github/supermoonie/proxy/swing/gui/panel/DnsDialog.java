package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.gui.table.BooleanRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author supermoonie
 * @since 2021/1/28
 */
public class DnsDialog extends JDialog {

    private final JCheckBox enableCheckBox = new JCheckBox("Enable DNS");
    private final DefaultTableModel dnsTableModel = new DefaultTableModel(null, new String[]{"Enable", "IP Address", "Port"});
    private final JTable dnsTable = new JTable(dnsTableModel) {
        private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class, Integer.class};

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }
    };
    private final JCheckBox enableSysDnsCheckBox = new JCheckBox("Enable Local Host");
    private final DefaultTableModel dnsMapTableModel = new DefaultTableModel(null, new String[]{"Enable", "Host", "IP Address"});
    private final JTable dnsMapTable = new JTable(dnsMapTableModel) {
        private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class, String.class};

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }
    };
    private final JButton dnsAddButton = new JButton("Add");
    private final JButton dnsRemoveButton = new JButton("Remove");
    private final JButton dnsMapAddButton = new JButton("Add");
    private final JButton dnsMapRemoveButton = new JButton("Remove");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton okButton = new JButton("OK");

    public DnsDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        // container
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        // enable panel
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enablePanel.add(enableCheckBox);
        // dns list table panel
        initTable();
        JPanel dnsPanel = new JPanel(new BorderLayout() {{
            setVgap(10);
        }});
        dnsPanel.setBorder(BorderFactory.createTitledBorder("DNS Server"));
        dnsPanel.add(enablePanel, BorderLayout.NORTH);
        dnsPanel.add(new JScrollPane(dnsTable){{
            setPreferredSize(new Dimension(600, 200));
        }}, BorderLayout.CENTER);
        JPanel operatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(10);
        }});
        operatePanel.add(dnsAddButton);
        operatePanel.add(dnsRemoveButton);
        dnsPanel.add(operatePanel, BorderLayout.SOUTH);
        container.add(dnsPanel);
        // enable sys dns panel
        JPanel enableSysDnsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enableSysDnsPanel.add(enableSysDnsCheckBox);
        // dns map table panel
        JPanel dnsMapPanel = new JPanel(new BorderLayout(){{setVgap(10);}});
        dnsMapPanel.setBorder(BorderFactory.createTitledBorder("Host Map"));
        dnsMapPanel.add(enableSysDnsPanel, BorderLayout.NORTH);
        dnsMapPanel.add(new JScrollPane(dnsMapTable){{
            setPreferredSize(new Dimension(600, 200));
        }}, BorderLayout.CENTER);
        JPanel dnsMapOpePanel = new JPanel(new FlowLayout(FlowLayout.CENTER){{setHgap(10);}});
        dnsMapOpePanel.add(dnsMapAddButton);
        dnsMapOpePanel.add(dnsMapRemoveButton);
        dnsMapPanel.add(dnsMapOpePanel, BorderLayout.SOUTH);
        container.add(dnsMapPanel);
        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.borderColor")));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(2));
        buttonPanel.add(okButton);
        container.add(Box.createVerticalStrut(10));
        container.add(buttonPanel);

        boolean dnsEnable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_DNS_ENABLE, ApplicationPreferences.DEFAULT_DNS_ENABLE);
        boolean localHostEnable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_DNS_LOCAL_HOST_ENABLE, ApplicationPreferences.DEFAULT_DNS_LOCAL_HOST_ENABLE);
        enableCheckBox.setSelected(dnsEnable);
        dnsTable.setEnabled(dnsEnable);
        dnsAddButton.setEnabled(dnsEnable);
        dnsRemoveButton.setEnabled(dnsEnable);
        enableSysDnsCheckBox.setSelected(localHostEnable);

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    private void initTable() {
        dnsTable.setShowHorizontalLines(true);
        dnsTable.setShowVerticalLines(true);
        dnsTable.setShowGrid(false);
        dnsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        dnsTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        dnsTable.getColumnModel().getColumn(2).setPreferredWidth(600);
        dnsTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanRenderer());
        dnsTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        dnsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        JTextField ipTextField = new JTextField();
        DefaultCellEditor ipCellEditor = new DefaultCellEditor(ipTextField);
        ipTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                ipCellEditor.stopCellEditing();
            }
        });
        ipCellEditor.setClickCountToStart(2);
        dnsTable.getColumnModel().getColumn(1).setCellEditor(ipCellEditor);
        JTextField portTextField = new JTextField();
        DefaultCellEditor portCellEditor = new DefaultCellEditor(portTextField);
        portTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                portCellEditor.stopCellEditing();
            }
        });
        portCellEditor.setClickCountToStart(2);
        dnsTable.getColumnModel().getColumn(2).setCellEditor(portCellEditor);

        dnsMapTable.setShowHorizontalLines(true);
        dnsMapTable.setShowVerticalLines(true);
        dnsMapTable.setShowGrid(false);
        dnsMapTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        dnsMapTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        dnsMapTable.getColumnModel().getColumn(2).setPreferredWidth(600);
        dnsMapTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanRenderer());
        dnsMapTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        dnsMapTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        JTextField hostTextField = new JTextField();
        DefaultCellEditor hostCellEditor = new DefaultCellEditor(hostTextField);
        hostTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                hostCellEditor.stopCellEditing();
            }
        });
        hostCellEditor.setClickCountToStart(2);
        dnsTable.getColumnModel().getColumn(1).setCellEditor(hostCellEditor);
        JTextField ipAddressTextField = new JTextField();
        DefaultCellEditor ipAddressCellEditor = new DefaultCellEditor(ipAddressTextField);
        ipAddressTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                ipAddressCellEditor.stopCellEditing();
            }
        });
        ipAddressCellEditor.setClickCountToStart(2);
        dnsTable.getColumnModel().getColumn(2).setCellEditor(ipAddressCellEditor);
    }

    public JCheckBox getEnableCheckBox() {
        return enableCheckBox;
    }

    public DefaultTableModel getDnsTableModel() {
        return dnsTableModel;
    }

    public JTable getDnsTable() {
        return dnsTable;
    }

    public JButton getDnsAddButton() {
        return dnsAddButton;
    }

    public JButton getDnsRemoveButton() {
        return dnsRemoveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public DefaultTableModel getDnsMapTableModel() {
        return dnsMapTableModel;
    }

    public JTable getDnsMapTable() {
        return dnsMapTable;
    }

    public JButton getDnsMapAddButton() {
        return dnsMapAddButton;
    }

    public JButton getDnsMapRemoveButton() {
        return dnsMapRemoveButton;
    }

    public JCheckBox getEnableSysDnsCheckBox() {
        return enableSysDnsCheckBox;
    }
}
