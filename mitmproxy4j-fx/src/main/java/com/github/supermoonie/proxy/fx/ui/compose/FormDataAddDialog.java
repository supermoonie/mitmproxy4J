package com.github.supermoonie.proxy.fx.ui.compose;

import com.github.supermoonie.proxy.mime.MimeMappings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.SearchableComboBox;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @date 2021-03-21
 */
public class FormDataAddDialog implements Initializable {

    public static final String TEXT = "Text";
    public static final String FILE = "File";
    public static final String DEFAULT_CONTENT_TYPE = "Auto";

    private static List<String> mimeTypeList;

    static {
        mimeTypeList = MimeMappings.DEFAULT.getAll().stream()
                .map(MimeMappings.Mapping::getMimeType)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        mimeTypeList.add(0, DEFAULT_CONTENT_TYPE);
    }

    @FXML
    protected TextField nameTextField;
    @FXML
    protected ComboBox<String> valueTypeComboBox;
    @FXML
    protected HBox valueBox;
    @FXML
    protected TextField valueTextField;
    protected Button selectButton = new Button("Select");
    protected Label fileLabel = new Label();
    @FXML
    protected SearchableComboBox<String> contentTypeComboBox;
    @FXML
    protected Button okButton;
    @FXML
    protected Button cancelButton;

    private FormData formData;

    private final EventHandler<ActionEvent> selectButtonClickedHandler = event -> {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(okButton.getScene().getWindow());
        if (null != file) {
            fileLabel.setText(file.getName());
            fileLabel.setUserData(file.getAbsolutePath());
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        valueTypeComboBox.getItems().addAll(TEXT, FILE);
        valueTypeComboBox.setValue(TEXT);
        valueTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (FILE.equals(newValue)) {
                valueBox.getChildren().clear();
                valueBox.getChildren().add(valueTypeComboBox);
                valueBox.getChildren().add(selectButton);
                valueBox.getChildren().add(fileLabel);
            } else {
                valueBox.getChildren().clear();
                valueBox.getChildren().add(valueTypeComboBox);
                valueBox.getChildren().add(valueTextField);
            }
        });
        selectButton.setOnAction(selectButtonClickedHandler);
        contentTypeComboBox.setValue(DEFAULT_CONTENT_TYPE);
        Platform.runLater(() -> contentTypeComboBox.getItems().addAll(mimeTypeList));
    }

    @FXML
    public void onOkButtonClicked() {
        String name = Objects.requireNonNullElse(nameTextField.getText(), "");
        String valueType = valueTypeComboBox.getSelectionModel().getSelectedItem();
        String value;
        if (TEXT.equals(valueType)) {
            value = Objects.requireNonNullElse(valueTextField.getText(), "");
        } else {
            value = Objects.requireNonNullElse(fileLabel.getUserData(), "").toString();
        }
        String contentType = contentTypeComboBox.getSelectionModel().getSelectedItem();
        if (FILE.equals(valueType) && DEFAULT_CONTENT_TYPE.equals(contentType) && StringUtils.isNotEmpty(value)) {
            contentType = Objects.requireNonNullElse(MimeMappings.DEFAULT.get(value.substring(value.lastIndexOf(".") + 1)), "plain/text");
        }
        formData = new FormData(name, valueType, value, contentType);
        onCancelButtonClicked();
    }

    @FXML
    public void onCancelButtonClicked() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setFormData(FormData formData) {
        nameTextField.setText(formData.getName());
        valueTypeComboBox.setValue(formData.getType());
        if (formData.getType().equals(TEXT)) {
            valueTextField.setText(formData.getValue());
        } else {
            fileLabel.setUserData(formData.getValue());
            fileLabel.setText(Paths.get(formData.getValue()).getFileName().toString());
        }
        contentTypeComboBox.setValue(formData.getContentType());
    }

    public FormData getFormData() {
        return formData;
    }
}
