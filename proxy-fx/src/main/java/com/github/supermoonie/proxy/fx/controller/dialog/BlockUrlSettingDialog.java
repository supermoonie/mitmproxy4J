package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.support.BlockUrl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/5
 */
public class BlockUrlSettingDialog implements Initializable {

    private Stage stage;

    @FXML
    protected CheckBox enableCheckBox;

    @FXML
    protected TableView<BlockUrl> settingTableView;

    @FXML
    protected TableColumn<BlockUrl, Boolean> enableColumn;

    @FXML
    protected TableColumn<BlockUrl, String> urlRegexColumn;

    @FXML
    protected Button addButton;

    @FXML
    protected Button removeButton;

    @FXML
    protected Button confirmButton;

    @FXML
    protected Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        enableColumn.setCellValueFactory(new PropertyValueFactory<>("enable"));
        urlRegexColumn.setCellValueFactory(new PropertyValueFactory<>("urlRegex"));
        enableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enableColumn));
        urlRegexColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        GlobalSetting setting = GlobalSetting.getInstance();
        enableCheckBox.setSelected(setting.isBlockUrl());
        settingTableView.setDisable(!setting.isBlockUrl());
        addButton.setDisable(!setting.isBlockUrl());
        removeButton.setDisable(!setting.isBlockUrl());
        enableCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            settingTableView.setDisable(!newValue);
            addButton.setDisable(!newValue);
            removeButton.setDisable(!newValue);
        });
        settingTableView.getItems().addAll(setting.getBlockUrlList());
    }

    public void onCancelButtonClicked() {
        stage.setUserData(GlobalSetting.getInstance().isBlockUrl());
        stage.close();
    }

    public void onConfirmButtonClicked() {
        GlobalSetting setting = GlobalSetting.getInstance();
        boolean enable = enableCheckBox.isSelected();
        setting.setBlockUrl(enable);
        setting.setBlockUrlList(settingTableView.getItems());
        stage.setUserData(enable);
        stage.close();
    }

    public void onRemoveButtonClicked() {
        ObservableList<BlockUrl> selectedItems = settingTableView.getSelectionModel().getSelectedItems();
        settingTableView.getItems().removeAll(selectedItems);
    }

    public void onAddButtonClicked() {
        BlockUrl blockUrl = new BlockUrl();
        settingTableView.getItems().add(blockUrl);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
