package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.ExternalProxy;
import com.github.supermoonie.proxy.swing.gui.table.BooleanRenderer;
import com.j256.ormlite.dao.Dao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author supermoonie
 * @since 2021/1/26
 */
public class ExternalProxyDialog extends JDialog {

    private final JCheckBox enableCheckBox = new JCheckBox("Enable External Proxy");
    private final DefaultTableModel proxyTableModel = new DefaultTableModel(null, new String[]{"Enable", "Host Or Ip", "ProxyHost", "ProxyPort", "Auth", "User", "Password", "Type"});
    private final JTable proxyTable = new JTable(proxyTableModel) {
        private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class, String.class, Integer.class, Boolean.class, String.class, String.class, String.class};

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }
    };
    private final JButton addButton = new JButton("Add");
    private final JButton removeButton = new JButton("Remove");
    private final JTextArea bypassHostTextArea = new JTextArea();
    private final JCheckBox bypassLocalHostCheckBox = new JCheckBox("Bypass localhost/127.0.0.1");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton okButton = new JButton("OK");

    public ExternalProxyDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        bypassHostTextArea.setRows(5);
        // container
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        // enable panel
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enablePanel.add(enableCheckBox);
        container.add(enablePanel);
        // proxy list table panel
        initTable();
        JPanel proxyTablePanel = new JPanel(new BorderLayout() {{
            setVgap(10);
        }});
        proxyTablePanel.add(new JScrollPane(proxyTable), BorderLayout.CENTER);
        JPanel operatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(10);
        }});
        operatePanel.add(addButton);
        operatePanel.add(removeButton);
        proxyTablePanel.add(operatePanel, BorderLayout.SOUTH);
        container.add(proxyTablePanel);
        // bypass host panel
        JPanel bypassHostPanel = new JPanel(new BorderLayout());
        bypassHostPanel.add(new JLabel("Bypass for the following hosts:"), BorderLayout.NORTH);
        bypassHostPanel.add(new JScrollPane(bypassHostTextArea));
        container.add(bypassHostPanel);
        // bypass localhost panel
        JPanel bypassLocalHostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bypassLocalHostPanel.add(bypassLocalHostCheckBox);
        container.add(bypassLocalHostPanel);
        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.borderColor")));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(2));
        buttonPanel.add(okButton);
        container.add(Box.createVerticalStrut(10));
        container.add(buttonPanel);

        boolean enable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_EXTERNAL_PROXY_ENABLE, ApplicationPreferences.DEFAULT_EXTERNAL_PROXY_ENABLE);
        enableCheckBox.setSelected(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        proxyTable.setEnabled(enable);
        Dao<ExternalProxy, Integer> proxyDao = DaoCollections.getDao(ExternalProxy.class);
        try {
            List<ExternalProxy> proxyList = proxyDao.queryForAll();
            for (ExternalProxy proxy : proxyList) {
                proxyTableModel.addRow(new Object[]{proxy.getEnable() == ExternalProxy.ENABLE, proxy.getHost(), proxy.getProxyHost(), proxy.getProxyPort(), proxy.getProxyAuth() == ExternalProxy.ENABLE, proxy.getProxyUser(), proxy.getProxyPwd(), ProxyType.valueOf(proxy.getProxyType()).orElseThrow().name()});
            }
        } catch (SQLException e) {
            Application.showError(e);
        }
        proxyTable.setShowHorizontalLines(true);
        proxyTable.setShowVerticalLines(true);
        boolean bypassLocalhost = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_EXTERNAL_PROXY_BYPASS_LOCALHOST, ApplicationPreferences.DEFAULT_EXTERNAL_PROXY_BYPASS_LOCALHOST);
        String bypassHostList = ApplicationPreferences.getState().get(ApplicationPreferences.KEY_EXTERNAL_PROXY_BYPASS_LIST, "");
        bypassLocalHostCheckBox.setSelected(bypassLocalhost);
        bypassHostTextArea.setText(bypassHostList);

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setMinimumSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    private void initTable() {
        proxyTable.setShowHorizontalLines(true);
        proxyTable.setShowVerticalLines(true);
        proxyTable.setShowGrid(false);
        proxyTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanRenderer());
        proxyTable.getColumnModel().getColumn(4).setCellRenderer(new BooleanRenderer());
        JTextField hostTextField = new JTextField();
        DefaultCellEditor hostCellEditor = new DefaultCellEditor(hostTextField);
        hostTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                hostCellEditor.stopCellEditing();
            }
        });
        hostCellEditor.setClickCountToStart(2);
        proxyTable.getColumnModel().getColumn(1).setCellEditor(hostCellEditor);
        JTextField proxyHostTextField = new JTextField();
        DefaultCellEditor proxyHostCellEditor = new DefaultCellEditor(proxyHostTextField);
        proxyHostTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                proxyHostCellEditor.stopCellEditing();
            }
        });
        proxyHostCellEditor.setClickCountToStart(2);
        proxyTable.getColumnModel().getColumn(2).setCellEditor(proxyHostCellEditor);
        JTextField proxyPortTextField = new JTextField();
        DefaultCellEditor proxyPortCellEditor = new DefaultCellEditor(proxyPortTextField);
        proxyPortTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                proxyPortCellEditor.stopCellEditing();
            }
        });
        proxyPortCellEditor.setClickCountToStart(2);
        proxyTable.getColumnModel().getColumn(3).setCellEditor(proxyPortCellEditor);
        JTextField proxyUserTextField = new JTextField();
        DefaultCellEditor proxyUserCellEditor = new DefaultCellEditor(proxyUserTextField);
        proxyUserTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                proxyUserCellEditor.stopCellEditing();
            }
        });
        proxyUserCellEditor.setClickCountToStart(2);
        proxyTable.getColumnModel().getColumn(5).setCellEditor(proxyUserCellEditor);
        JTextField proxyPwdTextField = new JTextField();
        DefaultCellEditor proxyPwdCellEditor = new DefaultCellEditor(proxyPwdTextField);
        proxyPwdTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                proxyPwdCellEditor.stopCellEditing();
            }
        });
        proxyPwdCellEditor.setClickCountToStart(2);
        proxyTable.getColumnModel().getColumn(6).setCellEditor(proxyPwdCellEditor);
        JComboBox<String> typeComboBox = new JComboBox<>(Stream.of(ProxyType.values()).map(Enum::name).collect(Collectors.toList()).toArray(new String[]{}));
        DefaultCellEditor typeCellEditor = new DefaultCellEditor(typeComboBox);
        typeCellEditor.setClickCountToStart(2);
        proxyTable.getColumnModel().getColumn(7).setCellEditor(typeCellEditor);
    }

    public JCheckBox getEnableCheckBox() {
        return enableCheckBox;
    }

    public DefaultTableModel getProxyTableModel() {
        return proxyTableModel;
    }

    public JTable getProxyTable() {
        return proxyTable;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JTextArea getBypassHostTextArea() {
        return bypassHostTextArea;
    }

    public JCheckBox getBypassLocalHostCheckBox() {
        return bypassLocalHostCheckBox;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getOkButton() {
        return okButton;
    }
}
