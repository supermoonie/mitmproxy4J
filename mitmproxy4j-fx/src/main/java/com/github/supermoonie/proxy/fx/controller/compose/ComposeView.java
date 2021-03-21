package com.github.supermoonie.proxy.fx.controller.compose;

import com.github.supermoonie.proxy.fx.component.TextFieldCell;
import com.github.supermoonie.proxy.fx.constant.HttpMethod;
import com.github.supermoonie.proxy.fx.controller.KeyValue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2021/3/19
 */
public class ComposeView implements Initializable {

    @FXML
    protected ChoiceBox<String> reqMethodChoiceBox;
    @FXML
    protected TextField urlTextField;
    /**
     * param
     */
    @FXML
    protected Button paramAddButton;
    @FXML
    protected Button paramDelButton;
    @FXML
    protected TableView<KeyValue> paramTableView;
    @FXML
    protected TableColumn<KeyValue, String> paramNameTableColumn;
    @FXML
    protected TableColumn<KeyValue, String> paramValueTableColumn;

    @FXML
    protected Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reqMethodChoiceBox.getItems().addAll(HttpMethod.ALL_METHOD);
        reqMethodChoiceBox.setValue(HttpMethod.GET);
        paramTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        paramNameTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        paramValueTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        paramDelButton.disableProperty().bind(paramTableView.getSelectionModel().selectedIndexProperty().lessThan(0));
        Platform.runLater(() -> urlTextField.requestFocus());
    }
}
