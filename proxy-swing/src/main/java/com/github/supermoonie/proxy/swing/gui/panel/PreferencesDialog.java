package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.ThemeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/12/18
 */
public class PreferencesDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(PreferencesDialog.class);

    private static final List<String> FAMILIES = List.of("Default", "Arial", "Cantarell", "Comic Sans MS", "Courier New", "DejaVu Sans",
            "Dialog", "Liberation Sans", "Monospaced", "Noto Sans", "Roboto",
            "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana");

    private JPanel appearanceSettingPanel;
    private final JComboBox<String> fontComboBox = new JComboBox<>() {{
        for (String font : FAMILIES) {
            addItem(font);
        }
        setMaximumSize(new Dimension(50, 25));
    }};
    private final JComboBox<Integer> fontSizeComboBox = new JComboBox<>() {{
        addItem(12);
        addItem(13);
        addItem(14);
        addItem(16);
        addItem(18);
        addItem(20);
        addItem(22);
    }};
    private final JComboBox<String> themeComboBox = new JComboBox<>() {{
        addItem("Dark");
        addItem("Light");
    }};

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
        preferenceTree.setShowsRootHandles(true);
        preferenceTree.setRootVisible(false);
        preferenceTree.setCellRenderer(new DefaultTreeCellRenderer() {{
            setLeafIcon(null);
            setClosedIcon(null);
            setOpenIcon(null);
        }});
        DefaultTreeModel treeModel = (DefaultTreeModel) preferenceTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel.setRoot(root);
        DefaultMutableTreeNode appearanceNode = new DefaultMutableTreeNode("Appearance");
        root.add(appearanceNode);
//        root.add();
        preferenceTree.setSelectionPath(new TreePath(appearanceNode.getPath()));
        leftPanel.add(new JScrollPane(preferenceTree), BorderLayout.CENTER);
        // default right panel
        rightPanel.add(appearancePanel());

        getContentPane().add(splitPane);
        super.setPreferredSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(owner);
        super.setVisible(true);
    }

    private JPanel appearancePanel() {
        if (null == appearanceSettingPanel) {
            appearanceSettingPanel = new JPanel(new BorderLayout());
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
//            buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.borderColor")));
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
        }
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
}
