package com.github.supermoonie.proxy.fx.controller.main;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.AppPreferences;
import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.constant.KeyEvents;
import com.github.supermoonie.proxy.fx.controller.SendReqController;
import com.github.supermoonie.proxy.fx.controller.dialog.*;
import com.github.supermoonie.proxy.fx.controller.main.factory.ListViewCellFactory;
import com.github.supermoonie.proxy.fx.controller.main.handler.*;
import com.github.supermoonie.proxy.fx.controller.main.listener.TreeViewFocusListener;
import com.github.supermoonie.proxy.fx.controller.main.listener.TreeViewSelectListener;
import com.github.supermoonie.proxy.fx.dto.ColumnMap;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.entity.*;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.source.Icons;
import com.github.supermoonie.proxy.fx.support.Flow;
import com.github.supermoonie.proxy.fx.support.HexContentFlow;
import com.github.supermoonie.proxy.fx.support.PropertyPair;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ClipboardUtil;
import com.github.supermoonie.proxy.fx.util.HttpClientUtil;
import com.github.supermoonie.proxy.fx.util.JacksonUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.util.encoders.Hex;
import org.controlsfx.glyphfont.FontAwesome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/9/25
 */
public class MainController implements Initializable {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

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

    private final TreeItem<PropertyPair> overviewRoot = new TreeItem<>(new PropertyPair());

    private final ContextMenu treeContextMenu = new ContextMenu();
    private final MenuItem copyMenuItem = new MenuItem("Copy URL");
    private final MenuItem copyResponseMenuItem = new MenuItem("Copy Response");
    private final MenuItem saveResponseMenuItem = new MenuItem("Save Response");
    private final MenuItem repeatMenuItem = new MenuItem("Repeat");
    private final MenuItem editMenuItem = new MenuItem("Edit");
    private final MenuItem blockMenuItem = new MenuItem("Block List");
    private final MenuItem allowMenuItem = new MenuItem("Allow List");

    private final ContextMenu listContextMenu = new ContextMenu();
    private final MenuItem copyLiMenuItem = new MenuItem("Copy URL");
    private final MenuItem copyLiResponseMenuItem = new MenuItem("Copy Response");
    private final MenuItem saveLiResponseMenuItem = new MenuItem("Save Response");
    private final MenuItem repeatLiMenuItem = new MenuItem("Repeat");
    private final MenuItem editLiMenuItem = new MenuItem("Edit");
    private final MenuItem blockLiMenuItem = new MenuItem("Block List");
    private final MenuItem allowLiMenuItem = new MenuItem("Allow List");

    private final ContextMenu overviewContextMenu = new ContextMenu();
    private final MenuItem ovCopyValueMenuItem = new MenuItem("Copy Value");
    private final MenuItem ovCopyRowMenuItem = new MenuItem("Copy Row");

    private final ObservableList<FlowNode> allNode = FXCollections.observableList(new LinkedList<>());
    private final TreeItem<FlowNode> root = new TreeItem<>(new FlowNode());
    private String currentRequestId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        blockListMenuItem.setSelected(AppPreferences.getState().getBoolean(AppPreferences.KEY_BLOCK_LIST_ENABLE, AppPreferences.DEFAULT_BLOCK_LIST_ENABLE));
        allowListMenuItem.setSelected(AppPreferences.getState().getBoolean(AppPreferences.KEY_ALLOW_LIST_ENABLE, AppPreferences.DEFAULT_ALLOW_LIST_ENABLE));
        initToolBar();
        initRecordSetting();
        initThrottlingSetting();
        initContextMenu();
        initTreeView();
        initListView();
        initWebview(responseJsonWebView);
        initOverview();
        clear();
    }


    private void initOverview() {
        overviewNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getKey()));
        overviewValueColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getValue()));
        overviewRoot.setExpanded(true);
        overviewTreeTableView.setRoot(overviewRoot);
        overviewTreeTableView.setShowRoot(false);
        overviewTreeTableView.setOnMouseClicked(new OverviewTreeTableViewMouseEventHandler(overviewTreeTableView));
        overviewTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        overviewContextMenu.getItems().addAll(ovCopyValueMenuItem, ovCopyRowMenuItem);
        overviewTreeTableView.setContextMenu(overviewContextMenu);
    }

    private void initListView() {
        listView.setCellFactory(new ListViewCellFactory());
        listView.setContextMenu(listContextMenu);
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            FlowNode selectedItem = listView.getSelectionModel().getSelectedItem();
            if (null != selectedItem) {
                fillContentsTab(selectedItem);
            }
        });
        listView.setOnKeyPressed(new FlowNodeKeyEventHandler(tabPane, structureTab, treeView, listView));
    }

    private void initTreeView() {
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.setContextMenu(treeContextMenu);
        treeView.contextMenuProperty().bind(Bindings.when(treeView.getSelectionModel().selectedItemProperty().isNull()).then((ContextMenu) null).otherwise(treeContextMenu));
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new TreeViewMouseEventHandler(treeView, selectedNode -> {
            fillContentsTab(selectedNode);
            fillOverviewTab(selectedNode);
        }));
        treeView.setOnKeyPressed(new FlowNodeKeyEventHandler(tabPane, structureTab, treeView, listView));
        treeView.focusedProperty().addListener(new TreeViewFocusListener(treeView));
        treeView.getSelectionModel().selectedItemProperty().addListener(new TreeViewSelectListener());
    }

    private void initContextMenu() {
        setMenuItemOnAction(copyMenuItem, copyResponseMenuItem, saveResponseMenuItem, repeatMenuItem, editMenuItem, blockMenuItem, allowMenuItem);
        setMenuItemOnAction(copyLiMenuItem, copyLiResponseMenuItem, saveLiResponseMenuItem, repeatLiMenuItem, editLiMenuItem, blockLiMenuItem, allowLiMenuItem);
        listContextMenu.getItems().addAll(copyLiMenuItem, copyLiResponseMenuItem, saveLiResponseMenuItem, new SeparatorMenuItem(), repeatLiMenuItem, editLiMenuItem, new SeparatorMenuItem(), blockLiMenuItem, allowLiMenuItem);
        treeContextMenu.getItems().addAll(copyMenuItem, copyResponseMenuItem, saveResponseMenuItem, new SeparatorMenuItem(), repeatMenuItem, editMenuItem, new SeparatorMenuItem(), blockMenuItem, allowMenuItem);
        treeContextMenu.setOnShowing(event -> {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            FlowNode node = selectedItem.getValue();
            boolean disable = node.getStatus() == -1 || null == node.getContentType() || !node.getType().equals(EnumFlowType.TARGET);
            copyResponseMenuItem.setDisable(disable);
            saveResponseMenuItem.setDisable(disable);
        });
        listContextMenu.setOnShowing(event -> {
            FlowNode node = listView.getSelectionModel().getSelectedItem();
            if (null == node) {
                return;
            }
            boolean disable = node.getStatus() == -1 || null == node.getContentType() || !node.getType().equals(EnumFlowType.TARGET);
            copyLiResponseMenuItem.setDisable(disable);
            saveLiResponseMenuItem.setDisable(disable);
        });
    }

    private void setMenuItemOnAction(MenuItem copyLiMenuItem, MenuItem copyLiResponseMenuItem, MenuItem saveLiResponseMenuItem, MenuItem repeatLiMenuItem, MenuItem editLiMenuItem, MenuItem blockLiMenuItem, MenuItem allowLiMenuItem) {
        copyLiMenuItem.setOnAction(new CopyMenuItemHandler(tabPane, structureTab, treeView, listView));
        copyLiResponseMenuItem.setOnAction(new CopyResponseMenuItemHandler(tabPane, structureTab, treeView, listView));
        saveLiResponseMenuItem.setOnAction(new SaveResponseMenuItemHandler(tabPane, structureTab, treeView, listView));
        repeatLiMenuItem.setOnAction(event -> onRepeatButtonClicked());
        editLiMenuItem.setOnAction(new EditMenuItemHandler(tabPane, structureTab, treeView, listView, node -> onSendRequestMenuItemClicked().setRequestId(node.getId())));
        blockLiMenuItem.setOnAction(new AddBlockListMenuItemHandler(tabPane, structureTab, treeView, listView));
        allowLiMenuItem.setOnAction(new AddAllowListMenuItemHandler(tabPane, structureTab, treeView, listView));
    }

    private void initWebview(WebView webView) {
        webView.setContextMenuEnabled(false);
        webView.getEngine().load(App.class.getResource("/static/RichText.html").toExternalForm());
        webView.setOnKeyPressed(keyEvent -> {
            if (KeyEvents.MAC_KEY_CODE_COMBINATION.match(keyEvent) || KeyEvents.WIN_KEY_CODE_COPY.match(keyEvent)) {
                WebEngine engine = webView.getEngine();
                String text = engine.executeScript("try{codeEditor.getSelectedText();}catch(e){''}").toString();
                ClipboardUtil.copyText(text);
            }
        });
    }

    private void initToolBar() {
        ImageView clearIconView = new ImageView(Icons.CLEAR_ICON);
        clearIconView.setFitHeight(12);
        clearIconView.setFitWidth(12);
        clearButton.setGraphic(clearIconView);
        editButton.setGraphic(Icons.FONT_AWESOME.create(FontAwesome.Glyph.EDIT));
        repeatButton.setGraphic(Icons.FONT_AWESOME.create(FontAwesome.Glyph.REPEAT));
    }

    private void initRecordSetting() {
        recordMenuItem.setSelected(true);
        ImageView imageView = new ImageView(Icons.GREEN_DOT_ICON);
        imageView.setFitWidth(12);
        imageView.setFitHeight(12);
        recordingSwitchButton.setGraphic(imageView);
        GlobalSetting.getInstance().recordProperty().addListener((observable, oldValue, newValue) -> {
            recordMenuItem.setSelected(newValue);
            imageView.setImage(newValue ? Icons.GREEN_DOT_ICON : Icons.GRAY_DOT_ICON);
            recordingSwitchButton.setGraphic(imageView);
        });
    }

    private void initThrottlingSetting() {
        throttlingMenuItem.setSelected(GlobalSetting.getInstance().isThrottling());
        ImageView imageView = new ImageView(GlobalSetting.getInstance().isThrottling() ? Icons.GREEN_DOT_ICON : Icons.GRAY_DOT_ICON);
        imageView.setFitWidth(12);
        imageView.setFitHeight(12);
        throttlingSwitchButton.setGraphic(imageView);
        GlobalSetting.getInstance().throttlingProperty().addListener((observable, oldValue, newValue) -> {
            throttlingMenuItem.setSelected(newValue);
            imageView.setImage(newValue ? Icons.GREEN_DOT_ICON : Icons.GRAY_DOT_ICON);
            throttlingSwitchButton.setGraphic(imageView);
            ProxyManager.getInternalProxy().setTrafficShaping(newValue);
            GlobalChannelTrafficShapingHandler handler = ProxyManager.getInternalProxy().getTrafficShapingHandler();
            handler.setReadLimit(GlobalSetting.getInstance().getThrottlingReadLimit());
            handler.setWriteLimit(GlobalSetting.getInstance().getThrottlingWriteLimit());
            handler.setReadChannelLimit(GlobalSetting.getInstance().getThrottlingReadLimit());
            handler.setWriteChannelLimit(GlobalSetting.getInstance().getThrottlingWriteLimit());
        });
        GlobalSetting.getInstance().throttlingReadLimitProperty().addListener((observable, oldValue, newValue) -> ProxyManager.getInternalProxy().getTrafficShapingHandler().setReadLimit(newValue.longValue()));
        GlobalSetting.getInstance().throttlingWriteLimitProperty().addListener((observable, oldValue, newValue) -> ProxyManager.getInternalProxy().getTrafficShapingHandler().setWriteLimit(newValue.longValue()));
    }

    public void onJsonViewerMenuItemClicked() {
        JsonViewerDialog.show();
    }

    public void onAllowListMenuItemClicked() {
        Object userData = AllowListSettingDialog.showAndWait();
        if (null != userData) {
            boolean enable = (boolean) userData;
            allowListMenuItem.setSelected(enable);
        }
    }

    public void onBlockListMenuItemClicked() {
        Object userData = BlockUrlSettingDialog.showAndWait();
        if (null != userData) {
            boolean enable = (boolean) userData;
            blockListMenuItem.setSelected(enable);
        }
    }

    public void onThrottlingSettingMenuItemClicked() {
        ThrottlingSettingDialog.show();
    }

    public void onProxySettingMenuItemClicked() {
        ProxySettingDialog.show();
    }

    public void onSystemProxyMenuItemClicked() {
        try {
            GlobalSetting setting = GlobalSetting.getInstance();
            if (setting.isSystemProxy()) {
                ProxyManager.disableSystemProxy();
            } else {
                ProxyManager.enableSystemProxy();
            }
            setting.setSystemProxy(!setting.isSystemProxy());
            systemProxyMenuItem.setSelected(setting.isSystemProxy());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }

    public void onThrottlingMenuItemClicked() {
        GlobalSetting.getInstance().setThrottling(!GlobalSetting.getInstance().isThrottling());
    }

    public void onThrottlingSwitchButtonClicked() {
        GlobalSetting.getInstance().setThrottling(!GlobalSetting.getInstance().isThrottling());
    }

    public void onOpenMenuItemClicked() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Json Files", "*.json"));
//        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
//        if (null != file) {
//            try {
//                String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
//                HexContentFlow flow = JacksonUtil.parse(data, HexContentFlow.class);
//                flowService.save(flow);
//                ConnectionInfo connectionInfo = new ConnectionInfo();
//                connectionInfo.setUrl(flow.getRequest().getUri());
//                addFlow(connectionInfo, flow.getRequest(), flow.getResponse());
//            } catch (IOException | URISyntaxException e) {
//                log.error(e.getMessage(), e);
//            }
//        }
    }

    public void onSaveMenuClicked() {
//        if (StringUtils.isEmpty(currentRequestId)) {
//            return;
//        }
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        File dir = directoryChooser.showDialog(App.getPrimaryStage());
//        if (null != dir) {
//            Flow flow = getFlow(currentRequestId);
//            HexContentFlow hexContentFlow = new HexContentFlow();
//            hexContentFlow.setRequest(flow.getRequest());
//            hexContentFlow.setResponse(flow.getResponse());
//            hexContentFlow.setRequestHeaders(flow.getRequestHeaders());
//            hexContentFlow.setResponseHeaders(flow.getResponseHeaders());
//            if (!StringUtils.isEmpty(flow.getRequest().getContentId())) {
//                Content content = contentMapper.selectById(flow.getRequest().getContentId());
//                hexContentFlow.setHexRequestContent(Hex.toHexString(content.getContent()));
//            }
//            if (!StringUtils.isEmpty(flow.getResponse().getContentId())) {
//                Content content = contentMapper.selectById(flow.getResponse().getContentId());
//                hexContentFlow.setHexResponseContent(Hex.toHexString(content.getContent()));
//            }
//            String data = JacksonUtil.toJsonString(hexContentFlow, true);
//            try {
//                String filePath = dir.getAbsolutePath() + File.separator + flow.getRequest().getId() + ".json";
//                FileUtils.writeStringToFile(new File(filePath), data, StandardCharsets.UTF_8);
//            } catch (IOException e) {
//                log.error(e.getMessage(), e);
//                AlertUtil.error(e);
//            }
//        }
    }

    public void onRecordMenuItemClicked() {
        GlobalSetting.getInstance().setRecord(!GlobalSetting.getInstance().isRecord());
    }

    public void onRecordSwitchButtonClicked() {
        GlobalSetting.getInstance().setRecord(!GlobalSetting.getInstance().isRecord());
    }

    public void onRepeatButtonClicked() {
        if (null == currentRequestId) {
            return;
        }
        final Flow flow = getFlow(currentRequestId);
        App.EXECUTOR.execute(() -> {
            try (CloseableHttpClient httpClient = HttpClientUtil.createTrustAllApacheHttpClientBuilder()
                    .setProxy(new HttpHost("127.0.0.1", GlobalSetting.getInstance().getPort()))
                    .build()) {
                Request request = flow.getRequest();
                RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod()).setUri(request.getUri());
                List<String> autoCalculateHeaders = List.of("Host", "Content-Length", "host", "content-length");
                flow.getRequestHeaders().forEach(header -> {
                    if (!autoCalculateHeaders.contains(header.getName())) {
                        requestBuilder.addHeader(header.getName(), header.getValue());
                    }
                });
                if (null != request.getContentId()) {
                    Content content = contentMapper.selectById(request.getContentId());
                    requestBuilder.setEntity(new ByteArrayEntity(content.getContent()));
                }
                httpClient.execute(requestBuilder.build()).close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public SendReqController onSendRequestMenuItemClicked() {
        Stage sendReqStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/SendReq.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            SendReqController sendReqController = fxmlLoader.getController();
            sendReqController.setStage(sendReqStage);
            sendReqStage.setScene(new Scene(parent));
            App.setCommonIcon(sendReqStage, "Lightning");
            sendReqStage.initModality(Modality.APPLICATION_MODAL);
            sendReqStage.show();
            return sendReqController;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public void onEditButtonClicked() {
        onSendRequestMenuItemClicked().setRequestId(currentRequestId);
    }

    public void onClearButtonClicked() {
        overviewRoot.getChildren().clear();
        allNode.clear();
        clear();
        currentRequestId = null;
        treeView.getRoot().getChildren().clear();
        listView.getItems().clear();
        allNode.clear();
        infoLabel.setText("");
    }

    public void onFilterTextFieldEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            treeViewFilter();
            listViewFilter();
            if (null != currentRequestId) {
                ObservableList<FlowNode> flowNodes = listView.getItems();
                boolean present = flowNodes.stream().anyMatch(node -> node.getId().equals(currentRequestId));
                if (!present) {
                    currentRequestId = null;
                }
            }
        }
    }

    private void listViewFilter() {
        String text = filterTextField.getText().trim();
        listView.getItems().clear();
        if (!StringUtils.isEmpty(text)) {
            allNode.forEach(node -> {
                if (node.getUrl().contains(text)) {
                    listView.getItems().add(node);
                }
            });
        } else {
            allNode.forEach(node -> listView.getItems().add(node));
        }
    }

    private void clear() {
        requestHeaderTableView.getItems().clear();
        queryTableView.getItems().clear();
        formTableView.getItems().clear();
        requestRawTextArea.clear();
        responseHeaderTableView.getItems().clear();
        responseRawTextArea.clear();
        responseTextArea.clear();
        removeTabByTitle(requestTabPane, requestQueryTab);
        removeTabByTitle(requestTabPane, requestFormTab);
        removeTabByTitle(responseTabPane, responseImageTab);
        removeTabByTitle(responseTabPane, responseContentTab);
    }

    private void removeTabByTitle(TabPane tabPane, Tab tabToRemove) {
        tabPane.getTabs().removeIf(tab -> tab.getText().equals(tabToRemove.getText()));
    }

    private void fillRequestRawTab(Request request, List<Header> requestHeaders) {
        StringBuilder requestRawBuilder = new StringBuilder();
        requestRawBuilder.append(request.getMethod()).append(" ").append(request.getUri()).append("\n");
        for (Header header : requestHeaders) {
            requestRawBuilder.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
        }
        if (!StringUtils.isEmpty(request.getContentId())) {
            Content content = contentMapper.selectById(request.getContentId());
            byte[] bytes = content.getContent();
            requestRawBuilder.append("\n");
            // TODO charset utf8 gbk gb2312 iso8859-1
            String raw = new String(bytes);
            requestRawBuilder.append(raw);
            requestHeaders.stream().filter(header -> HttpHeaderNames.CONTENT_TYPE.toString().equalsIgnoreCase(header.getName())).findFirst().ifPresent(header -> {
                if (header.getValue().startsWith(ContentType.APPLICATION_FORM)) {
                    List<ColumnMap> columnMaps = ColumnMap.listOf(raw);
                    formTableView.getItems().addAll(columnMaps);
                    requestTabPane.getTabs().add(1, requestFormTab);
                }
            });
        }
        requestRawTextArea.appendText(requestRawBuilder.toString());
    }

    private void fillRequestQueryTab(Request request) {
        try {
            URI uri = new URI(request.getUri());
            String query = uri.getQuery();
            if (!StringUtils.isEmpty(query)) {
                List<ColumnMap> columnMaps = ColumnMap.listOf(query);
                queryTableView.getItems().addAll(columnMaps);
                requestTabPane.getTabs().add(1, requestQueryTab);
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }

    private void fillResponseRawTab(Response response, List<Header> responseHeaders) {
        StringBuilder responseRawBuilder = new StringBuilder();
        responseRawBuilder.append("Status : ").append(response.getStatus()).append("\n");
        for (Header header : responseHeaders) {
            responseRawBuilder.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
        }
        if (!StringUtils.isEmpty(response.getContentId())) {
            Content content = contentMapper.selectById(response.getContentId());
            byte[] bytes = content.getContent();
            responseRawBuilder.append("\n");
            responseHeaders.stream().filter(header -> HttpHeaderNames.CONTENT_TYPE.toString().equalsIgnoreCase(header.getName())).findFirst().ifPresent(header -> {
                WebEngine engine = responseJsonWebView.getEngine();
                String hexRaw = Hex.toHexString(bytes);
                if (header.getValue().startsWith("image")) {
                    Image image = new Image(new ByteArrayInputStream(bytes));
                    responseImageView.setImage(image);
                    responseImageView.setFitHeight(image.getHeight());
                    responseImageView.setFitWidth(image.getWidth());
                    responseRawBuilder.append("<Image>");
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
                    appendTab(responseTabPane, responseImageTab);
                } else {
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseImageTab.getText()));
                    if (bytes.length > 100_000) {
                        responseRawBuilder.append("<Too Large>");
                        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
                    } else {
                        String raw = new String(bytes, StandardCharsets.UTF_8);
                        responseTextArea.setText(raw);
                        responseRawBuilder.append(raw);
                        appendTab(responseTabPane, responseTextTab);
                    }

                    if (header.getValue().startsWith(ContentType.APPLICATION_JSON)) {
                        engine.executeScript(String.format("setHexJson('%s')", hexRaw));
                        responseContentTab.setText("JSON");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.TEXT_HTML)) {
                        engine.executeScript(String.format("setHexHtml('%s')", hexRaw));
                        responseContentTab.setText("HTML");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.APPLICATION_JAVASCRIPT)) {
                        Platform.runLater(() -> engine.executeScript(String.format("setHexJavaScript('%s')", hexRaw)));
                        responseContentTab.setText("JavaScript");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.APPLICATION_XML) || header.getValue().startsWith(ContentType.TEXT_XML)) {
                        engine.executeScript(String.format("setHexXml('%s')", hexRaw));
                        responseContentTab.setText("XML");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.TEXT_CSS)) {
                        engine.executeScript(String.format("setHexCss('%s')", hexRaw));
                        responseContentTab.setText("CSS");
                        appendTab(responseTabPane, responseContentTab);
                    } else {
                        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
                    }
                }

            });
        } else {
            responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
            responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
            responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseImageTab.getText()));
        }
        responseRawTextArea.appendText(responseRawBuilder.toString());
    }

    private void fillOverviewTab(FlowNode selectedNode) {
        overviewRoot.getChildren().clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Request request = requestMapper.selectById(selectedNode.getId());
        overviewTreeTableView.setUserData(selectedNode.getId());
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Url", request.getUri())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Status", selectedNode.getStatus() == -1 ? "Loading" : "Complete")));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Response Code", selectedNode.getStatus() == -1 ? "" : String.valueOf(selectedNode.getStatus()))));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Protocol", request.getHttpVersion())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Method", request.getMethod())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Host", request.getHost())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Port", String.valueOf(request.getPort()))));
        QueryWrapper<Response> responseQuery = new QueryWrapper<>();
        responseQuery.eq("request_id", request.getId());
        Response response = responseMapper.selectOne(responseQuery);
        if (null != response) {
            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Content-Type", response.getContentType())));
            QueryWrapper<ConnectionOverview> connectionOverviewQuery = new QueryWrapper<>();
            connectionOverviewQuery.eq("request_id", request.getId());
            ConnectionOverview connectionOverview = connectionOverviewMapper.selectOne(connectionOverviewQuery);
            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Client Address", connectionOverview.getClientHost() + ":" + connectionOverview.getClientPort())));
            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("DNS", connectionOverview.getDnsServer())));
            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Remote Address", connectionOverview.getRemoteIp())));
            TreeItem<PropertyPair> tlsTreeItem = new TreeItem<>(new PropertyPair("TLS", connectionOverview.getServerProtocol() + " (" + connectionOverview.getServerCipherSuite() + ")"));
            TreeItem<PropertyPair> clientSessionIdTreeItem = new TreeItem<>(new PropertyPair("Client Session ID", connectionOverview.getClientSessionId()));
            TreeItem<PropertyPair> serverSessionIdTreeItem = new TreeItem<>(new PropertyPair("Server Session ID", connectionOverview.getServerSessionId()));
            TreeItem<PropertyPair> clientTreeItem = new TreeItem<>(new PropertyPair("Client Certificate", ""));
            clientTreeItem.getChildren().clear();
            TreeItem<PropertyPair> serverTreeItem = new TreeItem<>(new PropertyPair("Server Certificate", ""));
            QueryWrapper<CertificateMap> certificateMapQuery = new QueryWrapper<>();
            certificateMapQuery.eq("request_id", request.getId());
            List<CertificateMap> certificateMaps = certificateMapMapper.selectList(certificateMapQuery);
            for (CertificateMap certificateMap : certificateMaps) {
                QueryWrapper<CertificateInfo> certificateInfoQuery = new QueryWrapper<>();
                certificateInfoQuery.eq("serial_number", certificateMap.getCertificateSerialNumber()).orderByDesc("time_created");
                CertificateInfo certificateInfo = certificateInfoMapper.selectOne(certificateInfoQuery);
                TreeItem<PropertyPair> certTreeItem = new TreeItem<>(new PropertyPair(certificateInfo.getSubjectCommonName(), ""));
                TreeItem<PropertyPair> serialNumberTreeItem = new TreeItem<>(new PropertyPair("Serial Number", certificateInfo.getSerialNumber()));
                TreeItem<PropertyPair> typeTreeItem = new TreeItem<>(new PropertyPair("Type", certificateInfo.getType() + " [v" + certificateInfo.getVersion() + "] (" + certificateInfo.getSigAlgName() + ")"));
                TreeItem<PropertyPair> issuedToTreeItem = new TreeItem<>(new PropertyPair("Issued To", ""));
                issuedToTreeItem.getChildren().clear();
                if (!StringUtils.isEmpty(certificateInfo.getSubjectCommonName())) {
                    issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Common Name", certificateInfo.getSubjectCommonName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getSubjectOrganizationDepartment())) {
                    issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Unit", certificateInfo.getSubjectOrganizationDepartment())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getSubjectOrganizationName())) {
                    issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Name", certificateInfo.getSubjectOrganizationName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getSubjectLocalityName())) {
                    issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Locality Name", certificateInfo.getSubjectLocalityName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getSubjectStateName())) {
                    issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("State Name", certificateInfo.getSubjectStateName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getSubjectCountry())) {
                    issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Country", certificateInfo.getSubjectCountry())));
                }
                TreeItem<PropertyPair> issuedByTreeItem = new TreeItem<>(new PropertyPair("Issued By", ""));
                issuedByTreeItem.getChildren().clear();
                if (!StringUtils.isEmpty(certificateInfo.getIssuerCommonName())) {
                    issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Common Name", certificateInfo.getIssuerCommonName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getIssuerOrganizationDepartment())) {
                    issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Unit", certificateInfo.getIssuerOrganizationDepartment())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getIssuerOrganizationName())) {
                    issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Name", certificateInfo.getIssuerOrganizationName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getIssuerLocalityName())) {
                    issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Locality Name", certificateInfo.getIssuerLocalityName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getIssuerStateName())) {
                    issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("State Name", certificateInfo.getIssuerStateName())));
                }
                if (!StringUtils.isEmpty(certificateInfo.getIssuerCountry())) {
                    issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Country", certificateInfo.getIssuerCountry())));
                }
                TreeItem<PropertyPair> notValidBeforeTreeItem = new TreeItem<>(new PropertyPair("Not Valid Before", dateFormat.format(certificateInfo.getNotValidBefore())));
                TreeItem<PropertyPair> notValidAfterTreeItem = new TreeItem<>(new PropertyPair("Not Valid After", dateFormat.format(certificateInfo.getNotValidAfter())));
                TreeItem<PropertyPair> fingerprintsTreeItem = new TreeItem<>(new PropertyPair("Fingerprints", ""));
                fingerprintsTreeItem.getChildren().clear();
                fingerprintsTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("SHA-1", certificateInfo.getShaOne())));
                fingerprintsTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("SHA-256", certificateInfo.getShaTwoFiveSix())));
//                TreeItem<PropertyPair> fullDetailTreeItem = new TreeItem<>(new PropertyPair("Full Detail", certificateInfo.getFullDetail()));
                certTreeItem.getChildren().clear();
                certTreeItem.getChildren().add(serialNumberTreeItem);
                certTreeItem.getChildren().add(typeTreeItem);
                certTreeItem.getChildren().add(issuedToTreeItem);
                certTreeItem.getChildren().add(issuedByTreeItem);
                certTreeItem.getChildren().add(notValidBeforeTreeItem);
                certTreeItem.getChildren().add(notValidAfterTreeItem);
                certTreeItem.getChildren().add(fingerprintsTreeItem);
//                certTreeItem.getChildren().add(fullDetailTreeItem);
                if (null == certificateMap.getResponseId()) {
                    clientTreeItem.getChildren().add(certTreeItem);
                } else {
                    serverTreeItem.getChildren().add(certTreeItem);
                }
            }
            tlsTreeItem.getChildren().clear();
            tlsTreeItem.getChildren().add(clientSessionIdTreeItem);
            tlsTreeItem.getChildren().add(serverSessionIdTreeItem);
            tlsTreeItem.getChildren().add(clientTreeItem);
            tlsTreeItem.getChildren().add(serverTreeItem);
            overviewRoot.getChildren().add(tlsTreeItem);
            TreeItem<PropertyPair> timingTreeItem = new TreeItem<>(new PropertyPair("Timing", ""));
            timingTreeItem.getChildren().clear();
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Request Start Time", dateFormat.format(request.getStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Request End Time", dateFormat.format(request.getEndTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Connect Start Time", dateFormat.format(connectionOverview.getConnectStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Connect End Time", dateFormat.format(connectionOverview.getConnectStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Response Start Time", dateFormat.format(response.getStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Response End Time", dateFormat.format(response.getEndTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Request", (request.getEndTime() - request.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Response", (response.getEndTime() - response.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Duration", (response.getEndTime() - request.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("DNS", (connectionOverview.getDnsEndTime() - connectionOverview.getDnsStartTime()) + " ms")));
            overviewRoot.getChildren().add(timingTreeItem);
        }
    }

    private void fillContentsTab(FlowNode selectedNode) {
        if (EnumFlowType.TARGET.equals(selectedNode.getType())) {
            if (null != currentRequestId && currentRequestId.equals(selectedNode.getId())) {
                return;
            }
            clear();
            currentRequestId = selectedNode.getId();
            Flow flow = getFlow(currentRequestId);
            Request request = flow.getRequest();
            Response response = flow.getResponse();
            List<Header> requestHeaders = flow.getRequestHeaders();
            List<Header> responseHeaders = flow.getResponseHeaders();
            infoLabel.setText(request.getMethod().toUpperCase() + " " + request.getUri());
            requestHeaderTableView.getItems().addAll(requestHeaders);
            if (!CollectionUtils.isEmpty(responseHeaders)) {
                responseHeaderTableView.getItems().addAll(responseHeaders);
            }
            fillRequestRawTab(request, requestHeaders);
            fillRequestQueryTab(request);
            if (null != response) {
                fillResponseRawTab(response, responseHeaders);
            }
        }
    }

    private Flow getFlow(String requestId) {
        Flow flow = new Flow();
        Request request = requestMapper.selectById(requestId);
        QueryWrapper<Header> requestHeaderQueryWrapper = new QueryWrapper<>();
        requestHeaderQueryWrapper.eq("request_id", request.getId());
        List<Header> requestHeaders = headerMapper.selectList(requestHeaderQueryWrapper);
        QueryWrapper<Response> responseQueryWrapper = new QueryWrapper<>();
        responseQueryWrapper.eq("request_id", request.getId());
        flow.setRequest(request);
        flow.setRequestHeaders(requestHeaders);
        Response response = responseMapper.selectOne(responseQueryWrapper);
        if (null != response) {
            QueryWrapper<Header> responseHeaderQueryWrapper = new QueryWrapper<>();
            responseHeaderQueryWrapper.eq("response_id", response.getId());
            List<Header> responseHeaders = headerMapper.selectList(responseHeaderQueryWrapper);
            flow.setResponse(response);
            flow.setResponseHeaders(responseHeaders);
        }
        return flow;
    }

    private void addTreeNode(FlowNode flowNode) throws URISyntaxException {
        URI uri = new URI(flowNode.getUrl());
        String baseUri = uri.getScheme() + "://" + uri.getAuthority();
        ObservableList<TreeItem<FlowNode>> children = root.getChildren();
        TreeItem<FlowNode> baseNodeTreeItem = children.stream().filter(item -> item.getValue().getUrl().equals(baseUri)).findFirst().orElseGet(() -> {
            FlowNode baseNode = new FlowNode();
            baseNode.setUrl(baseUri);
            baseNode.setType(EnumFlowType.BASE_URL);
            baseNode.setCurrentUrl(baseUri);
            baseNode.setStatus(flowNode.getStatus());
            TreeItem<FlowNode> item = new TreeItem<>(baseNode, Icons.FONT_AWESOME.create(FontAwesome.Glyph.GLOBE));
            item.setExpanded(CollectionUtils.isEmpty(root.getChildren()));
            root.getChildren().add(item);
            return item;
        });
        FlowNode currentParent = baseNodeTreeItem.getValue();
        if ("".equals(uri.getPath()) || "/".equals(uri.getPath())) {
            FlowNode rootPathNode = new FlowNode();
            rootPathNode.setParent(currentParent);
            rootPathNode.setId(flowNode.getId());
            rootPathNode.setUrl("/");
            rootPathNode.setCurrentUrl(flowNode.getUrl());
            rootPathNode.setType(EnumFlowType.TARGET);
            TreeItem<FlowNode> rootPathTreeItem = new TreeItem<>(rootPathNode, Icons.loadIcon(flowNode.getStatus(), flowNode.getContentType()));
            rootPathTreeItem.setExpanded(CollectionUtils.isEmpty(overviewRoot.getChildren()));
            baseNodeTreeItem.getChildren().add(rootPathTreeItem);
        } else {
            String[] array = (uri.getPath() + " ").split("/");
            int len = array.length;
            TreeItem<FlowNode> currentItem = baseNodeTreeItem;
            StringBuilder currentUrl = new StringBuilder(baseUri);
            for (int i = 1; i < len; i++) {
                String fragment = "".equals(array[i].trim()) ? "/" : array[i].trim();
                currentUrl.append("/").append(fragment);
                if (i == (len - 1)) {
                    FlowNode node = new FlowNode();
                    node.setParent(currentParent);
                    node.setId(flowNode.getId());
                    node.setUrl(fragment);
                    node.setCurrentUrl(flowNode.getUrl());
                    node.setType(EnumFlowType.TARGET);
                    node.setStatus(flowNode.getStatus());
                    TreeItem<FlowNode> treeItem = new TreeItem<>(node, Icons.loadIcon(flowNode.getStatus(), flowNode.getContentType()));
                    currentItem.getChildren().add(treeItem);
                } else {
                    ObservableList<TreeItem<FlowNode>> treeItems = currentItem.getChildren();
                    final FlowNode finalCurrentParent = currentParent;
                    currentItem = treeItems.stream().filter(item -> item.getValue().getUrl().equals(fragment) && item.getValue().getType().equals(EnumFlowType.PATH))
                            .findFirst().orElseGet(() -> {
                                FlowNode node = new FlowNode();
                                node.setUrl(fragment);
                                node.setParent(finalCurrentParent);
                                node.setType(EnumFlowType.PATH);
                                node.setContentType(flowNode.getContentType());
                                node.setCurrentUrl(currentUrl.toString());
                                TreeItem<FlowNode> treeItem = new TreeItem<>(node, Icons.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER_OPEN_ALT));
                                treeItem.setExpanded(true);
                                treeItems.add(treeItem);
                                return treeItem;
                            });
                    currentParent = currentItem.getValue();
                }
            }
        }
    }

    private void updateTreeItem(FlowNode flowNode) throws URISyntaxException {
        URI uri = new URI(flowNode.getUrl());
        String baseUri = uri.getScheme() + "://" + uri.getAuthority();
        ObservableList<TreeItem<FlowNode>> children = root.getChildren();
        Queue<TreeItem<FlowNode>> queue = children.stream().filter(item -> item.getValue().getUrl().equals(baseUri)).collect(Collectors.toCollection(LinkedList::new));
        TreeItem<FlowNode> item = null;
        while (!queue.isEmpty()) {
            TreeItem<FlowNode> treeItem = queue.poll();
            FlowNode node = treeItem.getValue();
            if (null != node.getId() && node.getId().equals(flowNode.getId())) {
                item = treeItem;
                break;
            }
            ObservableList<TreeItem<FlowNode>> itemChildren = treeItem.getChildren();
            if (!CollectionUtils.isEmpty(itemChildren)) {
                queue.addAll(itemChildren);
            }
        }
        if (null != item) {
            item.getValue().setStatus(flowNode.getStatus());
            item.getValue().setContentType(flowNode.getContentType());
            Node node = Icons.loadIcon(flowNode.getStatus(), flowNode.getContentType());
            item.setGraphic(node);
        }
    }

    private void treeViewFilter() {
        String text = filterTextField.getText().trim();
        if (StringUtils.isEmpty(text)) {
            treeView.setRoot(root);
            return;
        }
        TreeItem<FlowNode> filterRoot = new TreeItem<>(new FlowNode());
        ObservableList<TreeItem<FlowNode>> children = root.getChildren();
        for (TreeItem<FlowNode> item : children) {
            if (item.getValue().getUrl().contains(text)) {
                filterRoot.getChildren().add(item);
            }
        }
        treeView.setRoot(filterRoot);
    }

    public void addFlow(ConnectionInfo connectionInfo, Request request, Response response) throws URISyntaxException {
        FlowNode flowNode = new FlowNode();
        flowNode.setId(request.getId());
        flowNode.setType(EnumFlowType.TARGET);
        flowNode.setUrl(connectionInfo.getUrl());
        flowNode.setRequestTime(request.getTimeCreated());
        if (null == response) {
            flowNode.setStatus(-1);
            flowNode.setRequestTime(request.getTimeCreated());
            addTreeNode(flowNode);
            allNode.add(flowNode);
        } else {
            flowNode.setStatus(response.getStatus());
            flowNode.setContentType(response.getContentType());
            allNode.stream().filter(item -> item.getId().equals(flowNode.getId())).findFirst().ifPresent(node -> {
                node.setContentType(response.getContentType());
                node.setStatus(response.getStatus());
                node.setStatusProperty(response.getStatus());
            });
            updateTreeItem(flowNode);
            if (null != currentRequestId && currentRequestId.equals(request.getId())) {
                QueryWrapper<Header> responseHeaderQueryWrapper = new QueryWrapper<>();
                responseHeaderQueryWrapper.eq("response_id", response.getId());
                List<Header> responseHeaders = headerMapper.selectList(responseHeaderQueryWrapper);
                if (!CollectionUtils.isEmpty(responseHeaders)) {
                    responseHeaderTableView.getItems().addAll(responseHeaders);
                }
                fillResponseRawTab(response, responseHeaders);
                fillOverviewTab(flowNode);
            }
        }
        treeViewFilter();
        listViewFilter();
    }

    private void appendTab(TabPane tabPane, Tab tab) {
        FilteredList<Tab> list = tabPane.getTabs().filtered(item -> item.getText().equals(tab.getText()));
        if (CollectionUtils.isEmpty(list)) {
            tabPane.getTabs().add(tabPane.getTabs().size(), tab);
        }
    }

}
