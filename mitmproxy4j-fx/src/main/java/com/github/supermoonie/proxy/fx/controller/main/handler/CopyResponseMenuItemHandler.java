package com.github.supermoonie.proxy.fx.controller.main.handler;

import com.github.supermoonie.proxy.fx.controller.FlowNode;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.util.ClipboardUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class CopyResponseMenuItemHandler implements EventHandler<ActionEvent> {

    private final TabPane tabPane;
    private final Tab structureTab;
    private final TreeView<FlowNode> treeView;
    private final ListView<FlowNode> listView;

//    private final RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
//    private final ResponseMapper responseMapper = ApplicationContextUtil.getBean(ResponseMapper.class);
//    private final ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);

    public CopyResponseMenuItemHandler(TabPane tabPane, Tab structureTab, TreeView<FlowNode> treeView, ListView<FlowNode> listView) {
        this.tabPane = tabPane;
        this.structureTab = structureTab;
        this.treeView = treeView;
        this.listView = listView;
    }

    @Override
    public void handle(ActionEvent event) {
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

//        Request request = requestMapper.selectById(node.getId());
//        QueryWrapper<Response> resQuery = new QueryWrapper<>();
//        resQuery.eq("request_id", request.getId());
//        Response response = responseMapper.selectOne(resQuery);
//        String contentType = response.getContentType();
//        Content content = contentMapper.selectById(response.getContentId());
//        if (null != content && null != content.getContent() && content.getContent().length > 0) {
//            if (contentType.startsWith("image/")) {
//                ClipboardUtil.copyImage(new Image(new ByteArrayInputStream(content.getContent())));
//            } else {
//                // TODO contentType 与 文件映射
//                ClipboardUtil.copyText(new String(content.getContent(), StandardCharsets.UTF_8));
//            }
//        } else {
//            ClipboardUtil.copyText("");
//        }
    }
}
