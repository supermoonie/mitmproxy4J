package com.github.supermoonie.proxy.fx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.mapper.HeaderMapper;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.mapper.ResponseMapper;
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
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
    protected Tab requestRawTab;

    @FXML
    protected Tab responseHeaderTab;

    @FXML
    protected Tab responseRawTab;

    @FXML
    protected TableView<Header> requestHeaderTableView;

    @FXML
    public TableColumn<Header, String> requestHeaderNameColumn;

    @FXML
    public TableColumn<Header, String> requestHeaderValueColumn;

    @FXML
    protected TableView<Header> responseHeaderTableView;

    @FXML
    public TableColumn<Header, String> responseHeaderNameColumn;

    @FXML
    public TableColumn<Header, String> responseHeaderValueColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<FlowNode> root = new TreeItem<>(new FlowNode());
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, treeViewClickedHandler);
        requestHeaderNameColumn.setCellFactory(centerCellCallBack);
        requestHeaderValueColumn.setCellFactory(centerCellCallBack);
        responseHeaderNameColumn.setCellFactory(centerCellCallBack);
        responseHeaderValueColumn.setCellFactory(centerCellCallBack);
    }

    private final Callback<TableColumn<Header, String>, TableCell<Header, String>> centerCellCallBack = headerStringTableColumn -> {
        TableCell<Header, String> cell = new TextFieldTableCell<>();
        cell.setAlignment(Pos.CENTER);
        return cell;
    };

    private final EventHandler<MouseEvent> treeViewClickedHandler = mouseEvent -> {
        TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (null == selectedItem) {
            return;
        }
        FlowNode selectedNode = selectedItem.getValue();
        if (EnumFlowType.TARGET.equals(selectedNode.getType())) {
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
