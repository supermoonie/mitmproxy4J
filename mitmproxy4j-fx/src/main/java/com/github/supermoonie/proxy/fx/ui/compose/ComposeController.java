package com.github.supermoonie.proxy.fx.ui.compose;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.ui.KeyValue;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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
    public void onFormDataAddButtonClicked() {
        Stage formDataAddStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/FormDataAddDialog.fxml"));
            Parent parent = fxmlLoader.load();
            FormDataAddDialog dialog = fxmlLoader.getController();
            formDataAddStage.setScene(new Scene(parent));
            App.setCommonIcon(formDataAddStage, "FormData");
            formDataAddStage.initModality(Modality.APPLICATION_MODAL);
            formDataAddStage.showAndWait();
            if (null != dialog.getFormData()) {
                formDataTableView.getItems().add(dialog.getFormData());
            }
        } catch (IOException e) {
            AlertUtil.error(e);
        }
    }

    @FXML
    public void onFormDataDelButtonClicked() {
        removeSelectedRow(formDataTableView);
    }

    @FXML
    public void onCancelButtonClicked() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void removeSelectedRow(TableView<?> tableView) {
        ObservableList<?> selectedItems = tableView.getSelectionModel().getSelectedItems();
        if (null != selectedItems && selectedItems.size() > 0) {
            tableView.getItems().removeAll(selectedItems);
        }
    }
}
