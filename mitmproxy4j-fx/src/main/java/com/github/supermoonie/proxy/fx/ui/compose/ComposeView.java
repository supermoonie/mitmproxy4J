package com.github.supermoonie.proxy.fx.ui.compose;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.component.TextFieldCell;
import com.github.supermoonie.proxy.fx.constant.HttpMethod;
import com.github.supermoonie.proxy.fx.constant.RequestRawType;
import com.github.supermoonie.proxy.fx.ui.KeyValue;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.bouncycastle.util.encoders.Hex;

import java.net.URL;
import java.nio.charset.StandardCharsets;
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
     * body form-data
     */
    @FXML
    protected Button formDataAddButton;
    @FXML
    protected Button formDataDelButton;
    @FXML
    protected Button formDataEditButton;
    @FXML
    protected TableView<FormData> formDataTableView;
    @FXML
    protected TableColumn<FormData, String> formDataNameTableColumn;
    @FXML
    protected TableColumn<FormData, String> formDataValueTableColumn;
    @FXML
    protected TableColumn<FormData, String> formDataContentTypeTableColumn;
    @FXML
    protected TableColumn<FormData, String> formDataValueTypeTableColumn;
    /**
     * body form-urlencoded
     */
    @FXML
    protected TableView<KeyValue> formUrlencodedTableView;
    @FXML
    protected TableColumn<KeyValue, String> formUrlencodedNameTableColumn;
    @FXML
    protected TableColumn<KeyValue, String> formUrlencodedValueTableColumn;
    @FXML
    protected Button formUrlencodedAddButton;
    @FXML
    protected Button formUrlencodedDelButton;
    /**
     * body binary
     */
    @FXML
    protected Label binaryFileLabel;
    /**
     * body raw
     */
    @FXML
    protected WebView rawWebView;

    @FXML
    protected Button sendButton;
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
        contentTypeComboBox.getItems().addAll(RequestRawType.RAW_TYPE_LIST);
        contentTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (RequestRawType.JSON.equals(newValue)) {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/json');");
            } else if (RequestRawType.XML.equals(newValue) || RequestRawType.HTML.equals(newValue)) {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/html');");
            } else if (RequestRawType.JAVASCRIPT.equals(newValue)) {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/javascript');");
            } else {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/text');");
            }
        });
        // body form-data
        formDataDelButton.disableProperty().bind(formDataTableView.getSelectionModel().selectedIndexProperty().lessThan(0));
        formDataEditButton.disableProperty().bind(formDataTableView.getSelectionModel().selectedIndexProperty().lessThan(0));
        // body form-urlencoded
        formUrlencodedNameTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        formUrlencodedValueTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        formUrlencodedDelButton.disableProperty().bind(formUrlencodedTableView.getSelectionModel().selectedIndexProperty().lessThan(0));
        // body raw
        rawWebView.setContextMenuEnabled(false);
        rawWebView.getEngine().load(App.class.getResource("/static/RichText.html").toExternalForm());
        rawWebView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Worker.State.SUCCEEDED)) {
                System.out.println("codeEditor.setReadOnly(false);");
                rawWebView.getEngine().executeScript("hideLoading();codeEditor.setReadOnly(false);");
            }
        });
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
