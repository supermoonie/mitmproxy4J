package com.github.supermoonie.proxy.fx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.controller.dialog.*;
import com.github.supermoonie.proxy.fx.dto.ColumnMap;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.entity.*;
import com.github.supermoonie.proxy.fx.mapper.*;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.service.FlowService;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.support.*;
import com.github.supermoonie.proxy.fx.util.*;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.util.encoders.Hex;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
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

    private final RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
    private final HeaderMapper headerMapper = ApplicationContextUtil.getBean(HeaderMapper.class);
    private final ResponseMapper responseMapper = ApplicationContextUtil.getBean(ResponseMapper.class);
    private final ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);
    private final FlowService flowService = ApplicationContextUtil.getBean(FlowService.class);
    private final ConnectionOverviewMapper connectionOverviewMapper = ApplicationContextUtil.getBean(ConnectionOverviewMapper.class);
    private final CertificateMapMapper certificateMapMapper = ApplicationContextUtil.getBean(CertificateMapMapper.class);
    private final CertificateInfoMapper certificateInfoMapper = ApplicationContextUtil.getBean(CertificateInfoMapper.class);

    private final KeyCodeCombination macKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);
    private final KeyCodeCombination winKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

    private final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    private final Image blackLoadingIcon = new Image(getClass().getResourceAsStream("/icon/loading_000.gif"), 16, 16, false, false);
    private final Image whiteLoadingIcon = new Image(getClass().getResourceAsStream("/icon/loading_fff.gif"), 16, 16, false, false);
    private final Image clearIcon = new Image(getClass().getResourceAsStream("/icon/clear.png"), 16, 16, false, false);
    private final Image grayDotIcon = new Image(getClass().getResourceAsStream("/icon/dot_gray.png"), 16, 16, false, false);
    private final Image greenDotIcon = new Image(getClass().getResourceAsStream("/icon/dot_green.png"), 16, 16, false, false);

    private final ObservableList<FlowNode> allNode = FXCollections.observableList(new LinkedList<>());
    private final TreeItem<FlowNode> root = new TreeItem<>(new FlowNode());
    private String currentRequestId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (structureTab.equals(newValue)) {
                filterTextField.setPromptText("Domain Filter");
            } else if (sequenceTab.equals(newValue)) {
                filterTextField.setPromptText("Filter");
            }
        });
        blockListMenuItem.setSelected(GlobalSetting.getInstance().isBlockUrl());
        allowListMenuItem.setSelected(GlobalSetting.getInstance().isAllowUrl());
        systemProxyMenuItem.setSelected(GlobalSetting.getInstance().isSystemProxy());
        initToolBar();
        initRecordSetting();
        initThrottlingSetting();
        initContextMenu();
        initTreeView();
        initListView();
        initResponseJsonWebView();
        clear();
        overviewNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getKey()));
        overviewValueColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getValue()));
        overviewRoot.setExpanded(true);
        overviewTreeTableView.setRoot(overviewRoot);
        overviewTreeTableView.setShowRoot(false);
        overviewTreeTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    TreeItem<PropertyPair> selectedItem = overviewTreeTableView.getSelectionModel().getSelectedItem();
                    if (null != selectedItem) {
                        PropertyPair propertyPair = selectedItem.getValue();
                        if (!StringUtils.isEmpty(propertyPair.getValue())) {
                            Stage stage = new Stage();
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/InfoDialog.fxml"));
                            try {
                                Parent parent = fxmlLoader.load();
                                InfoDialog infoDialog = fxmlLoader.getController();
                                infoDialog.setStage(stage);
                                infoDialog.setText(propertyPair.getValue());
                                stage.setScene(new Scene(parent));
                                App.setCommonIcon(stage, propertyPair.getKey());
                                stage.initModality(Modality.NONE);
                                stage.initStyle(StageStyle.UTILITY);
                                stage.setX(event.getX() + 300);
                                stage.setY(event.getY() + 100);
                                stage.showAndWait();
                            } catch (IOException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        });
    }

    private void initListView() {
        listView.setCellFactory(param -> new ListCell<>() {
            private final ChangeListener<Number> statusChangeListener = (observable, oldValue, newValue) -> {
                if (null == newValue || null == getItem()) {
                    return;
                }
                setGraphic(loadIcon(getItem().getStatus(), getItem().getContentType(), false));
            };

            @Override
            protected void updateItem(FlowNode item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getUrl());
                    setGraphic(loadIcon(item.getStatus(), item.getContentType(), false));
                    item.statusPropertyProperty().removeListener(statusChangeListener);
                    item.statusPropertyProperty().addListener(statusChangeListener);
                }
            }
        });
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            FlowNode selectedItem = listView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            fillContentsTab(selectedItem);
        });
        listView.setContextMenu(listContextMenu);
    }

    private void initTreeView() {
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.setContextMenu(treeContextMenu);
        treeView.contextMenuProperty().bind(Bindings.when(treeView.getSelectionModel().selectedItemProperty().isNull()).then((ContextMenu) null).otherwise(treeContextMenu));
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            FlowNode selectedNode = selectedItem.getValue();
            if (null == selectedNode) {
                return;
            }
            if (!selectedNode.getType().equals(EnumFlowType.TARGET)) {
                return;
            }
            fillContentsTab(selectedNode);
            fillOverviewTab(selectedNode);
        });
        treeView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            Node node = selectedItem.getGraphic();
            FlowNode flowNode = selectedItem.getValue();
            if (!newValue) {
                if (flowNode.getType().equals(EnumFlowType.TARGET) && node instanceof Glyph) {
                    Glyph glyph = (Glyph) node;
                    glyph.setColor((Color) Objects.requireNonNullElse(glyph.getUserData(), Color.BLACK));
                }
            } else {
                if (flowNode.getType().equals(EnumFlowType.TARGET) && node instanceof Glyph) {
                    Glyph glyph = (Glyph) node;
                    glyph.setColor(Color.WHITE);
                }
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (null != oldValue) {
                FlowNode node = oldValue.getValue();
                Node oldNode = oldValue.getGraphic();
                if (node.getType().equals(EnumFlowType.TARGET) && oldNode instanceof Glyph) {
                    Glyph glyph = (Glyph) oldNode;
                    glyph.setColor((Color) Objects.requireNonNullElse(glyph.getUserData(), Color.BLACK));
                }
                if (-1 == node.getStatus() && node.getType().equals(EnumFlowType.TARGET)) {
                    if (oldNode instanceof ImageView) {
                        ImageView imageView = (ImageView) oldNode;
                        imageView.setImage(blackLoadingIcon);
                    }
                }

            }
            if (null != newValue) {
                FlowNode node = newValue.getValue();
                Node newNode = newValue.getGraphic();
                if (-1 == node.getStatus() && node.getType().equals(EnumFlowType.TARGET)) {
                    if (newNode instanceof ImageView) {
                        ImageView imageView = (ImageView) newNode;
                        imageView.setImage(whiteLoadingIcon);
                    }
                }
                if (node.getType().equals(EnumFlowType.TARGET) && newNode instanceof Glyph) {
                    Glyph glyph = (Glyph) newNode;
                    glyph.setColor(Color.WHITE);
                }
            }
        });
    }

    private final EventHandler<ActionEvent> copyMenuItemHandler = event -> {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            FlowNode node = selectedItem.getValue();
            if (!node.getType().equals(EnumFlowType.BASE_URL)) {
                List<String> list = new LinkedList<>();
                TreeItem<FlowNode> current = selectedItem;
                while (current != treeView.getRoot()) {
                    list.add(current.getValue().getUrl());
                    current = current.getParent();
                }
                Collections.reverse(list);
                String url = String.join("/", list);
                ClipboardUtil.copyText(url);
            } else {
                ClipboardUtil.copyText(node.getUrl());
            }
        } else {
            FlowNode node = listView.getSelectionModel().getSelectedItem();
            if (null == node) {
                return;
            }
            ClipboardUtil.copyText(node.getUrl());
        }

    };

    private final EventHandler<ActionEvent> copyResponseMenuItemHandler = event -> {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        FlowNode node;
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            node = selectedItem.getValue();
        } else {
            node = listView.getSelectionModel().getSelectedItem();
            if (node == null) {
                return;
            }
        }
        Request request = requestMapper.selectById(node.getId());
        QueryWrapper<Response> resQuery = new QueryWrapper<>();
        resQuery.eq("request_id", request.getId());
        Response response = responseMapper.selectOne(resQuery);
        String contentType = response.getContentType();
        Content content = contentMapper.selectById(response.getContentId());
        if (null != content && null != content.getContent() && content.getContent().length > 0) {
            if (contentType.startsWith("image/")) {
                ClipboardUtil.copyImage(new Image(new ByteArrayInputStream(content.getContent())));
            } else {
                // TODO contentType 与 文件映射
                ClipboardUtil.copyText(new String(content.getContent(), StandardCharsets.UTF_8));
            }
        } else {
            ClipboardUtil.copyText("");
        }
    };

    private final EventHandler<ActionEvent> saveResponseMenuItemHandler = event -> {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        FlowNode node;
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            node = selectedItem.getValue();
        } else {
            node = listView.getSelectionModel().getSelectedItem();
            if (node == null) {
                return;
            }
        }
        Request request = requestMapper.selectById(node.getId());
        QueryWrapper<Response> resQuery = new QueryWrapper<>();
        resQuery.eq("request_id", request.getId());
        Response response = responseMapper.selectOne(resQuery);
        Content content = contentMapper.selectById(response.getContentId());
        if (null != content && null != content.getContent() && content.getContent().length > 0) {
            FileChooser fileChooser = new FileChooser();
            String lastFragment = UrlUtil.getLastFragment(request.getUri());
            if (null != lastFragment) {
                fileChooser.setInitialFileName(lastFragment);
            }
            File file = fileChooser.showSaveDialog(App.getPrimaryStage());
            try {
                FileUtils.writeByteArrayToFile(file, content.getContent());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                AlertUtil.error(e);
            }
        } else {
            AlertUtil.info("Empty Response!");
        }
    };

    private final EventHandler<ActionEvent> editMenuItemHandler = event -> {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        FlowNode node;
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            node = selectedItem.getValue();
        } else {
            node = listView.getSelectionModel().getSelectedItem();
            if (node == null) {
                return;
            }
        }
        onSendRequestMenuItemClicked().setRequestId(node.getId());
    };

    private final EventHandler<ActionEvent> addBlockListMenuItemHandler = event -> {
        GlobalSetting setting = GlobalSetting.getInstance();
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        FlowNode node;
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            node = selectedItem.getValue();
            String url;
            if (!node.getType().equals(EnumFlowType.BASE_URL)) {
                List<String> list = new LinkedList<>();
                TreeItem<FlowNode> current = selectedItem;
                while (current != treeView.getRoot()) {
                    list.add(current.getValue().getUrl());
                    current = current.getParent();
                }
                Collections.reverse(list);
                url = String.join("/", list);
            } else {
                url = node.getUrl();
            }
            BlockUrl blockUrl = new BlockUrl(true, url);
            setting.getBlockUrlList().add(blockUrl);
        } else {
            node = listView.getSelectionModel().getSelectedItem();
            if (node == null) {
                return;
            }
            BlockUrl blockUrl = new BlockUrl(true, node.getUrl());
            setting.getBlockUrlList().add(blockUrl);
        }
    };

    private final EventHandler<ActionEvent> addAllowListMenuItemHandler = event -> {
        GlobalSetting setting = GlobalSetting.getInstance();
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        FlowNode node;
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            node = selectedItem.getValue();
            String url;
            if (!node.getType().equals(EnumFlowType.BASE_URL)) {
                List<String> list = new LinkedList<>();
                TreeItem<FlowNode> current = selectedItem;
                while (current != treeView.getRoot()) {
                    list.add(current.getValue().getUrl());
                    current = current.getParent();
                }
                Collections.reverse(list);
                url = String.join("/", list);
            } else {
                url = node.getUrl();
            }
            AllowUrl allowUrl = new AllowUrl(true, url);
            setting.getAllowUrlList().add(allowUrl);
        } else {
            node = listView.getSelectionModel().getSelectedItem();
            if (node == null) {
                return;
            }
            AllowUrl allowUrl = new AllowUrl(true, node.getUrl());
            setting.getAllowUrlList().add(allowUrl);
        }

    };

    private void initContextMenu() {
        copyMenuItem.setOnAction(copyMenuItemHandler);
        copyResponseMenuItem.setOnAction(copyResponseMenuItemHandler);
        saveResponseMenuItem.setOnAction(saveResponseMenuItemHandler);
        repeatMenuItem.setOnAction(event -> onRepeatButtonClicked());
        editMenuItem.setOnAction(editMenuItemHandler);
        blockMenuItem.setOnAction(addBlockListMenuItemHandler);
        allowMenuItem.setOnAction(addAllowListMenuItemHandler);
        treeContextMenu.getItems().addAll(copyMenuItem, copyResponseMenuItem, saveResponseMenuItem, new SeparatorMenuItem(), repeatMenuItem, editMenuItem, new SeparatorMenuItem(), blockMenuItem, allowMenuItem);
        listContextMenu.getItems().addAll(copyMenuItem, copyResponseMenuItem, saveResponseMenuItem, new SeparatorMenuItem(), repeatMenuItem, editMenuItem, new SeparatorMenuItem(), blockMenuItem, allowMenuItem);
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
            copyResponseMenuItem.setDisable(disable);
            saveResponseMenuItem.setDisable(disable);
        });
    }

    private void initResponseJsonWebView() {
        responseJsonWebView.setContextMenuEnabled(false);
        responseJsonWebView.getEngine().load(App.class.getResource("/static/RichText.html").toExternalForm());
        responseJsonWebView.setOnKeyPressed(keyEvent -> {
            if (macKeyCodeCopy.match(keyEvent) || winKeyCodeCopy.match(keyEvent)) {
                WebEngine engine = responseJsonWebView.getEngine();
                String text = engine.executeScript("try{codeEditor.getSelectedText();}catch(e){''}").toString();
                ClipboardUtil.copyText(text);
            }
        });
    }

    private void initToolBar() {
        ImageView clearIconView = new ImageView(clearIcon);
        clearIconView.setFitHeight(12);
        clearIconView.setFitWidth(12);
        clearButton.setGraphic(clearIconView);
        editButton.setGraphic(fontAwesome.create(FontAwesome.Glyph.EDIT));
        repeatButton.setGraphic(fontAwesome.create(FontAwesome.Glyph.REPEAT));
    }

    private void initRecordSetting() {
        recordMenuItem.setSelected(GlobalSetting.getInstance().isRecord());
        ImageView imageView = new ImageView(GlobalSetting.getInstance().isRecord() ? greenDotIcon : grayDotIcon);
        imageView.setFitWidth(12);
        imageView.setFitHeight(12);
        recordingSwitchButton.setGraphic(imageView);
        GlobalSetting.getInstance().recordProperty().addListener((observable, oldValue, newValue) -> {
            recordMenuItem.setSelected(newValue);
            imageView.setImage(newValue ? greenDotIcon : grayDotIcon);
            recordingSwitchButton.setGraphic(imageView);
        });
    }

    private void initThrottlingSetting() {
        throttlingMenuItem.setSelected(GlobalSetting.getInstance().isThrottling());
        ImageView imageView = new ImageView(GlobalSetting.getInstance().isThrottling() ? greenDotIcon : grayDotIcon);
        imageView.setFitWidth(12);
        imageView.setFitHeight(12);
        throttlingSwitchButton.setGraphic(imageView);
        GlobalSetting.getInstance().throttlingProperty().addListener((observable, oldValue, newValue) -> {
            throttlingMenuItem.setSelected(newValue);
            imageView.setImage(newValue ? greenDotIcon : grayDotIcon);
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
        Stage jsonViewerStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/JsonViewerDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            JsonViewerDialog jsonViewerDialog = fxmlLoader.getController();
            jsonViewerDialog.setStage(jsonViewerStage);
            jsonViewerStage.setScene(new Scene(parent));
            App.setCommonIcon(jsonViewerStage, "JSON Viewer");
            jsonViewerStage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void onAllowListMenuItemClicked() {
        Stage allowListSettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/AllowListSettingDialog.fxml"));
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
            if (null != allowListSettingStage.getUserData()) {
                boolean enable = (boolean) allowListSettingStage.getUserData();
                allowListMenuItem.setSelected(enable);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void onBlockListMenuItemClicked() {
        Stage blockUrlSettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/BlockUrlSettingDialog.fxml"));
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
            if (null != blockUrlSettingStage.getUserData()) {
                boolean enable = (boolean) blockUrlSettingStage.getUserData();
                blockListMenuItem.setSelected(enable);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void onThrottlingSettingMenuItemClicked() {
        Stage throttlingSettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/ThrottlingSettingDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            ThrottlingSettingDialog throttlingSettingDialog = fxmlLoader.getController();
            throttlingSettingDialog.setStage(throttlingSettingStage);
            throttlingSettingStage.setScene(new Scene(parent));
            App.setCommonIcon(throttlingSettingStage, "Throttling Setting");
            throttlingSettingStage.initModality(Modality.APPLICATION_MODAL);
            throttlingSettingStage.setResizable(false);
            throttlingSettingStage.initStyle(StageStyle.UTILITY);
            throttlingSettingStage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void onProxySettingMenuItemClicked() {
        Stage proxySettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/ProxySettingDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            ProxySettingDialog proxySettingDialog = fxmlLoader.getController();
            proxySettingDialog.setStage(proxySettingStage);
            proxySettingStage.setScene(new Scene(parent));
            App.setCommonIcon(proxySettingStage, "Lightning");
            proxySettingStage.initModality(Modality.APPLICATION_MODAL);
            proxySettingStage.setResizable(false);
            proxySettingStage.initStyle(StageStyle.UTILITY);
            proxySettingStage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Json Files", "*.json"));
        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
        if (null != file) {
            try {
                String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                HexContentFlow flow = JSON.parse(data, HexContentFlow.class);
                flowService.save(flow);
                ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setUrl(flow.getRequest().getUri());
                addFlow(connectionInfo, flow.getRequest(), flow.getResponse());
            } catch (IOException | URISyntaxException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void onSaveMenuClicked() {
        if (StringUtils.isEmpty(currentRequestId)) {
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(App.getPrimaryStage());
        if (null != dir) {
            Flow flow = getFlow(currentRequestId);
            HexContentFlow hexContentFlow = new HexContentFlow();
            hexContentFlow.setRequest(flow.getRequest());
            hexContentFlow.setResponse(flow.getResponse());
            hexContentFlow.setRequestHeaders(flow.getRequestHeaders());
            hexContentFlow.setResponseHeaders(flow.getResponseHeaders());
            if (!StringUtils.isEmpty(flow.getRequest().getContentId())) {
                Content content = contentMapper.selectById(flow.getRequest().getContentId());
                hexContentFlow.setHexRequestContent(Hex.toHexString(content.getContent()));
            }
            if (!StringUtils.isEmpty(flow.getResponse().getContentId())) {
                Content content = contentMapper.selectById(flow.getResponse().getContentId());
                hexContentFlow.setHexResponseContent(Hex.toHexString(content.getContent()));
            }
            String data = JSON.toJsonString(hexContentFlow, true);
            try {
                String filePath = dir.getAbsolutePath() + File.separator + flow.getRequest().getId() + ".json";
                FileUtils.writeStringToFile(new File(filePath), data, StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                AlertUtil.error(e);
            }
        }
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
                    responseRawBuilder.append("<Too Large>");
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
                    appendTab(responseTabPane, responseImageTab);
                } else {
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseImageTab.getText()));
                    String raw = new String(bytes, StandardCharsets.UTF_8);
                    responseTextArea.setText(raw);
                    responseRawBuilder.append(raw);
                    appendTab(responseTabPane, responseTextTab);
                    if (header.getValue().startsWith(ContentType.APPLICATION_JSON)) {
                        engine.executeScript(String.format("setHexJson('%s')", hexRaw));
                        responseContentTab.setText("JSON");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.TEXT_HTML)) {
                        engine.executeScript(String.format("setHexHtml('%s')", hexRaw));
                        responseContentTab.setText("HTML");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.APPLICATION_JAVASCRIPT)) {
                        engine.executeScript(String.format("setHexJavaScript('%s')", hexRaw));
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
            TreeItem<FlowNode> item = new TreeItem<>(baseNode, fontAwesome.create(FontAwesome.Glyph.GLOBE));
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
            TreeItem<FlowNode> rootPathTreeItem = new TreeItem<>(rootPathNode, loadIcon(flowNode.getStatus(), flowNode.getContentType()));
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
                    TreeItem<FlowNode> treeItem = new TreeItem<>(node, loadIcon(flowNode.getStatus(), flowNode.getContentType()));
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
                                TreeItem<FlowNode> treeItem = new TreeItem<>(node, fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN_ALT));
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
            Node node = loadIcon(flowNode.getStatus(), flowNode.getContentType());
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

    private List<TreeItem<FlowNode>> getLayer(TreeItem<FlowNode> root, int index) {
        List<TreeItem<FlowNode>> layer = new LinkedList<>();
        layer.add(root);
        for (int i = 0; i < index; i++) {
            List<TreeItem<FlowNode>> temp = new LinkedList<>();
            for (TreeItem<FlowNode> treeItem : layer) {
                ObservableList<TreeItem<FlowNode>> children = treeItem.getChildren();
                if (!CollectionUtils.isEmpty(children)) {
                    temp.addAll(children);
                }
            }
            layer.clear();
            layer.addAll(temp);
        }
        return layer;
    }

    private List<TreeItem<FlowNode>> findInTree(String text) {
        ObservableList<TreeItem<FlowNode>> children = root.getChildren();
        Queue<TreeItem<FlowNode>> queue = new LinkedList<>(children);
        List<TreeItem<FlowNode>> list = new LinkedList<>();
        while (!queue.isEmpty()) {
            TreeItem<FlowNode> treeItem = queue.poll();
            ObservableList<TreeItem<FlowNode>> itemChildren = treeItem.getChildren();
            if (!CollectionUtils.isEmpty(itemChildren)) {
                queue.addAll(itemChildren);
            } else {
                FlowNode node = treeItem.getValue();
                if (node.getType().equals(EnumFlowType.TARGET) && node.getCurrentUrl().contains(text)) {
                    list.add(treeItem);
                }
            }
        }
        return list;
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

    private Node loadIcon(int status, String contentType) {
        return loadIcon(status, contentType, true);
    }

    private Node loadIcon(int status, String contentType, boolean coloring) {
        if (status == -1) {
            ImageView imageView = new ImageView(blackLoadingIcon);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            return imageView;
        }
        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
            if (null == contentType) {
                return fontAwesome.create(FontAwesome.Glyph.LINK);
            }
            if (contentType.startsWith(ContentType.TEXT_CSS)) {
                Glyph glyph = fontAwesome.create(FontAwesome.Glyph.CSS3);
                if (coloring) {
                    Color color = Color.web("#3077b8");
                    glyph.color(color);
                    glyph.setUserData(color);
                }
                return glyph;
            } else if (contentType.startsWith(ContentType.TEXT_XML) || contentType.startsWith(ContentType.APPLICATION_XML)) {
                return fontAwesome.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith(ContentType.TEXT_PLAIN)) {
                return fontAwesome.create(FontAwesome.Glyph.FILE_TEXT_ALT);
            } else if (contentType.startsWith(ContentType.APPLICATION_JAVASCRIPT)) {
                return fontAwesome.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith(ContentType.TEXT_HTML)) {
                Glyph glyph = fontAwesome.create(FontAwesome.Glyph.HTML5);
                if (coloring) {
                    Color color = Color.web("#d65a26");
                    glyph.color(color);
                    glyph.setUserData(color);
                }
                return glyph;
            } else if (contentType.startsWith(ContentType.APPLICATION_JSON)) {
                return fontAwesome.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith("image/")) {
                return fontAwesome.create(FontAwesome.Glyph.PHOTO);
            }
        } else if (status >= HttpStatus.SC_MULTIPLE_CHOICES && status < HttpStatus.SC_BAD_REQUEST) {
            Glyph glyph = fontAwesome.create(FontAwesome.Glyph.SHARE);
            if (coloring) {
                Color color = Color.web("#f8aa19");
                glyph.color(color);
                glyph.setUserData(color);
            }
            return glyph;
        } else if (status >= HttpStatus.SC_BAD_REQUEST && status < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            return fontAwesome.create(FontAwesome.Glyph.QUESTION_CIRCLE);
        } else if (status >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            return fontAwesome.create(FontAwesome.Glyph.BOMB);
        }
        return fontAwesome.create(FontAwesome.Glyph.LINK);
    }

}
