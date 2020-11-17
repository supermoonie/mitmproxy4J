package com.github.supermoonie.proxy.fx.controller.main.handler;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.util.ClipboardUtil;
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
public class CopyMenuItemHandler implements EventHandler<ActionEvent> {

    private final TabPane tabPane;
    private final Tab structureTab;
    private final TreeView<FlowNode> treeView;
    private final ListView<FlowNode> listView;

    public CopyMenuItemHandler(TabPane tabPane, Tab structureTab, TreeView<FlowNode> treeView, ListView<FlowNode> listView) {
        this.tabPane = tabPane;
        this.structureTab = structureTab;
        this.treeView = treeView;
        this.listView = listView;
    }

    @Override
    public void handle(ActionEvent event) {
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
    }
}
