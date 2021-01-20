package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author supermoonie
 * @since 2021/1/17
 */
public class AppearanceDialog extends JDialog {

    private static final java.util.List<String> FAMILIES = List.of("Default", "Arial", "Cantarell", "Comic Sans MS", "Courier New", "DejaVu Sans",
            "Dialog", "Liberation Sans", "Monospaced", "Noto Sans", "Roboto",
            "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana");

    private final JComboBox<String> themeComboBox = new JComboBox<>() {{
        addItem("Dark");
        addItem("Light");
    }};
    private final JComboBox<String> fontComboBox = new JComboBox<>() {{
        for (String font : FAMILIES) {
            addItem(font);
        }
        setMaximumSize(new Dimension(50, 25));
    }};
    private final JComboBox<Integer> fontSizeComboBox = new JComboBox<>() {{
        addItem(8);
        addItem(12);
        addItem(13);
        addItem(14);
        addItem(16);
        addItem(18);
        addItem(20);
        addItem(22);
    }};

    public AppearanceDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        // container
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        // theme panel
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.add(new JLabel("Theme:"));
        themePanel.add(Box.createHorizontalStrut(2));
        themePanel.add(themeComboBox);
        container.add(themePanel);
        // font panel
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontPanel.add(new JLabel("Font:"));
        fontPanel.add(Box.createHorizontalStrut(2));
        fontPanel.add(fontComboBox);
        fontPanel.add(Box.createHorizontalStrut(5));
        fontPanel.add(new JLabel("Size:"));
        fontPanel.add(Box.createHorizontalStrut(2));
        fontPanel.add(fontSizeComboBox);
        container.add(fontPanel);

        if (ThemeManager.isDark()) {
            themeComboBox.setSelectedItem("Dark");
        } else {
            themeComboBox.setSelectedItem("Light");
        }
        Font font = ApplicationPreferences.getFont();
        fontComboBox.setSelectedItem(font.getFamily().equals(ApplicationPreferences.VALUE_DEFAULT_FONT_FAMILY) ? "Default" : font.getFamily());
        fontSizeComboBox.setSelectedItem(font.getSize());

        super.getContentPane().add(container);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    public JComboBox<String> getThemeComboBox() {
        return themeComboBox;
    }

    public JComboBox<String> getFontComboBox() {
        return fontComboBox;
    }

    public JComboBox<Integer> getFontSizeComboBox() {
        return fontSizeComboBox;
    }
}
