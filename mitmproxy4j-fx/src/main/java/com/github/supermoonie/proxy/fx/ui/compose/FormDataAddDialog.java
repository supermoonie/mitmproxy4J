package com.github.supermoonie.proxy.fx.ui.compose;

import com.github.supermoonie.proxy.mime.MimeMappings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.SearchableComboBox;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @date 2021-03-21
 */
public class FormDataAddDialog implements Initializable {

    @FXML
    protected TextField nameTextField;
    @FXML
    protected ComboBox<String> valueTypeComboBox;
    @FXML
    protected TextField valueTextField;
    @FXML
    protected SearchableComboBox<String> contentTypeComboBox;

    @FXML
    protected Button okButton;
    @FXML
    protected Button cancelButton;

    private final EventHandler<MouseEvent> fileSelectHandler = event -> {
        if ("Text".equals(valueTypeComboBox.getSelectionModel().getSelectedItem())) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(okButton.getScene().getWindow());
        if (null != file) {
            valueTextField.setText(file.getName());
            valueTextField.setUserData(file.getAbsolutePath());
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        valueTypeComboBox.getItems().addAll("Text", "File");
        valueTypeComboBox.setValue("Text");
        valueTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            valueTextField.clear();
        });
        valueTextField.setOnMouseClicked(fileSelectHandler);
        List<String> mimeTypeList = MimeMappings.DEFAULT.getAll().stream().map(MimeMappings.Mapping::getMimeType).distinct().sorted().collect(Collectors.toList());
        mimeTypeList.add(0, "Auto");
        contentTypeComboBox.getItems().addAll(mimeTypeList);
        contentTypeComboBox.setValue("Auto");
    }

    @FXML
    public void onOkButtonClicked() {
        String name = Objects.requireNonNullElse(nameTextField.getText(), "");
        String valueType = valueTypeComboBox.getSelectionModel().getSelectedItem();
        String value;
        if ("Text".equals(valueType)) {
            value = Objects.requireNonNullElse(valueTextField.getText(), "");
        } else {
            value = Objects.requireNonNullElse(valueTextField.getUserData(), "").toString();
        }
        String contentType = contentTypeComboBox.getSelectionModel().getSelectedItem();
        if ("File".equals(valueType) && "Auto".equals(contentType) && StringUtils.isNotEmpty(value)) {
            contentType = MimeMappings.DEFAULT.get(value.substring(value.lastIndexOf(".")));
            System.out.println(value.substring(value.lastIndexOf(".")));
        }
    }
}
