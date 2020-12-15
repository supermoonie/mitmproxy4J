package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.gui.table.FormDataTable;
import com.github.supermoonie.proxy.swing.gui.table.NameValueTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/12/6
 */
public class SendRequestDialog extends JDialog {

    /**
     * method + url
     */
    private final JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"});
    private final JTextField urlTextField = new JTextField("https://");

    /**
     * send & cancel button
     */
    private final JCheckBox closeWindowCheckBox = new JCheckBox("Close window after send");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton sendButton = new JButton("Send");

    /**
     * query table
     */
    private final JTable queryTable = new NameValueTable();

    /**
     * header table
     */
    private final JTable headerTable = new NameValueTable();

    /**
     * body component
     */
    private final JRadioButton noneRadioButton = new JRadioButton("none");
    ;
    private final JRadioButton formDataRadioButton = new JRadioButton("form-data");
    private final JRadioButton urlEncodedRadioButton = new JRadioButton("x-www-form-urlencoded");
    private final JRadioButton rawRadioButton = new JRadioButton("raw");
    private final JRadioButton binaryRadioButton = new JRadioButton("binary");
    private final JComboBox<String> rawTypeComboBox = new JComboBox<>(new String[]{"Text", "JavaScript", "JSON", "HTML", "XML"});
    private final JPanel nonePanel = new JPanel(new GridBagLayout());
    private final JTable formDataTable = new FormDataTable();
    private final JTable urlEncodedTable = new NameValueTable();

    public SendRequestDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        super.setLayout(new BorderLayout());
        // north panel
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        methodComboBox.setEditable(false);
        urlTextField.requestFocus();
        northPanel.add(methodComboBox, BorderLayout.WEST);
        northPanel.add(urlTextField, BorderLayout.CENTER);
        super.add(northPanel, BorderLayout.NORTH);
        // center panel
        JTabbedPane centerPane = initCenterPanel();
        super.add(centerPane, BorderLayout.CENTER);
        // south panel
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(closeWindowCheckBox);
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(sendButton);
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(cancelButton);
        super.add(southPanel, BorderLayout.SOUTH);

        super.setPreferredSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    private JTabbedPane initCenterPanel() {
        JTabbedPane centerPane = new JTabbedPane();
        JScrollPane queryScrollPane = new JScrollPane(queryTable);
        centerPane.add("Query", queryScrollPane);
        JScrollPane headerScrollPane = new JScrollPane(headerTable);
        centerPane.add("Header", headerScrollPane);
        JPanel bodyPanel = initBodyPanel();
        centerPane.add("Body", bodyPanel);
        return centerPane;
    }

    private JPanel initBodyPanel() {
        JPanel bodyPanel = new JPanel(new BorderLayout());
        // radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        noneRadioButton.setSelected(true);
        rawTypeComboBox.setEnabled(false);
        rawTypeComboBox.setMaximumSize(new Dimension(100, 23));
        rawRadioButton.addChangeListener(e -> rawTypeComboBox.setEnabled(rawRadioButton.isSelected()));
        radioButtonGroup.add(noneRadioButton);
        radioButtonGroup.add(formDataRadioButton);
        radioButtonGroup.add(urlEncodedRadioButton);
        radioButtonGroup.add(rawRadioButton);
        radioButtonGroup.add(binaryRadioButton);
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(noneRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(formDataRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(urlEncodedRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(rawRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(binaryRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(rawTypeComboBox);
        northPanel.add(buttonPanel, BorderLayout.CENTER);
        northPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
        bodyPanel.add(northPanel, BorderLayout.NORTH);
        // body content
        nonePanel.add(new JLabel("This request does not have a body"));
        nonePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane urlEncodedScrollPanel = new JScrollPane(urlEncodedTable);
        JScrollPane formDataScrollPanel = new JScrollPane(formDataTable);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(nonePanel);
        bodyPanel.add(centerPanel, BorderLayout.CENTER);
        // action listener
        noneRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(nonePanel);
            centerPanel.updateUI();
        });
        urlEncodedRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(urlEncodedScrollPanel);
            centerPanel.updateUI();
        });
        formDataRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(formDataScrollPanel);
            centerPanel.updateUI();
        });
        return bodyPanel;
    }
}
