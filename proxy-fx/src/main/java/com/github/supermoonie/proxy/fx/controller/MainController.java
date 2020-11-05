package com.github.supermoonie.proxy.fx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.controller.dialog.ProxySettingDialog;
import com.github.supermoonie.proxy.fx.controller.dialog.ThrottlingSettingDialog;
import com.github.supermoonie.proxy.fx.dto.ColumnMap;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.mapper.ContentMapper;
import com.github.supermoonie.proxy.fx.mapper.HeaderMapper;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.mapper.ResponseMapper;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.service.FlowService;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.support.Flow;
import com.github.supermoonie.proxy.fx.support.HexContentFlow;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import com.github.supermoonie.proxy.fx.util.ClipboardUtil;
import com.github.supermoonie.proxy.fx.util.JSON;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
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
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
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
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

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
    protected AnchorPane structurePane;
    @FXML
    protected AnchorPane sequencePane;
    @FXML
    protected TreeView<FlowNode> treeView;
    @FXML
    protected ListView<FlowNode> listView;
    @FXML
    protected Label infoLabel;
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

    private final RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
    private final HeaderMapper headerMapper = ApplicationContextUtil.getBean(HeaderMapper.class);
    private final ResponseMapper responseMapper = ApplicationContextUtil.getBean(ResponseMapper.class);
    private final ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);
    private final FlowService flowService = ApplicationContextUtil.getBean(FlowService.class);

    private final KeyCodeCombination macKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);
    private final KeyCodeCombination winKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

    private final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    private final Image webIcon = new Image(getClass().getResourceAsStream("/icon/web.png"), 16, 16, false, false);
    private final Image folderIcon = new Image(getClass().getResourceAsStream("/icon/folder.png"), 16, 16, false, false);
    private final Image clearIcon = new Image(getClass().getResourceAsStream("/icon/clear.png"), 16, 16, false, false);
    private final Image editIcon = new Image(getClass().getResourceAsStream("/icon/edit.png"), 16, 16, false, false);
    private final Image repeatIcon = new Image(getClass().getResourceAsStream("/icon/repeat.png"), 16, 16, false, false);
    private final Image grayDotIcon = new Image(getClass().getResourceAsStream("/icon/dot_gray.png"), 16, 16, false, false);
    private final Image greenDotIcon = new Image(getClass().getResourceAsStream("/icon/dot_green.png"), 16, 16, false, false);

    private ObservableList<FlowNode> allNode;
    private String currentRequestId;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initToolBar();
        initRecordSetting();
        initThrottlingSetting();
        TreeItem<FlowNode> root = new TreeItem<>(new FlowNode());
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            FlowNode selectedNode = selectedItem.getValue();
            fillMainView(selectedNode);
        });
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            FlowNode selectedItem = listView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            fillMainView(selectedItem);
        });
        requestTabPane.getTabs().removeIf(tab -> tab.getText().equals(requestFormTab.getText()));
        requestTabPane.getTabs().removeIf(tab -> tab.getText().equals(requestQueryTab.getText()));
        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseImageTab.getText()));
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
        ImageView editIconView = new ImageView(editIcon);
        editIconView.setFitWidth(12);
        editIconView.setFitHeight(12);
        editButton.setGraphic(editIconView);
        ImageView repeatIconView = new ImageView(repeatIcon);
        repeatIconView.setFitWidth(12);
        repeatIconView.setFitHeight(12);
        repeatButton.setGraphic(repeatIconView);
    }

    private void initRecordSetting() {
        recordMenuItem.setSelected(GlobalSetting.getInstance().isRecord());
        ImageView imageView = new ImageView(GlobalSetting.getInstance().isRecord() ? greenDotIcon : grayDotIcon);
        imageView.setFitWidth(12);
        imageView.setFitHeight(12);
        recordingSwitchButton.setGraphic(imageView);
        GlobalSetting.getInstance().recordProperty().addListener((observable, oldValue, newValue) -> {
            recordMenuItem.setSelected(newValue);
            ImageView view = new ImageView(newValue ? greenDotIcon : grayDotIcon);
            view.setFitWidth(12);
            view.setFitHeight(12);
            recordingSwitchButton.setGraphic(view);
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
            ImageView view = new ImageView(newValue ? greenDotIcon : grayDotIcon);
            view.setFitWidth(12);
            view.setFitHeight(12);
            throttlingSwitchButton.setGraphic(view);
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

    public void onThrottlingSettingMenuItemClicked() {
        Stage throttlingSettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/ThrottlingSettingDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            ThrottlingSettingDialog throttlingSettingDialog = fxmlLoader.getController();
            throttlingSettingDialog.setStage(throttlingSettingStage);
            throttlingSettingStage.setScene(new Scene(parent));
            App.setCommonIcon(throttlingSettingStage, "Lightning");
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
                addFlow(flow.getRequest(), flow.getResponse());
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
                log.info("path: " + filePath);
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
        clear();
        currentRequestId = null;
        treeView.getRoot().getChildren().clear();
        listView.getItems().clear();
        if (null != allNode) {
            allNode.clear();
        }
        infoLabel.setText("");
    }

    public void onFilterTextFieldEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (null == allNode || allNode.size() < listView.getItems().size()) {
                allNode = new FilteredList<>(listView.getItems(), p -> true);
            }
            String text = filterTextField.getText();
            if (!StringUtils.isEmpty(text)) {
                ObservableList<FlowNode> filterList = FXCollections.observableList(new LinkedList<>());
                allNode.forEach(node -> {
                    if (node.getUrl().contains(text)) {
                        filterList.add(node);
                    }
                });
                listView.setItems(filterList);
            } else {
                listView.setItems(allNode);
            }
            ObservableList<FlowNode> items = listView.getItems();
            try {
                setAllTreeNode(items);
            } catch (URISyntaxException e) {
                log.error(e.getMessage(), e);
            }
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
        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseImageTab.getText()));
        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
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

    private void fillMainView(FlowNode selectedNode) {
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
            responseHeaderTableView.getItems().addAll(responseHeaders);
            fillRequestRawTab(request, requestHeaders);
            fillRequestQueryTab(request);
            fillResponseRawTab(response, responseHeaders);
        }
    }

    private Flow getFlow(String requestId) {
        Request request = requestMapper.selectById(requestId);
        QueryWrapper<Header> requestHeaderQueryWrapper = new QueryWrapper<>();
        requestHeaderQueryWrapper.eq("request_id", request.getId());
        List<Header> requestHeaders = headerMapper.selectList(requestHeaderQueryWrapper);
        QueryWrapper<Response> responseQueryWrapper = new QueryWrapper<>();
        responseQueryWrapper.eq("request_id", request.getId());
        Response response = responseMapper.selectOne(responseQueryWrapper);
        QueryWrapper<Header> responseHeaderQueryWrapper = new QueryWrapper<>();
        responseHeaderQueryWrapper.eq("response_id", response.getId());
        List<Header> responseHeaders = headerMapper.selectList(responseHeaderQueryWrapper);
        Flow flow = new Flow();
        flow.setRequest(request);
        flow.setRequestHeaders(requestHeaders);
        flow.setResponse(response);
        flow.setResponseHeaders(responseHeaders);
        return flow;
    }

    private void setAllTreeNode(ObservableList<FlowNode> allListNode) throws URISyntaxException {
        treeView.getRoot().getChildren().clear();
        for (FlowNode flowNode : allListNode) {
            addTreeNode(flowNode);
        }
    }

    private void addTreeNode(FlowNode flowNode) throws URISyntaxException {
        URI uri = new URI(flowNode.getUrl());
        String baseUri = uri.getScheme() + "://" + uri.getAuthority();
        TreeItem<FlowNode> root = treeView.getRoot();
        ObservableList<TreeItem<FlowNode>> children = root.getChildren();
        TreeItem<FlowNode> baseNodeTreeItem = children.stream().filter(item -> item.getValue().getUrl().equals(baseUri)).findFirst().orElseGet(() -> {
            FlowNode baseNode = new FlowNode();
            baseNode.setUrl(baseUri);
            baseNode.setType(EnumFlowType.BASE_URL);
            TreeItem<FlowNode> item = new TreeItem<>(baseNode, new ImageView(webIcon));
            item.setExpanded(true);
            root.getChildren().add(item);
            return item;
        });
        if ("".equals(uri.getPath()) || "/".equals(uri.getPath())) {
            FlowNode rootPathNode = new FlowNode();
            rootPathNode.setId(flowNode.getId());
            rootPathNode.setUrl("/");
            rootPathNode.setType(EnumFlowType.TARGET);
            TreeItem<FlowNode> rootPathTreeItem = new TreeItem<>(rootPathNode, loadIcon(flowNode.getStatus(), flowNode.getContentType()));
            rootPathTreeItem.setExpanded(true);
            baseNodeTreeItem.getChildren().add(rootPathTreeItem);
        } else {
            String[] array = (uri.getPath() + " ").split("/");
            int len = array.length;
            TreeItem<FlowNode> currentItem = baseNodeTreeItem;
            for (int i = 1; i < len; i++) {
                String fragment = "".equals(array[i].trim()) ? "/" : array[i].trim();
                if (i == (len - 1)) {
                    FlowNode node = new FlowNode();
                    node.setId(flowNode.getId());
                    node.setUrl(fragment);
                    node.setType(EnumFlowType.TARGET);
                    node.setStatus(flowNode.getStatus());
                    currentItem.getChildren().add(new TreeItem<>(node, loadIcon(flowNode.getStatus(), flowNode.getContentType())));
                } else {
                    ObservableList<TreeItem<FlowNode>> treeItems = currentItem.getChildren();
                    currentItem = treeItems.stream().filter(item -> item.getValue().getUrl().equals(fragment) && item.getValue().getType().equals(EnumFlowType.PATH))
                            .findFirst().orElseGet(() -> {
                                FlowNode node = new FlowNode();
                                node.setUrl(fragment);
                                node.setType(EnumFlowType.PATH);
                                TreeItem<FlowNode> treeItem = new TreeItem<>(node, new ImageView(folderIcon));
                                treeItems.add(treeItem);
                                return treeItem;
                            });
                }
                currentItem.setExpanded(true);
            }
        }
    }

    public void addFlow(Request request, Response response) throws URISyntaxException {
        FlowNode flowNode = new FlowNode();
        flowNode.setStatus(response.getStatus());
        flowNode.setType(EnumFlowType.TARGET);
        flowNode.setUrl(request.getUri());
        flowNode.setId(request.getId());
        flowNode.setContentType(response.getContentType());
        listView.getItems().add(flowNode);
        addTreeNode(flowNode);
    }

    private void appendTab(TabPane tabPane, Tab tab) {
        FilteredList<Tab> list = tabPane.getTabs().filtered(item -> item.getText().equals(tab.getText()));
        if (CollectionUtils.isEmpty(list)) {
            tabPane.getTabs().add(tabPane.getTabs().size(), tab);
        }
    }

    private Node loadIcon(int status, String contentType) {
        if (HttpStatus.SC_OK == status) {
            if (contentType.startsWith(ContentType.TEXT_CSS)) {
                return fontAwesome.create(FontAwesome.Glyph.CSS3);
            } else if (contentType.startsWith(ContentType.TEXT_XML) || contentType.startsWith(ContentType.APPLICATION_XML)) {
                return fontAwesome.create(FontAwesome.Glyph.FILE_CODE_ALT);
            } else if (contentType.startsWith(ContentType.TEXT_PLAIN)) {
                return fontAwesome.create(FontAwesome.Glyph.FILE_TEXT_ALT);
            } else if (contentType.startsWith(ContentType.APPLICATION_JAVASCRIPT)) {
                return fontAwesome.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith(ContentType.TEXT_HTML)) {
                return fontAwesome.create(FontAwesome.Glyph.HTML5);
            } else if (contentType.startsWith(ContentType.APPLICATION_JSON)) {
                return fontAwesome.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith("image/")) {
                return fontAwesome.create(FontAwesome.Glyph.PHOTO);
            }
        } else if (HttpStatus.SC_NOT_FOUND == status) {
            Glyph glyph = fontAwesome.create(FontAwesome.Glyph.QUESTION_CIRCLE);
            glyph.setColor(Color.web("#f8aa19"));
            return glyph;
        } else if (HttpStatus.SC_INTERNAL_SERVER_ERROR == status){
            return fontAwesome.create(FontAwesome.Glyph.BOMB);
        }
        return fontAwesome.create(FontAwesome.Glyph.LINK);
    }

}
