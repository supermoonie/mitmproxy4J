package com.github.supermoonie.proxy.fx.ui.dialog;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.support.BlockUrl;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
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
 * @since 2020/11/5
 */
public class BlockUrlSettingDialog implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(BlockUrlSettingDialog.class);

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

    private Stage stage;

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
        ObservableSet<BlockUrl> blockUrls = FXCollections.observableSet(new HashSet<>());
        blockUrls.addAll(settingTableView.getItems());
        setting.setBlockUrlList(blockUrls);
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
        this.stage.setOnCloseRequest(windowEvent -> onCancelButtonClicked());
    }

    public static Object showAndWait() {
        Stage blockUrlSettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BlockUrlSettingDialog.class.getResource("/ui/dialog/BlockUrlSettingDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            BlockUrlSettingDialog blockUrlSettingDialog = fxmlLoader.getController();
            blockUrlSettingDialog.setStage(blockUrlSettingStage);
            blockUrlSettingStage.setScene(new Scene(parent));
            App.setCommonIcon(blockUrlSettingStage, "Block List Setting");
            blockUrlSettingStage.setResizable(false);
            blockUrlSettingStage.initModality(Modality.APPLICATION_MODAL);
            blockUrlSettingStage.initStyle(StageStyle.UTILITY);
            blockUrlSettingStage.showAndWait();
            return blockUrlSettingStage.getUserData();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
            return null;
        }
    }
}
