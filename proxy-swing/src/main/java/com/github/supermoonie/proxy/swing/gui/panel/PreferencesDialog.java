package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.github.supermoonie.proxy.swing.proxy.intercept.InternalProxyInterceptInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        splitPane.setDividerLocation(120);
        // left
        JTree preferenceTree = new JTree();
        preferenceTree.setShowsRootHandles(false);
        preferenceTree.setRootVisible(false);
        preferenceTree.setCellRenderer(new DefaultTreeCellRenderer() {{
            setLeafIcon(null);
            setClosedIcon(null);
            setOpenIcon(null);
        }});
        preferenceTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) preferenceTree.getLastSelectedPathComponent();
                String selected = node.getUserObject().toString();
                if (selected.equals(current)) {
                    return;
                }
                current = selected;
                rightPanel.removeAll();
                if ("Appearance".equals(selected)) {
                    rightPanel.add(appearancePanel());
                } else if ("Proxy".equals(selected)) {
                    rightPanel.add(proxyPanel());
                }
                rightPanel.updateUI();
            }
        });
        DefaultTreeModel treeModel = (DefaultTreeModel) preferenceTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel.setRoot(root);
        DefaultMutableTreeNode appearanceNode = new DefaultMutableTreeNode("Appearance");
        root.add(appearanceNode);
        root.add(new DefaultMutableTreeNode("Proxy"));
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
                Application.MAIN_FRAME.setTitle("Lightning:" + ProxyManager.getInternalProxy().getPort());
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
                    } catch (InterruptedException ignore) {}
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
