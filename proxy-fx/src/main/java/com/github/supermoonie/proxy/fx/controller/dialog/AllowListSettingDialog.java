package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.support.AllowUrl;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @date 2020-11-08
 */
public class AllowListSettingDialog implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(AllowListSettingDialog.class);

    public CheckBox enableCheckBox;
    public TableView<AllowUrl> settingTableView;
    public TableColumn<AllowUrl, Boolean> enableColumn;
    public TableColumn<AllowUrl, String> urlRegexColumn;
    public Button addButton;
    public Button removeButton;
    public Button confirmButton;
    public Button cancelButton;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        settingTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        enableColumn.setCellValueFactory(new PropertyValueFactory<>("enable"));
        urlRegexColumn.setCellValueFactory(new PropertyValueFactory<>("urlRegex"));
        enableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enableColumn));
        urlRegexColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        GlobalSetting setting = GlobalSetting.getInstance();
        enableCheckBox.setSelected(setting.isAllowUrl());
        settingTableView.setDisable(!setting.isAllowUrl());
        addButton.setDisable(!setting.isAllowUrl());
        removeButton.setDisable(!setting.isAllowUrl());
        enableCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            settingTableView.setDisable(!newValue);
            addButton.setDisable(!newValue);
            removeButton.setDisable(!newValue);
        });
        settingTableView.getItems().addAll(setting.getAllowUrlList());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(windowEvent -> onCancelButtonClicked());
    }

    public void onAddButtonClicked() {
        settingTableView.getItems().add(new AllowUrl());
    }

    public void onRemoveButtonClicked() {
        ObservableList<AllowUrl> selectedItems = settingTableView.getSelectionModel().getSelectedItems();
        settingTableView.getItems().removeAll(selectedItems);
    }

    public void onConfirmButtonClicked() {
        GlobalSetting setting = GlobalSetting.getInstance();
        boolean enable = enableCheckBox.isSelected();
        setting.setAllowUrl(enable);
        ObservableSet<AllowUrl> allowUrls = FXCollections.observableSet(new HashSet<>());
        allowUrls.addAll(settingTableView.getItems());
        setting.setAllowUrlList(allowUrls);
        stage.setUserData(enable);
        stage.close();
    }

    public void onCancelButtonClicked() {
        stage.setUserData(GlobalSetting.getInstance().isAllowUrl());
        stage.close();
    }

    public static Object showAndWait() {
        Stage allowListSettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(AllowListSettingDialog.class.getResource("/ui/dialog/AllowListSettingDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            AllowListSettingDialog allowListSettingDialog = fxmlLoader.getController();
            allowListSettingDialog.setStage(allowListSettingStage);
            allowListSettingStage.setScene(new Scene(parent));
            App.setCommonIcon(allowListSettingStage, "Allow List Setting");
            allowListSettingStage.initModality(Modality.APPLICATION_MODAL);
            allowListSettingStage.setResizable(false);
            allowListSettingStage.initStyle(StageStyle.UTILITY);
            allowListSettingStage.showAndWait();
            return allowListSettingStage.getUserData();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
            return null;
        }
    }
}
