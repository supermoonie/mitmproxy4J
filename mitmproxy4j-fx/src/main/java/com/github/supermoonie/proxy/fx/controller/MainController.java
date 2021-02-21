package com.github.supermoonie.proxy.fx.controller;

import com.github.supermoonie.proxy.fx.ColumnMap;
import com.github.supermoonie.proxy.fx.FlowNode;
import com.github.supermoonie.proxy.fx.PropertyPair;
import com.github.supermoonie.proxy.fx.entity.Header;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @date 2021-02-21
 */
public class MainController implements Initializable {

    @FXML
    protected MenuBar menuBar;
    @FXML
    protected CheckMenuItem recordMenuItem;
    @FXML
    protected CheckMenuItem throttlingMenuItem;
    @FXML
    protected CheckMenuItem systemProxyMenuItem;
    @FXML
    protected CheckMenuItem blockListMenuItem;
    @FXML
    protected CheckMenuItem allowListMenuItem;
    @FXML
    protected MenuItem jsonViewerMenuItem;
    @FXML
    protected TabPane tabPane;
    @FXML
    protected Tab structureTab;
    @FXML
    protected Tab sequenceTab;
    @FXML
    protected TreeView<FlowNode> treeView;
    @FXML
    protected ListView<FlowNode> listView;
    @FXML
    protected Label infoLabel;
    @FXML
    protected Tab overviewTab;
    @FXML
    protected Tab contentsTab;
    @FXML
    protected TreeTableView<PropertyPair> overviewTreeTableView;
    @FXML
    protected TreeTableColumn<PropertyPair, String> overviewNameColumn;
    @FXML
    protected TreeTableColumn<PropertyPair, String> overviewValueColumn;
    @FXML
    protected TabPane requestTabPane;
    @FXML
    protected TabPane responseTabPane;
    @FXML
    protected Tab requestHeaderTab;
    @FXML
    protected Tab requestFormTab;
    @FXML
    protected Tab requestQueryTab;
    @FXML
    protected Tab requestRawTab;
    @FXML
    protected Tab responseHeaderTab;
    @FXML
    protected Tab responseRawTab;
    @FXML
    protected Tab responseContentTab;
    @FXML
    protected TableView<Header> requestHeaderTableView;
    @FXML
    protected TableColumn<Header, String> requestHeaderNameColumn;
    @FXML
    protected TableColumn<Header, String> requestHeaderValueColumn;
    @FXML
    protected TableColumn<ColumnMap, String> requestFormNameColumn;
    @FXML
    protected TableColumn<ColumnMap, String> requestFormValueColumn;
    @FXML
    protected TableColumn<ColumnMap, String> requestQueryNameColumn;
    @FXML
    protected TableColumn<ColumnMap, String> requestQueryValueColumn;
    @FXML
    protected TableView<Header> responseHeaderTableView;
    @FXML
    protected TableColumn<Header, String> responseHeaderNameColumn;
    @FXML
    protected TableColumn<Header, String> responseHeaderValueColumn;
    @FXML
    protected TextArea requestRawTextArea;
    @FXML
    protected TextArea responseRawTextArea;
    @FXML
    protected TableView<ColumnMap> formTableView;
    @FXML
    protected TableView<ColumnMap> queryTableView;
    @FXML
    protected WebView responseJsonWebView;
    @FXML
    protected Tab responseTextTab;
    @FXML
    protected TextArea responseTextArea;
    @FXML
    protected Tab responseImageTab;
    @FXML
    protected ImageView responseImageView;
    @FXML
    protected TextField filterTextField;
    @FXML
    protected Button clearButton;
    @FXML
    protected Button editButton;
    @FXML
    protected Button repeatButton;
    @FXML
    protected Button throttlingSwitchButton;
    @FXML
    protected Button recordingSwitchButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onJsonViewerMenuItemClicked() {

    }

    public void onAllowListMenuItemClicked() {

    }

    public void onBlockListMenuItemClicked() {

    }

    public void onThrottlingSettingMenuItemClicked() {

    }

    public void onProxySettingMenuItemClicked() {

    }

    public void onSystemProxyMenuItemClicked() {

    }

    public void onThrottlingMenuItemClicked() {

    }

    public void onThrottlingSwitchButtonClicked() {

    }

    public void onOpenMenuItemClicked() {

    }

    public void onSaveMenuClicked() {

    }

    public void onRecordMenuItemClicked() {

    }

    public void onRecordSwitchButtonClicked() {

    }

    public void onRepeatButtonClicked() {

    }

    public void onSendRequestMenuItemClicked() {

    }

    public void onEditButtonClicked() {

    }

    public void onClearButtonClicked() {

    }

    public void onFilterTextFieldEnter(KeyEvent keyEvent) {

    }
}
