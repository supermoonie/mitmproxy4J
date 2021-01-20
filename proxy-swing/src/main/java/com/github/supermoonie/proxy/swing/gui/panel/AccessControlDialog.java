package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.ApplicationPreferences;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Set;

/**
 * @author supermoonie
 * @since 2021/1/17
 */
public class AccessControlDialog extends JDialog {

    private final JTable accessTable = new JTable();
    private final JButton addButton = new JButton("Add");
    private final JButton removeButton = new JButton("Remove");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton okButton = new JButton("OK");

    public AccessControlDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        // ip panel
        accessTable.setShowHorizontalLines(true);
        accessTable.setModel(new DefaultTableModel(null, new String[]{"IP Range"}));
        accessTable.getColumnModel().getColumn(0).setCellEditor(new CellEditor(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField ipTextField = (JTextField) input;
                return ipTextField.getText().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
            }
        }));
        JPanel accessControlPanel = new JPanel(new BorderLayout() {{
            setVgap(10);
        }});
        accessControlPanel.add(new JScrollPane(accessTable), BorderLayout.CENTER);
        JPanel accessControlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(10);
        }});
        accessControlButtonsPanel.add(addButton);
        accessControlButtonsPanel.add(removeButton);
        accessControlPanel.add(accessControlButtonsPanel, BorderLayout.SOUTH);
        container.add(accessControlPanel);
        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.borderColor")));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(2));
        buttonPanel.add(okButton);
        container.add(Box.createVerticalStrut(10));
        container.add(buttonPanel);

        DefaultTableModel model = (DefaultTableModel) accessTable.getModel();
        Set<String> accessControlList = ApplicationPreferences.getAccessControl();
        for (String ip : accessControlList) {
            model.addRow(new String[]{ip});
        }

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
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

    public JTable getAccessTable() {
        return accessTable;
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
