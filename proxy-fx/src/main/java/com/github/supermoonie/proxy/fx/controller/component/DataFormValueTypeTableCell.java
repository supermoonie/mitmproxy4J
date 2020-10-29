package com.github.supermoonie.proxy.fx.controller.component;

import com.github.supermoonie.proxy.fx.dto.FormDataColumnMap;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author supermoonie
 * @since 2020/10/21
 */
public class DataFormValueTypeTableCell extends TableCell<FormDataColumnMap, String> {

    private ComboBox<String> comboBox;

    public DataFormValueTypeTableCell() {
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (isEmpty() || null == item) {
            setText(null);
            setGraphic(null);
        } else {
            if (null == comboBox) {
                comboBox = new ComboBox<>(FXCollections.observableList(List.of("Text", "File")));
//                Bindings.createStringBinding(() -> null, comboBox.valueProperty());
//                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
//                    FormDataColumnMap map = this.getTableRow().getItem();
//                    map.setValue(newValue);
//                });
            }
            FormDataColumnMap map = this.getTableRow().getItem();
            comboBox.setValue(map.getValueType());
            setGraphic(comboBox);
        }
    }
}
