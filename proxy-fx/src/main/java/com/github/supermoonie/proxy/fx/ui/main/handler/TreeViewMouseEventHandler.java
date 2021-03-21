package com.github.supermoonie.proxy.fx.ui.main.handler;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class TreeViewMouseEventHandler implements EventHandler<MouseEvent> {

    private final TreeView<FlowNode> treeView;
    private final Consumer<FlowNode> selectedNodeConsumer;

    public TreeViewMouseEventHandler(TreeView<FlowNode> treeView, Consumer<FlowNode> selectedNodeConsumer) {
        this.treeView = treeView;
        this.selectedNodeConsumer = selectedNodeConsumer;
    }

    @Override
    public void handle(MouseEvent event) {
        TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (null == selectedItem) {
            return;
        }
        FlowNode selectedNode = selectedItem.getValue();
        if (null == selectedNode) {
            return;
        }
        if (!selectedNode.getType().equals(EnumFlowType.TARGET)) {
            return;
        }
        selectedNodeConsumer.accept(selectedNode);
    }
}
