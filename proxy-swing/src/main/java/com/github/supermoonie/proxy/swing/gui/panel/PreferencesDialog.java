package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AllowBlock;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.github.supermoonie.proxy.swing.proxy.intercept.DefaultConfigIntercept;
import com.github.supermoonie.proxy.swing.proxy.intercept.InternalProxyInterceptInitializer;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

/**
 * @author supermoonie
 * @since 2020/12/18
 */
public class PreferencesDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(PreferencesDialog.class);

    private static final List<String> FAMILIES = List.of("Default", "Arial", "Cantarell", "Comic Sans MS", "Courier New", "DejaVu Sans",
            "Dialog", "Liberation Sans", "Monospaced", "Noto Sans", "Roboto",
            "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana");

    private String current;

    public PreferencesDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        // Split
        JSplitPane splitPane = new JSplitPane();
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.getInsets().set(10, 10, 10, 10);
        JPanel rightPanel = new JPanel(new BorderLayout());
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(200);
        // left
        JTree preferenceTree = new JTree();
        preferenceTree.setShowsRootHandles(false);
        preferenceTree.setRootVisible(false);
        preferenceTree.setCellRenderer(new DefaultTreeCellRenderer() {{
            setLeafIcon(null);
            setClosedIcon(null);
            setOpenIcon(null);
        }});
        preferenceTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) preferenceTree.getLastSelectedPathComponent();
            String selected = node.getUserObject().toString();
            if (selected.equals(current)) {
                return;
            }
            current = selected;
            rightPanel.removeAll();
            switch (selected) {
                case "Appearance":
                    rightPanel.add(appearancePanel());
                    break;
                case "Proxy & Access Control":
                    rightPanel.add(proxyPanel());
                    break;
                case "Throttling":
                    rightPanel.add(throttlingPanel());
                    break;
                case "Allow List":
                    rightPanel.add(allowListPanel());
                    break;
                case "Block List":
                    rightPanel.add(blockListPanel());
                    break;
            }
            rightPanel.updateUI();
        });
        DefaultTreeModel treeModel = (DefaultTreeModel) preferenceTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel.setRoot(root);
        DefaultMutableTreeNode appearanceNode = new DefaultMutableTreeNode("Appearance");
        root.add(appearanceNode);
        root.add(new DefaultMutableTreeNode("Proxy & Access Control"));
        root.add(new DefaultMutableTreeNode("Allow List"));
        root.add(new DefaultMutableTreeNode("Block List"));
        preferenceTree.setSelectionPath(new TreePath(appearanceNode.getPath()));
        leftPanel.add(new JScrollPane(preferenceTree), BorderLayout.CENTER);
        // default right panel
        rightPanel.add(appearancePanel());
        current = "Appearance";

        getContentPane().add(splitPane);
        super.setPreferredSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(owner);
        super.setVisible(true);
    }

    private JPanel appearancePanel() {
        JComboBox<String> fontComboBox = new JComboBox<>() {{
            for (String font : FAMILIES) {
                addItem(font);
            }
            setMaximumSize(new Dimension(50, 25));
        }};
        JComboBox<Integer> fontSizeComboBox = new JComboBox<>() {{
            addItem(12);
            addItem(13);
            addItem(14);
            addItem(16);
            addItem(18);
            addItem(20);
            addItem(22);
        }};
        JComboBox<String> themeComboBox = new JComboBox<>() {{
            addItem("Dark");
            addItem("Light");
        }};
        JPanel appearanceSettingPanel = new JPanel(new BorderLayout());
        appearanceSettingPanel.getInsets().set(10, 10, 10, 10);
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder("Appearance"));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        themePanel.add(new JLabel("Theme:"));
        themePanel.add(Box.createHorizontalStrut(2));
        themePanel.add(themeComboBox);
        container.add(themePanel);
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        fontPanel.add(new JLabel("Font:"));
        fontPanel.add(Box.createHorizontalStrut(2));
        fontPanel.add(fontComboBox);
        fontPanel.add(Box.createHorizontalStrut(10));
        fontPanel.add(new JLabel("Size:"));
        fontPanel.add(Box.createHorizontalStrut(2));
        fontPanel.add(fontSizeComboBox);
        container.add(fontPanel);
        appearanceSettingPanel.add(container, BorderLayout.CENTER);
        // buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT) {{
            setHgap(10);
        }});
        buttonsPanel.add(new JButton("Cancel") {{
            addActionListener(e -> PreferencesDialog.this.setVisible(false));
        }});
        buttonsPanel.add(new JButton("Apply") {{
            setBackground(Color.decode("#4f8bc9"));
            setForeground(Color.WHITE);
            addActionListener(e -> {
                String family = Objects.requireNonNullElse(fontComboBox.getSelectedItem(), ApplicationPreferences.VALUE_DEFAULT_FONT_FAMILY).toString();
                family = family.equals("Default") ? ApplicationPreferences.VALUE_DEFAULT_FONT_FAMILY : family;
                int fontSize = Integer.parseInt(Objects.requireNonNullElse(fontSizeComboBox.getSelectedItem(), ApplicationPreferences.VALUE_DEFAULT_FONT_SIZE).toString());
                ThemeManager.setFont(family, fontSize);
                int themeSelectedIndex = themeComboBox.getSelectedIndex();
                if (0 == themeSelectedIndex) {
                    if (!ThemeManager.isDark()) {
                        ThemeManager.setDarkLookFeel();
                    }
                } else {
                    if (ThemeManager.isDark()) {
                        ThemeManager.setLightLookFeel();
                    }
                }
                ApplicationPreferences.getState().put(ApplicationPreferences.KEY_FONT_FAMILY, family);
                ApplicationPreferences.getState().putInt(ApplicationPreferences.KEY_FONT_SIZE, fontSize);
            });
        }});
        appearanceSettingPanel.add(buttonsPanel, BorderLayout.SOUTH);
        Font font = ApplicationPreferences.getFont();
        fontComboBox.setSelectedItem(font.getFamily().equals(ApplicationPreferences.VALUE_DEFAULT_FONT_FAMILY) ? "Default" : font.getFamily());
        fontSizeComboBox.setSelectedItem(font.getSize());
        if (ThemeManager.isDark()) {
            themeComboBox.setSelectedItem("Dark");
        } else {
            themeComboBox.setSelectedItem("Light");
        }
        return appearanceSettingPanel;
    }

    private JPanel proxyPanel() {
        JSpinner portSpinner = new JSpinner();
        JCheckBox authCheckBox = new JCheckBox("Authorization");
        JTextField usernameTextField = new JTextField();
        JTextField passwordTextField = new JTextField();
        JTable accessTable = new JTable();
        accessTable.setShowHorizontalLines(true);
        accessTable.setModel(new DefaultTableModel(null, new String[]{"IP Range"}));
        accessTable.getColumnModel().getColumn(0).setCellEditor(new CellEditor(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField ipTextField = (JTextField) input;
                System.out.println("ip: " + ipTextField.getText());
                return ipTextField.getText().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
            }
        }));
        authCheckBox.addActionListener(e -> {
            usernameTextField.setEnabled(authCheckBox.isSelected());
            passwordTextField.setEnabled(authCheckBox.isSelected());
        });
        JPanel proxySettingPanel = new JPanel(new BorderLayout());
        proxySettingPanel.getInsets().set(10, 10, 10, 10);
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder("Proxy"));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        portPanel.add(new JLabel("Port:"));
        portPanel.add(Box.createHorizontalStrut(2));
        portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, "#"));
        JFormattedTextField txt = ((JSpinner.NumberEditor) portSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        portSpinner.setPreferredSize(new Dimension(90, 25));
        portSpinner.setMaximumSize(new Dimension(90, 25));
        portPanel.add(portSpinner);
        container.add(portPanel);

        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        authPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        authPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        authPanel.add(authCheckBox);
        container.add(authPanel);

        JPanel authUserPanel = new JPanel(new BorderLayout() {{
            setHgap(10);
        }});
        authUserPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        authUserPanel.setBorder(BorderFactory.createEmptyBorder(2, 25, 2, 5));
        authUserPanel.add(new JLabel("Username:"), BorderLayout.WEST);
        authUserPanel.add(usernameTextField, BorderLayout.CENTER);
        container.add(authUserPanel);

        JPanel authPwdPanel = new JPanel(new BorderLayout() {{
            setHgap(10);
        }});
        authPwdPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        authPwdPanel.setBorder(BorderFactory.createEmptyBorder(2, 25, 2, 5));
        authPwdPanel.add(new JLabel("Password:"), BorderLayout.WEST);
        authPwdPanel.add(passwordTextField, BorderLayout.CENTER);
        container.add(authPwdPanel);

        JPanel accessControlPanel = new JPanel(new BorderLayout() {{
            setHgap(10);
            setVgap(10);
        }});
        accessControlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        accessControlPanel.add(new JLabel("Access Control"), BorderLayout.NORTH);
        accessControlPanel.add(new JScrollPane(accessTable), BorderLayout.CENTER);
        JPanel accessControlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(10);
        }});
        accessControlButtonsPanel.add(new JButton("Add") {{
            addActionListener(e -> {
                int editingRow = accessTable.getEditingRow();
                if (-1 == editingRow) {
                    DefaultTableModel model = (DefaultTableModel) accessTable.getModel();
                    model.addRow(new String[]{""});
                }
            });
        }});
        accessControlButtonsPanel.add(new JButton("Remove") {{
            addActionListener(e -> {
                int selectedRow = accessTable.getSelectedRow();
                int editingRow = accessTable.getEditingRow();
                int row = -1 != editingRow ? editingRow : selectedRow;
                if (-1 != row) {
                    DefaultTableModel model = (DefaultTableModel) accessTable.getModel();
                    model.removeRow(row);
                    accessTable.clearSelection();
                    accessTable.updateUI();
                    accessTable.setShowHorizontalLines(true);
                }
            });
        }});
        accessControlPanel.add(accessControlButtonsPanel, BorderLayout.SOUTH);
        container.add(accessControlPanel);

        proxySettingPanel.add(container, BorderLayout.CENTER);
        // buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT) {{
            setHgap(10);
        }});
        buttonsPanel.add(new JButton("Cancel") {{
            addActionListener(e -> PreferencesDialog.this.setVisible(false));
        }});
        buttonsPanel.add(new JButton("Apply") {{
            setBackground(Color.decode("#4f8bc9"));
            setForeground(Color.WHITE);
            addActionListener(e -> {
                this.setText("Applying");
                int port = Integer.parseInt(portSpinner.getValue().toString());
                boolean auth = authCheckBox.isSelected();
                String username = Objects.requireNonNullElse(usernameTextField.getText(), "");
                String password = Objects.requireNonNullElse(passwordTextField.getText(), "");
                if (ProxyManager.getInternalProxy().getPort() != port) {
                    ProxyManager.restart(port, auth, username, password, new InternalProxyInterceptInitializer());
                } else {
                    ProxyManager.getInternalProxy().setAuth(auth);
                    ProxyManager.getInternalProxy().setUsername(username);
                    ProxyManager.getInternalProxy().setPassword(password);
                }
                Application.MAIN_FRAME.setTitle("Lightning | Listening on " + ProxyManager.getInternalProxy().getPort());
                ApplicationPreferences.getState().putInt(ApplicationPreferences.KEY_PROXY_PORT, port);
                ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_PROXY_AUTH, auth);
                ApplicationPreferences.getState().put(ApplicationPreferences.KEY_PROXY_AUTH_USER, username);
                ApplicationPreferences.getState().put(ApplicationPreferences.KEY_PROXY_AUTH_PWD, password);
                Set<String> ipSet = new HashSet<>();
                DefaultTableModel model = (DefaultTableModel) accessTable.getModel();
                for (int row = 0; row < model.getRowCount(); row++) {
                    String ip = Objects.requireNonNullElse(model.getValueAt(row, 0), "").toString();
                    if (!"".equals(ip)) {
                        ipSet.add(ip);
                    }
                }
                ApplicationPreferences.setAccessControl(ipSet);
                this.setText("Done!");
                Application.EXECUTOR.execute(() -> {
                    try {
                        Thread.sleep(300);
                        SwingUtilities.invokeLater(() -> this.setText("Apply"));
                    } catch (InterruptedException ignore) {
                    }
                });
            });
        }});
        proxySettingPanel.add(buttonsPanel, BorderLayout.SOUTH);
        portSpinner.setValue(ApplicationPreferences.getState().getInt(ApplicationPreferences.KEY_PROXY_PORT, ApplicationPreferences.VALUE_DEFAULT_PROXY_PORT));
        authCheckBox.setSelected(ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_PROXY_AUTH, ApplicationPreferences.VALUE_DEFAULT_PROXY_AUTH));
        usernameTextField.setText(ApplicationPreferences.getState().get(ApplicationPreferences.KEY_PROXY_AUTH_USER, ""));
        passwordTextField.setText(ApplicationPreferences.getState().get(ApplicationPreferences.KEY_PROXY_AUTH_PWD, ""));
        usernameTextField.setEnabled(authCheckBox.isSelected());
        passwordTextField.setEnabled(authCheckBox.isSelected());
        DefaultTableModel model = (DefaultTableModel) accessTable.getModel();
        Set<String> accessControlList = ApplicationPreferences.getAccessControl();
        for (String ip : accessControlList) {
            model.addRow(new String[]{ip});
        }
        return proxySettingPanel;
    }

    private JPanel throttlingPanel() {
        JPanel throttlingSettingPanel = new JPanel(new BorderLayout());
        throttlingSettingPanel.getInsets().set(10, 10, 10, 10);
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder("Throttling"));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);

        return throttlingSettingPanel;
    }

    private JPanel allowListPanel() {
        JPanel allowSettingPanel = new JPanel(new BorderLayout());
        allowSettingPanel.getInsets().set(10, 10, 10, 10);
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder("Allow List"));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);

        JPanel enableAllowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox enableCheckBox = new JCheckBox("Enable Allow List");
        enableAllowPanel.add(enableCheckBox);
        enableAllowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        container.add(enableAllowPanel);
        DefaultTableModel allowTableModel = new DefaultTableModel(null, new String[]{"Enable", "Location Regex"}) {{
            addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.DELETE || e.getType() == TableModelEvent.UPDATE) {
                    Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
                    try {
                        DeleteBuilder<AllowBlock, Integer> deleteBuilder = allowBlockDao.deleteBuilder();
                        deleteBuilder.where().eq(AllowBlock.TYPE_FIELD_NAME, AllowBlock.TYPE_ALLOW);
                        deleteBuilder.delete();
                        Set<String> allowSet = new HashSet<>();
                        int rowCount = getRowCount();
                        for (int i = 0; i < rowCount; i++) {
                            boolean enable = (boolean) getValueAt(i, 0);
                            String location = (String) getValueAt(i, 1);
                            if (null != location && !"".equals(location)) {
                                AllowBlock allowBlock = new AllowBlock();
                                allowBlock.setEnable(enable ? AllowBlock.ENABLE : AllowBlock.DISABLE);
                                allowBlock.setType(AllowBlock.TYPE_ALLOW);
                                allowBlock.setLocation(location);
                                allowBlock.setTimeCreated(new Date());
                                allowBlockDao.create(allowBlock);
                            }
                            if (enable) {
                                allowSet.add(location);
                            }
                        }
                        DefaultConfigIntercept.INSTANCE.getAllowUriList().clear();
                        DefaultConfigIntercept.INSTANCE.getAllowUriList().addAll(allowSet);
                    } catch (SQLException t) {
                        Application.showError(t);
                    }
                }
            });
        }};
        JTable allowTable = new JTable(allowTableModel) {
            private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class};

            @Override
            public Class<?> getColumnClass(int column) {
                return columnTypes[column];
            }

            {
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        clearSelection();
                    }
                });
            }
        };
        allowTable.setShowHorizontalLines(true);
        allowTable.setShowVerticalLines(true);
        allowTable.setShowGrid(false);
        allowTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        allowTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        allowTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanRenderer());
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
        JPanel allowTablePanel = new JPanel(new BorderLayout());
        allowTablePanel.add(new JScrollPane(allowTable));
        JPanel allowTableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add") {{
            addActionListener(e -> {
                allowTable.clearSelection();
                allowTableModel.addRow(new Object[]{true, ""});
                allowTable.setShowHorizontalLines(true);
            });
        }};
        JButton removeButton = new JButton("Remove") {{
            addActionListener(e -> {
                int[] selectedRows = allowTable.getSelectedRows();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int row = selectedRows[i];
                    allowTableModel.removeRow(row);
                }
                allowTable.clearSelection();
                allowTable.setShowHorizontalLines(true);
            });
        }};
        allowTableButtonsPanel.add(addButton);
        allowTableButtonsPanel.add(removeButton);
        allowTablePanel.add(allowTableButtonsPanel, BorderLayout.SOUTH);
        container.add(allowTablePanel);
        allowSettingPanel.add(container);

        boolean enable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_ALLOW_LIST_ENABLE, ApplicationPreferences.VALUE_ALLOW_LIST_ENABLE);
        enableCheckBox.setSelected(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        allowTable.setEnabled(enable);
        enableCheckBox.addActionListener(e -> {
            allowTable.setEnabled(enableCheckBox.isSelected());
            addButton.setEnabled(enableCheckBox.isSelected());
            removeButton.setEnabled(enableCheckBox.isSelected());
            DefaultConfigIntercept.INSTANCE.setAllowFlag(enableCheckBox.isSelected());
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_ALLOW_LIST_ENABLE, enableCheckBox.isSelected());
        });
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
        return allowSettingPanel;
    }

    private JPanel blockListPanel() {
        JPanel blockSettingPanel = new JPanel(new BorderLayout());
        blockSettingPanel.getInsets().set(10, 10, 10, 10);
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder("Block List"));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);

        JPanel enableBlockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox enableCheckBox = new JCheckBox("Enable Block List");
        enableBlockPanel.add(enableCheckBox);
        enableBlockPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        container.add(enableBlockPanel);
        DefaultTableModel blockTableModel = new DefaultTableModel(null, new String[]{"Enable", "Location Regex"}) {{
            addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.DELETE || e.getType() == TableModelEvent.UPDATE) {
                    Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
                    try {
                        DeleteBuilder<AllowBlock, Integer> deleteBuilder = allowBlockDao.deleteBuilder();
                        deleteBuilder.where().eq(AllowBlock.TYPE_FIELD_NAME, AllowBlock.TYPE_BLOCK);
                        deleteBuilder.delete();
                        Set<String> blockSet = new HashSet<>();
                        int rowCount = getRowCount();
                        for (int i = 0; i < rowCount; i++) {
                            boolean enable = (boolean) getValueAt(i, 0);
                            String location = (String) getValueAt(i, 1);
                            if (null != location && !"".equals(location)) {
                                AllowBlock allowBlock = new AllowBlock();
                                allowBlock.setEnable(enable ? AllowBlock.ENABLE : AllowBlock.DISABLE);
                                allowBlock.setType(AllowBlock.TYPE_BLOCK);
                                allowBlock.setLocation(location);
                                allowBlock.setTimeCreated(new Date());
                                allowBlockDao.create(allowBlock);
                            }
                            if (enable) {
                                blockSet.add(location);
                            }
                        }
                        DefaultConfigIntercept.INSTANCE.getBlockUriList().clear();
                        DefaultConfigIntercept.INSTANCE.getBlockUriList().addAll(blockSet);
                    } catch (SQLException t) {
                        Application.showError(t);
                    }
                }
            });
        }};
        JTable blockTable = new JTable(blockTableModel) {
            private final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class};

            @Override
            public Class<?> getColumnClass(int column) {
                return columnTypes[column];
            }

            {
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        clearSelection();
                    }
                });
            }
        };
        blockTable.setShowHorizontalLines(true);
        blockTable.setShowVerticalLines(true);
        blockTable.setShowGrid(false);
        blockTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        blockTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        blockTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanRenderer());
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
        JPanel allowTablePanel = new JPanel(new BorderLayout());
        allowTablePanel.add(new JScrollPane(blockTable));
        JPanel allowTableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add") {{
            addActionListener(e -> {
                blockTable.clearSelection();
                blockTableModel.addRow(new Object[]{true, ""});
                blockTable.setShowHorizontalLines(true);
            });
        }};
        JButton removeButton = new JButton("Remove") {{
            addActionListener(e -> {
                int[] selectedRows = blockTable.getSelectedRows();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int row = selectedRows[i];
                    blockTableModel.removeRow(row);
                }
                blockTable.clearSelection();
                blockTable.setShowHorizontalLines(true);
            });
        }};
        allowTableButtonsPanel.add(addButton);
        allowTableButtonsPanel.add(removeButton);
        allowTablePanel.add(allowTableButtonsPanel, BorderLayout.SOUTH);
        container.add(allowTablePanel);
        blockSettingPanel.add(container);

        boolean enable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_BLOCK_LIST_ENABLE, ApplicationPreferences.VALUE_BLOCK_LIST_ENABLE);
        enableCheckBox.setSelected(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        blockTable.setEnabled(enable);
        enableCheckBox.addActionListener(e -> {
            blockTable.setEnabled(enableCheckBox.isSelected());
            addButton.setEnabled(enableCheckBox.isSelected());
            removeButton.setEnabled(enableCheckBox.isSelected());
            DefaultConfigIntercept.INSTANCE.setBlockFlag(enableCheckBox.isSelected());
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_BLOCK_LIST_ENABLE, enableCheckBox.isSelected());
        });
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
        return blockSettingPanel;
    }

    static class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
        private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        public BooleanRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
            setBorderPainted(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getForeground());
                if (ThemeManager.isDark()) {
                    setBackground(Color.decode("#4b6eaf"));
                } else {
                    setBackground(Color.decode("#2675bf"));
                }
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setSelected(((value != null) && (Boolean) value));
            setBorder(noFocusBorder);

            return this;
        }
    }

    private static class CellEditor extends DefaultCellEditor {

        final InputVerifier verifier;

        public CellEditor(InputVerifier verifier) {
            super(new JTextField());
            this.verifier = verifier;
        }

        @Override
        public boolean stopCellEditing() {
            if (verifier.verify(editorComponent)) {
                return super.stopCellEditing();
            } else {
                editorComponent.putClientProperty("JComponent.outline", "error");
                editorComponent.updateUI();
                editorComponent.requestFocus();
                return false;
            }
        }

    }
}
