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
    protected ComboBox<String> reqMethodComboBox;
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
    /**
     * header
     */
    @FXML
    protected TableView<KeyValue> headerTableView;
    @FXML
    protected TableColumn<KeyValue, String> headerNameTableColumn;
    @FXML
    protected TableColumn<KeyValue, String> headerValueTableColumn;
    @FXML
    protected Button headerAddButton;
    @FXML
    protected Button headerDelButton;
    /**
     * body
     */
    protected final ToggleGroup toggleGroup = new ToggleGroup();
    @FXML
    protected RadioButton noneRadioButton;
    @FXML
    protected RadioButton formDataRadioButton;
    @FXML
    protected RadioButton formUrlencodedRadioButton;
    @FXML
    protected RadioButton binaryRadioButton;
    @FXML
    protected RadioButton rawRadioButton;
    @FXML
    protected ComboBox<String> contentTypeComboBox;
    @FXML
    protected TabPane bodyContentTabPane;
    @FXML
    protected Tab noneTab;
    @FXML
    protected Tab formDataTab;
    @FXML
    protected Tab formUrlencodedTab;
    @FXML
    protected Tab binaryTab;
    @FXML
    protected Tab rawTab;
    /**
     * body form-data tab
     */
    @FXML
    protected MenuButton formDataAddMenuButton;

    @FXML
    protected Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reqMethodComboBox.getItems().addAll(HttpMethod.ALL_METHOD);
        // param
        paramTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        paramNameTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        paramValueTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        paramDelButton.disableProperty().bind(paramTableView.getSelectionModel().selectedIndexProperty().lessThan(0));
        // header
        headerTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        headerNameTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        headerValueTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        headerDelButton.disableProperty().bind(headerTableView.getSelectionModel().selectedIndexProperty().lessThan(0));
        // body
        toggleGroup.getToggles().add(noneRadioButton);
        toggleGroup.getToggles().add(formDataRadioButton);
        toggleGroup.getToggles().add(formUrlencodedRadioButton);
        toggleGroup.getToggles().add(binaryRadioButton);
        toggleGroup.getToggles().add(rawRadioButton);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> switchBodyContentTab());
        contentTypeComboBox.getItems().addAll("JSON", "XML", "Text", "JavaScript", "HTML");
        // body form-data
        Platform.runLater(() -> urlTextField.requestFocus());
    }

    private void switchBodyContentTab() {
        if (formDataRadioButton.isSelected()) {
            bodyContentTabPane.getSelectionModel().select(formDataTab);
        } else if (formUrlencodedRadioButton.isSelected()) {
            bodyContentTabPane.getSelectionModel().select(formUrlencodedTab);
        } else if (binaryRadioButton.isSelected()) {
            bodyContentTabPane.getSelectionModel().select(binaryTab);
        } else if (rawRadioButton.isSelected()) {
            bodyContentTabPane.getSelectionModel().select(rawTab);
        } else {
            bodyContentTabPane.getSelectionModel().select(noneTab);
        }
    }
}
