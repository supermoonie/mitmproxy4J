package com.github.supermoonie.proxy.fx.ui.main.listener;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.Glyph;

import java.util.Objects;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class TreeViewFocusListener implements ChangeListener<Boolean> {

    private final TreeView<FlowNode> treeView;

    public TreeViewFocusListener(TreeView<FlowNode> treeView) {
        this.treeView = treeView;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (null == selectedItem) {
            return;
        }
        Node node = selectedItem.getGraphic();
        FlowNode flowNode = selectedItem.getValue();
        if (!newValue) {
            if (flowNode.getType().equals(EnumFlowType.TARGET) && node instanceof Glyph) {
                Glyph glyph = (Glyph) node;
                glyph.setColor((Color) Objects.requireNonNullElse(glyph.getUserData(), Color.BLACK));
            }
        } else {
            if (flowNode.getType().equals(EnumFlowType.TARGET) && node instanceof Glyph) {
                Glyph glyph = (Glyph) node;
                glyph.setColor(Color.WHITE);
            }
        }
    }
}
