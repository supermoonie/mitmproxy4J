package com.github.supermoonie.proxy.fx.ui.main.handler;

import com.github.supermoonie.proxy.fx.ui.FlowNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.util.function.Consumer;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class EditMenuItemHandler implements EventHandler<ActionEvent> {

    private final TabPane tabPane;
    private final Tab structureTab;
    private final TreeView<FlowNode> treeView;
    private final ListView<FlowNode> listView;
    private final Consumer<FlowNode> consumer;

    public EditMenuItemHandler(TabPane tabPane, Tab structureTab, TreeView<FlowNode> treeView, ListView<FlowNode> listView, Consumer<FlowNode> consumer) {
        this.tabPane = tabPane;
        this.structureTab = structureTab;
        this.treeView = treeView;
        this.listView = listView;
        this.consumer = consumer;
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
        consumer.accept(node);
    }
}
