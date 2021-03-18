package com.github.supermoonie.proxy.fx.controller.main.handler;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.controller.FlowNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class AddBlockListMenuItemHandler implements EventHandler<ActionEvent> {

    private final TabPane tabPane;
    private final Tab structureTab;
    private final TreeView<FlowNode> treeView;
    private final ListView<FlowNode> listView;

    public AddBlockListMenuItemHandler(TabPane tabPane, Tab structureTab, TreeView<FlowNode> treeView, ListView<FlowNode> listView) {
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
//            BlockUrl blockUrl = new BlockUrl(true, url);
//            setting.getBlockUrlList().add(blockUrl);
        } else {
            node = listView.getSelectionModel().getSelectedItem();
            if (node == null) {
                return;
            }
//            BlockUrl blockUrl = new BlockUrl(true, node.getUrl());
//            setting.getBlockUrlList().add(blockUrl);
        }
    }
}
