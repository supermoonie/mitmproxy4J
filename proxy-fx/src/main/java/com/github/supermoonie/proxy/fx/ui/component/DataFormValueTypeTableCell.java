package com.github.supermoonie.proxy.fx.ui.component;

import com.github.supermoonie.proxy.fx.dto.FormDataColumnMap;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import java.util.List;

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
