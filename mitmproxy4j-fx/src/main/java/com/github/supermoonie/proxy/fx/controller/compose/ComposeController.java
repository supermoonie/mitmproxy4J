package com.github.supermoonie.proxy.fx.controller.compose;

import com.github.supermoonie.proxy.fx.controller.KeyValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
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
        removeSelectedRow(paramTableView);
    }

    @FXML
    public void onHeaderAddButtonClicked() {
        headerTableView.getSelectionModel().clearSelection();
        headerTableView.getItems().add(new KeyValue("", ""));
        int rowIndex = headerTableView.getItems().size() - 1;
        headerTableView.getSelectionModel().select(rowIndex);
        headerTableView.edit(rowIndex, headerNameTableColumn);
    }

    @FXML
    public void onHeaderDelButtonClicked() {
        removeSelectedRow(headerTableView);
    }

    @FXML
    public void onCancelButtonClicked() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void removeSelectedRow(TableView<?> tableView) {
        ObservableList<?> selectedItems = tableView.getSelectionModel().getSelectedItems();
        if (null != selectedItems && selectedItems.size() > 0) {
            tableView.getItems().removeAll();
        }
    }
}
