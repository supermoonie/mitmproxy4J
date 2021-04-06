package com.github.supermoonie.proxy.fx.ui.main;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.AppPreferences;
import com.github.supermoonie.proxy.fx.Icons;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.constant.KeyEvents;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.ui.ColumnMap;
import com.github.supermoonie.proxy.fx.ui.FlowNode;
import com.github.supermoonie.proxy.fx.ui.KeyValue;
import com.github.supermoonie.proxy.fx.ui.main.factory.ListViewCellFactory;
import com.github.supermoonie.proxy.fx.ui.main.handler.*;
import com.github.supermoonie.proxy.fx.util.ClipboardUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.controlsfx.glyphfont.FontAwesome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @date 2021-02-21
 */
public abstract class MainView implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainView.class);

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
    protected TabPane mainTabPane;
    @FXML
    protected Tab overviewTab;
    @FXML
    protected Tab contentsTab;
    @FXML
    protected TreeTableView<KeyValue> overviewTreeTableView;
    @FXML
    protected TreeTableColumn<KeyValue, String> overviewNameColumn;
    @FXML
    protected TreeTableColumn<KeyValue, String> overviewValueColumn;
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

    protected final TreeItem<KeyValue> overviewRoot = new TreeItem<>(new KeyValue());
    private final TreeItem<FlowNode> root = new TreeItem<>(new FlowNode());
    protected Integer currentRequestId;
    protected TreeItem<FlowNode> treeViewSelectedItem = null;

    protected final ContextMenu treeContextMenu = new ContextMenu();
    protected final MenuItem copyMenuItem = new MenuItem("Copy URL");
    protected final MenuItem copyResponseMenuItem = new MenuItem("Copy Response");
    protected final MenuItem saveResponseMenuItem = new MenuItem("Save Response");
    protected final MenuItem repeatMenuItem = new MenuItem("Repeat");
    protected final MenuItem editMenuItem = new MenuItem("Edit");
    protected final MenuItem blockMenuItem = new MenuItem("Block List");
    protected final MenuItem allowMenuItem = new MenuItem("Allow List");

    protected final ContextMenu listContextMenu = new ContextMenu();
    protected final MenuItem copyLiMenuItem = new MenuItem("Copy URL");
    protected final MenuItem copyLiResponseMenuItem = new MenuItem("Copy Response");
    protected final MenuItem saveLiResponseMenuItem = new MenuItem("Save Response");
    protected final MenuItem repeatLiMenuItem = new MenuItem("Repeat");
    protected final MenuItem editLiMenuItem = new MenuItem("Edit");
    protected final MenuItem blockLiMenuItem = new MenuItem("Block List");
    protected final MenuItem allowLiMenuItem = new MenuItem("Allow List");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blockListMenuItem.setSelected(AppPreferences.getState().getBoolean(AppPreferences.KEY_BLOCK_LIST_ENABLE, AppPreferences.DEFAULT_BLOCK_LIST_ENABLE));
        allowListMenuItem.setSelected(AppPreferences.getState().getBoolean(AppPreferences.KEY_ALLOW_LIST_ENABLE, AppPreferences.DEFAULT_ALLOW_LIST_ENABLE));
        initToolBar();
        initTreeView();
        initListView();
        initWebview(responseJsonWebView);
        initOverviewTreeTableView();
        initContextMenu();
        responseTabPane.getTabs().remove(responseImageTab);
        responseTabPane.getTabs().remove(responseContentTab);
        Platform.runLater(() -> filterTextField.requestFocus());
    }

    /**
     * 初始化 overview
     */
    private void initOverviewTreeTableView() {
        overviewNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getKey()));
        overviewValueColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getValue()));
        overviewRoot.setExpanded(true);
        overviewTreeTableView.setShowRoot(false);
        overviewTreeTableView.setRoot(overviewRoot);
        overviewTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * 初始化webview
     *
     * @param webView webview
     */
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

    /**
     * 初始化树
     */
    private void initTreeView() {
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.setContextMenu(treeContextMenu);
        treeView.contextMenuProperty().bind(Bindings.when(treeView.getSelectionModel().selectedItemProperty().isNull()).then((ContextMenu) null).otherwise(treeContextMenu));
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onTreeItemClicked);
        treeView.setOnKeyPressed(new TreeItemCopyHandler(tabPane, structureTab, treeView, listView));
        treeView.getSelectionModel().selectedItemProperty().addListener(this::onTreeItemSelected);
    }

    /**
     * 初始化列表
     */
    private void initListView() {
        listView.setCellFactory(new ListViewCellFactory());
        listView.setContextMenu(listContextMenu);
        listView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onListItemClicked);
//        listView.setOnKeyPressed(new FlowNodeKeyEventHandler(tabPane, structureTab, treeView, listView));
    }

    /**
     * 初始化工具栏
     */
    private void initToolBar() {
        ImageView clearIconView = new ImageView(Icons.CLEAR_ICON);
        clearIconView.setFitHeight(12);
        clearIconView.setFitWidth(12);
        clearButton.setGraphic(clearIconView);
        editButton.setGraphic(Icons.FONT_AWESOME.create(FontAwesome.Glyph.EDIT));
        repeatButton.setGraphic(Icons.FONT_AWESOME.create(FontAwesome.Glyph.REPEAT));
        ImageView throttlingImageView = new ImageView(Icons.GRAY_DOT_ICON);
        throttlingImageView.setFitWidth(12);
        throttlingImageView.setFitHeight(12);
        throttlingSwitchButton.setGraphic(throttlingImageView);
        ImageView recordingImageView = new ImageView(Icons.GREEN_DOT_ICON);
        recordingImageView.setFitWidth(12);
        recordingImageView.setFitHeight(12);
        recordingSwitchButton.setGraphic(recordingImageView);
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
//        repeatLiMenuItem.setOnAction(event -> onRepeatButtonClicked());
//        editLiMenuItem.setOnAction(new EditMenuItemHandler(tabPane, structureTab, treeView, listView, node -> onSendRequestMenuItemClicked().setRequestId(node.getId())));
        blockLiMenuItem.setOnAction(new AddBlockListMenuItemHandler(tabPane, structureTab, treeView, listView));
        allowLiMenuItem.setOnAction(new AddAllowListMenuItemHandler(tabPane, structureTab, treeView, listView));
    }

    /**
     * tree item clicked
     *
     * @param event event
     */
    protected abstract void onTreeItemClicked(MouseEvent event);

    /**
     * tree item selected
     *
     * @param observable observable
     * @param oldValue   old
     * @param newValue   new
     */
    protected abstract void onTreeItemSelected(ObservableValue<? extends TreeItem<FlowNode>> observable, TreeItem<FlowNode> oldValue, TreeItem<FlowNode> newValue);

    /**
     * list item clicked
     *
     * @param event e
     */
    protected abstract void onListItemClicked(MouseEvent event);

    public void addFlow(ConnectionInfo connectionInfo, Request request, Response response) throws URISyntaxException {
        FlowNode flowNode = new FlowNode();
        flowNode.setId(request.getId());
        flowNode.setType(EnumFlowType.TARGET);
        flowNode.setUrl(connectionInfo.getUrl());
        flowNode.setRequestTime(request.getTimeCreated());
        if (null == response) {
            // 请求未响应的情况
            flowNode.setStatus(-1);
            addTreeNode(flowNode);
            listView.getItems().add(flowNode);
        } else {
            flowNode.setStatus(response.getStatus());
            flowNode.setContentType(response.getContentType());
            updateTreeItem(flowNode);
            updateListItem(flowNode);
        }
        if (null != treeViewSelectedItem) {
            treeView.getSelectionModel().select(treeViewSelectedItem);
        }
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
            item.setExpanded(root.getChildren().isEmpty());
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
            rootPathTreeItem.setExpanded(overviewRoot.getChildren().isEmpty());
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

    /**
     * 更新树
     *
     * @param flowNode node
     * @throws URISyntaxException e
     */
    public void updateTreeItem(FlowNode flowNode) throws URISyntaxException {
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
            if (!itemChildren.isEmpty()) {
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

    /**
     * 更新列表
     *
     * @param flowNode node
     */
    public void updateListItem(FlowNode flowNode) {
        ObservableList<FlowNode> items = listView.getItems();
        items.stream()
                .filter(item -> item.getId().equals(flowNode.getId()))
                .findFirst()
                .ifPresent(item -> {
                    item.setStatus(flowNode.getStatus());
                    item.setContentType(flowNode.getContentType());
                    listView.refresh();
                });

    }
}
