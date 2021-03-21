package com.github.supermoonie.proxy.fx.controller.compose;

import com.github.supermoonie.proxy.fx.controller.KeyValue;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * @author supermoonie
 * @since 2021/3/19
 */
public class ComposeController extends ComposeView {

    @FXML
    public void onParamAddButtonClicked() {
        paramTableView.getSelectionModel().clearSelection();
        paramTableView.getItems().add(new KeyValue("", ""));
        int rowIndex = paramTableView.getItems().size() - 1;
        paramTableView.getSelectionModel().select(rowIndex);
        paramTableView.edit(rowIndex, paramNameTableColumn);
    }

    @FXML
    public void onParamDelButtonClicked() {
        paramTableView.getItems().removeAll(paramTableView.getSelectionModel().getSelectedItems());
    }

    @FXML
    public void onCancelButtonClicked() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
