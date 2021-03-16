package com.github.supermoonie.proxy.fx.controller.main;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.AppPreferences;
import com.github.supermoonie.proxy.fx.Icons;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.constant.KeyEvents;
import com.github.supermoonie.proxy.fx.controller.ColumnMap;
import com.github.supermoonie.proxy.fx.controller.FlowNode;
import com.github.supermoonie.proxy.fx.controller.PropertyPair;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.util.ClipboardUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.glyphfont.FontAwesome;

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

    protected final TreeItem<PropertyPair> overviewRoot = new TreeItem<>(new PropertyPair());
    private final TreeItem<FlowNode> root = new TreeItem<>(new FlowNode());
    protected Integer currentRequestId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blockListMenuItem.setSelected(AppPreferences.getState().getBoolean(AppPreferences.KEY_BLOCK_LIST_ENABLE, AppPreferences.DEFAULT_BLOCK_LIST_ENABLE));
        allowListMenuItem.setSelected(AppPreferences.getState().getBoolean(AppPreferences.KEY_ALLOW_LIST_ENABLE, AppPreferences.DEFAULT_ALLOW_LIST_ENABLE));
        initToolBar();
        initTreeView();
        initWebview(responseJsonWebView);
        initOverviewTreeTableView();
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

    private void initTreeView() {
//        ContextMenu treeContextMenu = new ContextMenu();
        treeView.setRoot(root);
        treeView.setShowRoot(false);
//        treeView.setContextMenu(treeContextMenu);
//        treeView.contextMenuProperty().bind(Bindings.when(treeView.getSelectionModel().selectedItemProperty().isNull()).then((ContextMenu) null).otherwise(treeContextMenu));
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onTreeViewClicked);
//        treeView.setOnKeyPressed(new FlowNodeKeyEventHandler(tabPane, structureTab, treeView, listView));
//        treeView.focusedProperty().addListener(new TreeViewFocusListener(treeView));
//        treeView.getSelectionModel().selectedItemProperty().addListener(new TreeViewSelectListener());
    }

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

    /**
     * tree clicked
     *
     * @param event event
     */
    protected abstract void onTreeViewClicked(MouseEvent event);

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
        } else {
            flowNode.setStatus(response.getStatus());
            flowNode.setContentType(response.getContentType());
            updateTreeItem(flowNode);
//            if (null != currentRequestId && currentRequestId.equals(request.getId())) {
//                QueryWrapper<Header> responseHeaderQueryWrapper = new QueryWrapper<>();
//                responseHeaderQueryWrapper.eq("response_id", response.getId());
//                List<Header> responseHeaders = headerMapper.selectList(responseHeaderQueryWrapper);
//                if (!CollectionUtils.isEmpty(responseHeaders)) {
//                    responseHeaderTableView.getItems().addAll(responseHeaders);
//                }
//                fillResponseRawTab(response, responseHeaders);
//                fillOverviewTab(flowNode);
//            }
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
}
