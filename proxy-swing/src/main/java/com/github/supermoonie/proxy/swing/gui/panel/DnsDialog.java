package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Dns;
import com.github.supermoonie.proxy.swing.entity.HostMap;
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
    private final DefaultTableModel hostMapTableModel = new DefaultTableModel(null, new String[]{"Enable", "Host", "IP Address"});
    private final JTable hostMapTable = new JTable(hostMapTableModel) {
        private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class, String.class};

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }
    };
    private final JButton dnsAddButton = new JButton("Add");
    private final JButton dnsRemoveButton = new JButton("Remove");
    private final JButton hostMapAddButton = new JButton("Add");
    private final JButton hostMapRemoveButton = new JButton("Remove");
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
        dnsMapPanel.add(new JScrollPane(hostMapTable){{
            setPreferredSize(new Dimension(600, 200));
        }}, BorderLayout.CENTER);
        JPanel dnsMapOpePanel = new JPanel(new FlowLayout(FlowLayout.CENTER){{setHgap(10);}});
        dnsMapOpePanel.add(hostMapAddButton);
        dnsMapOpePanel.add(hostMapRemoveButton);
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
        Dao<Dns, Integer> dnsDao = DaoCollections.getDao(Dns.class);
        Dao<HostMap, Integer> hostMapDao = DaoCollections.getDao(HostMap.class);
        try {
            List<Dns> dnsList = dnsDao.queryForAll();
            List<HostMap> hostMapList = hostMapDao.queryForAll();
            for (Dns dns : dnsList) {
                dnsTableModel.addRow(new Object[]{dns.getEnable() == Dns.ENABLE, dns.getIp(), dns.getPort()});
            }
            for (HostMap hm : hostMapList) {
                hostMapTableModel.addRow(new Object[]{hm.getEnable() == Dns.ENABLE, hm.getHost(), hm.getIp()});
            }
        } catch (SQLException e) {
            Application.showError(e);
            return;
        }

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

        hostMapTable.setShowHorizontalLines(true);
        hostMapTable.setShowVerticalLines(true);
        hostMapTable.setShowGrid(false);
        hostMapTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        hostMapTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        hostMapTable.getColumnModel().getColumn(2).setPreferredWidth(600);
        hostMapTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanRenderer());
        hostMapTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });
        hostMapTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
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

    public DefaultTableModel getHostMapTableModel() {
        return hostMapTableModel;
    }

    public JTable getHostMapTable() {
        return hostMapTable;
    }

    public JButton getHostMapAddButton() {
        return hostMapAddButton;
    }

    public JButton getHostMapRemoveButton() {
        return hostMapRemoveButton;
    }

    public JCheckBox getEnableSysDnsCheckBox() {
        return enableSysDnsCheckBox;
    }
}
