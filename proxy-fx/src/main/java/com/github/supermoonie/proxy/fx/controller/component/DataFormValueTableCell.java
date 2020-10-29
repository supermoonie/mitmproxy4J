package com.github.supermoonie.proxy.fx.controller.component;

import com.github.supermoonie.proxy.fx.dto.FormDataColumnMap;
import com.github.supermoonie.proxy.fx.util.JSON;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * @author supermoonie
 * @since 2020/10/26
 */
public class DataFormValueTableCell extends TableCell<FormDataColumnMap, String> {

    private TextField textField;

    private HBox fileSelectBox;

    private Button selectButton;

    private Label fileNameLabel;

    private final TableColumn<FormDataColumnMap, String> column;

    public DataFormValueTableCell(TableColumn<FormDataColumnMap, String> column) {
        this.column = column;
    }

    @Override
    public void startEdit() {
        super.startEdit();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
    }



    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (isEmpty() || null == item) {
            setText(null);
            setGraphic(null);
        } else {
//            setText(item);
            FormDataColumnMap map = this.getTableRow().getItem();
            System.out.println("item: " + item + ", map: " + JSON.toJsonString(map));
            if ("Text".equals(map.getValueType())) {
                if (null == textField) {
                    textField = new TextField();
                }
                textField.setText(item);
                setGraphic(textField);
            } else {
                if (null == selectButton) {
                    fileSelectBox = new HBox();
                    selectButton = new Button("Select");
                    fileNameLabel = new Label(map.getValue());
                    fileSelectBox.getChildren().addAll(selectButton, fileNameLabel);
                }
                setGraphic(fileSelectBox);
            }
        }
    }
}
