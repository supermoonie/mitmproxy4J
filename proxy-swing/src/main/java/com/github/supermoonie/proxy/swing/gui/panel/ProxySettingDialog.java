package com.github.supermoonie.proxy.swing.gui.panel;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2021/1/13
 */
public class ProxySettingDialog extends JDialog {

    private final JSpinner portSpinner = new JSpinner();
    private final JCheckBox authCheckBox = new JCheckBox("Authorization");
    private final JTextField usernameTextField = new JTextField("", 20);
    private final JTextField passwordTextField = new JPasswordField("", 20);
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton okButton = new JButton("OK");

    public ProxySettingDialog(Frame owner, String title, boolean modal, Integer port, Boolean auth, String user, String pwd) {
        super(owner, title, modal);
        portSpinner.setSize(new Dimension(100, 25));
        usernameTextField.setSize(new Dimension(200, 25));
        passwordTextField.setSize(new Dimension(200, 25));
        portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, "#"));
        JFormattedTextField txt = ((JSpinner.NumberEditor) portSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        // container
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);
        // port panel
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portPanel.add(new JLabel("Port :"));
        portPanel.add(Box.createHorizontalStrut(2));
        portPanel.add(portSpinner);
        container.add(portPanel);
        // auth panel
        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        authPanel.add(authCheckBox);
        container.add(authPanel);
        // username & password panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("Username:"));
        userPanel.add(Box.createHorizontalStrut(2));
        userPanel.add(usernameTextField);
        container.add(userPanel);
        JPanel pwdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pwdPanel.add(new JLabel("Password:"));
        pwdPanel.add(Box.createHorizontalStrut(2));
        pwdPanel.add(passwordTextField);
        container.add(pwdPanel);
        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(2));
        buttonPanel.add(okButton);
        container.add(Box.createVerticalStrut(10));
        container.add(buttonPanel);

        authCheckBox.addChangeListener(e -> {
            usernameTextField.setEnabled(authCheckBox.isSelected());
            passwordTextField.setEnabled(authCheckBox.isSelected());
        });
        if (null != port) {
            portSpinner.setValue(port);
        }
        if (null != auth) {
            authCheckBox.setSelected(auth);
        }
        if (null != user) {
            usernameTextField.setText(user);
        }
        if (null != pwd) {
            passwordTextField.setText(pwd);
        }
        usernameTextField.setEnabled(authCheckBox.isSelected());
        passwordTextField.setEnabled(authCheckBox.isSelected());

        super.getContentPane().add(container);
        super.getRootPane().setDefaultButton(okButton);
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    public JSpinner getPortSpinner() {
        return portSpinner;
    }

    public JCheckBox getAuthCheckBox() {
        return authCheckBox;
    }

    public JTextField getUsernameTextField() {
        return usernameTextField;
    }

    public JTextField getPasswordTextField() {
        return passwordTextField;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getOkButton() {
        return okButton;
    }
}
