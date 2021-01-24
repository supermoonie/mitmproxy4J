package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2021/1/23
 */
public class ThrottlingDialog extends JDialog {

    private final JCheckBox enableCheckBox = new JCheckBox("Enable Throttling");
    private final JSpinner writeLimitSpinner = new JSpinner();
    private final JSpinner readLimitSpinner = new JSpinner();
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton okButton = new JButton("OK");

    public ThrottlingDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        writeLimitSpinner.setSize(new Dimension(200, 25));
        readLimitSpinner.setSize(new Dimension(200, 25));
        writeLimitSpinner.setEditor(new JSpinner.NumberEditor(writeLimitSpinner, "#.00"));
        readLimitSpinner.setEditor(new JSpinner.NumberEditor(readLimitSpinner, "#.00"));
        // container
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        // enable panel
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enablePanel.add(enableCheckBox);
        container.add(enablePanel);
        // write limit panel
        JPanel writeLimitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        writeLimitPanel.add(new JLabel("    Upload:"));
        writeLimitPanel.add(Box.createHorizontalStrut(2));
        writeLimitPanel.add(writeLimitSpinner);
        writeLimitPanel.add(Box.createHorizontalStrut(2));
        writeLimitPanel.add(new JLabel("KB/s"));
        container.add(writeLimitPanel);
        // read limit panel
        JPanel readLimitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        readLimitPanel.add(new JLabel("Download:"));
        readLimitPanel.add(Box.createHorizontalStrut(2));
        readLimitPanel.add(readLimitSpinner);
        readLimitPanel.add(Box.createHorizontalStrut(2));
        readLimitPanel.add(new JLabel("KB/s"));
        container.add(readLimitPanel);
        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(2));
        buttonPanel.add(okButton);
        container.add(Box.createVerticalStrut(10));
        container.add(buttonPanel);

        GlobalChannelTrafficShapingHandler trafficShapingHandler = ProxyManager.getInternalProxy().getTrafficShapingHandler();
        boolean enable = ProxyManager.getInternalProxy().isTrafficShaping();
        enableCheckBox.setSelected(enable);
        writeLimitSpinner.setValue(trafficShapingHandler.getWriteLimit() / 1000);
        readLimitSpinner.setValue(trafficShapingHandler.getReadLimit() / 1000);
        if (!enable) {
            writeLimitSpinner.setEnabled(false);
            readLimitSpinner.setEnabled(false);
        }

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    public JCheckBox getEnableCheckBox() {
        return enableCheckBox;
    }

    public JSpinner getWriteLimitSpinner() {
        return writeLimitSpinner;
    }

    public JSpinner getReadLimitSpinner() {
        return readLimitSpinner;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getOkButton() {
        return okButton;
    }
}
