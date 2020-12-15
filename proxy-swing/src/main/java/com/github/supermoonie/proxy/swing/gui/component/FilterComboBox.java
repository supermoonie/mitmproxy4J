package com.github.supermoonie.proxy.swing.gui.component;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/12/10
 */
public class FilterComboBox extends JComboBox<String> {
    private final List<String> array;

    public FilterComboBox(String[] items) {
        super(items);
        this.array = List.of(items);
        this.setEditable(true);
        final JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
        textfield.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
                SwingUtilities.invokeLater(() -> comboFilter(textfield.getText()));
            }
        });

    }

    public void comboFilter(String enteredText) {
        System.out.println(enteredText);
        if (!this.isPopupVisible()) {
            this.showPopup();
        }
        List<String> filterArray= new ArrayList<>();
        for (String value : array) {
            if (value.toLowerCase().contains(enteredText.toLowerCase())) {
                filterArray.add(value);
            }
        }
        if (filterArray.size() > 0) {
            System.out.println(filterArray);
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) this.getModel();
            model.removeAllElements();
            for (String s: filterArray) {
                model.addElement(s);
            }

//            JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
//            textfield.setText(enteredText);
        }

    }
}
