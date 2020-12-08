package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.gui.table.TableHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/12/6
 */
public class SendRequestDialog extends JDialog {

    private final JPanel northPanel;
    private final JComboBox<String> methodComboBox;
    private final JTextField urlTextField;

    private final JPanel southPanel;
    private final JButton cancelButton;
    private final JButton sendButton;

    public SendRequestDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        super.setLayout(new BorderLayout());
        // north panel
        northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        methodComboBox = new JComboBox<>(new String[]{"GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"});
        methodComboBox.setEditable(false);
        urlTextField = new JTextField();
        urlTextField.requestFocus();
        northPanel.add(methodComboBox, BorderLayout.WEST);
        northPanel.add(urlTextField, BorderLayout.CENTER);
        super.add(northPanel, BorderLayout.NORTH);
        // center panel
        JTabbedPane centerPane = new JTabbedPane();
        JTable queryTable = new JTable(new DefaultTableModel(new Object[][]{
                {true, "foo", "bar"},
                {false, "abc", "123"}
        }, new String[]{"", "Name", "Value"}) {
            final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class, String.class};
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        TableColumn column = queryTable.getColumnModel().getColumn(0);
        queryTable.getTableHeader().setResizingColumn(column);
        column.setWidth(50);
        JScrollPane queryScrollPane = new JScrollPane(queryTable);
        centerPane.add("Query", queryScrollPane);
        JTable headerTable = new JTable(new DefaultTableModel(null, new String[]{"Name", "Value"}));
        JScrollPane headerScrollPane = new JScrollPane(headerTable);
        centerPane.add("Header", headerScrollPane);
        JTable bodyTable = new JTable(new DefaultTableModel(null, new String[]{"Name", "Value"}));
        JScrollPane bodyScrollPane = new JScrollPane(bodyTable);
        centerPane.add("Body", bodyScrollPane);
        super.add(centerPane, BorderLayout.CENTER);
        // south panel
        southPanel = new JPanel(new FlowLayout());
        cancelButton = new JButton("Cancel");
        sendButton = new JButton("Send");
        southPanel.add(sendButton);
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(cancelButton);
        super.add(southPanel, BorderLayout.SOUTH);

        super.setPreferredSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }
}
