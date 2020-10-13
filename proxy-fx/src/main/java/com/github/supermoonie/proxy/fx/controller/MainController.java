package com.github.supermoonie.proxy.fx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/9/25
 */
public class MainController implements Initializable {

    @FXML
    protected MenuBar menuBar;

    @FXML
    protected AnchorPane structurePane;

    @FXML
    protected AnchorPane sequencePane;

    @FXML
    protected TreeView<FlowNode> treeView;

    @FXML
    protected ListView<String> listView;

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
    protected Tab requestRawTab;

    @FXML
    protected Tab responseHeaderTab;

    @FXML
    protected Tab responseRawTab;

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

    private String currentRequestId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<FlowNode> root = new TreeItem<>(new FlowNode());
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, treeViewClickedHandler);
        requestTabPane.getTabs().removeIf(tab -> tab.getText().equals(requestFormTab.getText()));
    }

    private final EventHandler<MouseEvent> treeViewClickedHandler = mouseEvent -> {
        TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (null == selectedItem) {
            return;
        }
        FlowNode selectedNode = selectedItem.getValue();
        if (EnumFlowType.TARGET.equals(selectedNode.getType())) {
            if (null != currentRequestId && currentRequestId.equals(selectedNode.getId())) {
                return;
            }
            currentRequestId = selectedNode.getId();
            requestHeaderTableView.getItems().clear();
            responseHeaderTableView.getItems().clear();
            requestRawTextArea.clear();
            responseRawTextArea.clear();
            formTableView.getItems().clear();
            RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
            Request request = requestMapper.selectById(selectedNode.getId());
            infoLabel.setText(request.getUri());
            HeaderMapper headerMapper = ApplicationContextUtil.getBean(HeaderMapper.class);
            QueryWrapper<Header> requestHeaderQueryWrapper = new QueryWrapper<>();
            requestHeaderQueryWrapper.eq("request_id", request.getId());
            List<Header> requestHeaders = headerMapper.selectList(requestHeaderQueryWrapper);
            requestHeaderTableView.getItems().addAll(requestHeaders);
            ResponseMapper responseMapper = ApplicationContextUtil.getBean(ResponseMapper.class);
            QueryWrapper<Response> responseQueryWrapper = new QueryWrapper<>();
            responseQueryWrapper.eq("request_id", request.getId());
            Response response = responseMapper.selectOne(responseQueryWrapper);
            QueryWrapper<Header> responseHeaderQueryWrapper = new QueryWrapper<>();
            responseHeaderQueryWrapper.eq("response_id", response.getId());
            List<Header> responseHeaders = headerMapper.selectList(responseHeaderQueryWrapper);
            responseHeaderTableView.getItems().addAll(responseHeaders);

            StringBuilder requestRawBuilder = new StringBuilder();
            requestRawBuilder.append(request.getMethod()).append(" ").append(request.getUri()).append("\n");
            for (Header header : requestHeaders) {
                requestRawBuilder.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
            }
            ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);
            if (!StringUtils.isEmpty(request.getContentId())) {
                Content content = contentMapper.selectById(request.getContentId());
                byte[] bytes = content.getContent();
                requestRawBuilder.append("\n");
                // TODO charset utf8 gbk gbk2312 iso8859-1
                String raw = new String(bytes);
                requestRawBuilder.append(raw);
                requestHeaders.stream().filter(header -> "Content-Type".equals(header.getName())).findFirst().ifPresent(header -> {
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

            StringBuilder responseRawBuilder = new StringBuilder();
            responseRawBuilder.append("status : ").append(response.getStatus()).append("\n");
            for (Header header : responseHeaders) {
                responseRawBuilder.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
            }
            if (!StringUtils.isEmpty(response.getContentId())) {
                Content content = contentMapper.selectById(response.getContentId());
                byte[] bytes = content.getContent();
                responseRawBuilder.append("\n");
                responseRawBuilder.append(new String(bytes));
            }
            responseRawTextArea.appendText(responseRawBuilder.toString());
        }
    };

    public void addFlow(Request request, Response response) throws URISyntaxException {
        listView.getItems().add(request.getUri());
        URI uri = new URI(request.getUri());
        String baseUri = uri.getScheme() + "://" + uri.getAuthority();
        TreeItem<FlowNode> root = treeView.getRoot();
        ObservableList<TreeItem<FlowNode>> children = root.getChildren();
        TreeItem<FlowNode> baseNodeTreeItem = children.stream().filter(item -> item.getValue().getUrl().equals(baseUri)).findFirst().orElseGet(() -> {
            FlowNode baseNode = new FlowNode();
            baseNode.setUrl(baseUri);
            baseNode.setType(EnumFlowType.BASE_URL);
            TreeItem<FlowNode> item = new TreeItem<>(baseNode);
            root.getChildren().add(item);
            return item;
        });
        if ("".equals(uri.getPath()) || "/".equals(uri.getPath())) {
            FlowNode rootPathNode = new FlowNode();
            rootPathNode.setId(request.getId());
            rootPathNode.setUrl("/");
            rootPathNode.setType(EnumFlowType.TARGET);
            TreeItem<FlowNode> rootPathTreeItem = new TreeItem<>(rootPathNode);
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
                    currentItem.getChildren().add(new TreeItem<>(node));
                } else {
                    ObservableList<TreeItem<FlowNode>> treeItems = currentItem.getChildren();
                    currentItem = treeItems.stream().filter(item -> item.getValue().getUrl().equals(fragment) && item.getValue().getType().equals(EnumFlowType.PATH))
                            .findFirst().orElseGet(() -> {
                                FlowNode node = new FlowNode();
                                node.setUrl(fragment);
                                node.setType(EnumFlowType.PATH);
                                TreeItem<FlowNode> treeItem = new TreeItem<>(node);
                                treeItems.add(treeItem);
                                return treeItem;
                            });
                }
            }
        }
    }

}
