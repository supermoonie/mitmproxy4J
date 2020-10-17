package com.github.supermoonie.proxy.fx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
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
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import com.github.supermoonie.proxy.fx.util.ClipboardUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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

    private final RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
    private final HeaderMapper headerMapper = ApplicationContextUtil.getBean(HeaderMapper.class);
    private final ResponseMapper responseMapper = ApplicationContextUtil.getBean(ResponseMapper.class);
    private final ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);

    private final KeyCodeCombination macKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);
    private final KeyCodeCombination winKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

    private final Image xmlIcon = new Image(getClass().getResourceAsStream("/icon/xml.png"), 16, 16, false, true);
    private final Image webIcon = new Image(getClass().getResourceAsStream("/icon/web.png"), 16, 16, false, false);
    private final Image textIcon = new Image(getClass().getResourceAsStream("/icon/text.png"), 16, 16, false, false);
    private final Image jsonIcon = new Image(getClass().getResourceAsStream("/icon/json.png"), 16, 16, false, false);
    private final Image jsIcon = new Image(getClass().getResourceAsStream("/icon/js.png"), 16, 16, false, false);
    private final Image htmlIcon = new Image(getClass().getResourceAsStream("/icon/html.png"), 16, 16, false, false);
    private final Image folderIcon = new Image(getClass().getResourceAsStream("/icon/folder.png"), 16, 16, false, false);
    private final Image errorIcon = new Image(getClass().getResourceAsStream("/icon/error.png"), 16, 16, false, false);
    private final Image cssIcon = new Image(getClass().getResourceAsStream("/icon/css.png"), 16, 16, false, false);
    private final Image icon404 = new Image(getClass().getResourceAsStream("/icon/404.png"), 16, 16, false, false);
    private final Image linkIcon = new Image(getClass().getResourceAsStream("/icon/link.png"), 16, 16, false, false);

    private String currentRequestId;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    private void clear() {
        requestHeaderTableView.getItems().clear();
        responseHeaderTableView.getItems().clear();
        requestRawTextArea.clear();
        responseRawTextArea.clear();
        responseTextArea.clear();
        formTableView.getItems().clear();
        queryTableView.getItems().clear();
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
                    String[] params = raw.split("&");
                    for (String param : params) {
                        String[] form = param.split("=");
                        if (form.length == 1) {
                            formTableView.getItems().add(new ColumnMap(form[0], ""));
                        } else if (form.length == 2) {
                            formTableView.getItems().add(new ColumnMap(form[0], form[1]));
                        }
                        requestTabPane.getTabs().add(1, requestFormTab);
                    }
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
                String[] params = query.split("&");
                for (String param : params) {
                    String[] form = param.split("=");
                    if (form.length == 1) {
                        queryTableView.getItems().add(new ColumnMap(form[0], ""));
                    } else if (form.length == 2) {
                        queryTableView.getItems().add(new ColumnMap(form[0], form[1]));
                    }
                    requestTabPane.getTabs().add(1, requestQueryTab);
                }
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
            Request request = requestMapper.selectById(selectedNode.getId());
            infoLabel.setText(request.getUri());
            QueryWrapper<Header> requestHeaderQueryWrapper = new QueryWrapper<>();
            requestHeaderQueryWrapper.eq("request_id", request.getId());
            List<Header> requestHeaders = headerMapper.selectList(requestHeaderQueryWrapper);
            requestHeaderTableView.getItems().addAll(requestHeaders);
            QueryWrapper<Response> responseQueryWrapper = new QueryWrapper<>();
            responseQueryWrapper.eq("request_id", request.getId());
            Response response = responseMapper.selectOne(responseQueryWrapper);
            QueryWrapper<Header> responseHeaderQueryWrapper = new QueryWrapper<>();
            responseHeaderQueryWrapper.eq("response_id", response.getId());
            List<Header> responseHeaders = headerMapper.selectList(responseHeaderQueryWrapper);
            responseHeaderTableView.getItems().addAll(responseHeaders);
            fillRequestRawTab(request, requestHeaders);
            fillRequestQueryTab(request);
            fillResponseRawTab(response, responseHeaders);
        }
    }

    public void addFlow(Request request, Response response) throws URISyntaxException {
        FlowNode rootNode = new FlowNode();
        rootNode.setStatus(response.getStatus());
        rootNode.setType(EnumFlowType.TARGET);
        rootNode.setUrl(request.getUri());
        rootNode.setId(request.getId());
        listView.getItems().add(rootNode);
        URI uri = new URI(request.getUri());
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
            rootPathNode.setId(request.getId());
            rootPathNode.setUrl("/");
            rootPathNode.setType(EnumFlowType.TARGET);
            TreeItem<FlowNode> rootPathTreeItem = new TreeItem<>(rootPathNode, new ImageView(loadIcon(response)));
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
                    node.setId(request.getId());
                    node.setUrl(fragment);
                    node.setType(EnumFlowType.TARGET);
                    node.setStatus(response.getStatus());
                    currentItem.getChildren().add(new TreeItem<>(node, new ImageView(loadIcon(response))));
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

    private void appendTab(TabPane tabPane, Tab tab) {
        FilteredList<Tab> list = tabPane.getTabs().filtered(item -> item.getText().equals(tab.getText()));
        if (CollectionUtils.isEmpty(list)) {
            tabPane.getTabs().add(tabPane.getTabs().size(), tab);
        }
    }

    private Image loadIcon(Response response) {
        Integer status = response.getStatus();
        if (200 == status) {
            if (response.getContentType().startsWith(ContentType.TEXT_CSS)) {
                return cssIcon;
            } else if (response.getContentType().startsWith(ContentType.TEXT_XML) || response.getContentType().startsWith(ContentType.APPLICATION_XML)) {
                return xmlIcon;
            } else if (response.getContentType().startsWith(ContentType.TEXT_PLAIN)) {
                return textIcon;
            } else if (response.getContentType().startsWith(ContentType.APPLICATION_JAVASCRIPT)) {
                return jsIcon;
            } else if (response.getContentType().startsWith(ContentType.TEXT_HTML)) {
                return htmlIcon;
            } else if (response.getContentType().startsWith(ContentType.APPLICATION_JSON)) {
                return jsonIcon;
            }
            return linkIcon;
        } else if (404 == status) {
            return icon404;
        } else {
            return errorIcon;
        }
    }

}
