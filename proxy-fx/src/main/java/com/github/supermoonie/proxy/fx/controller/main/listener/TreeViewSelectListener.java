package com.github.supermoonie.proxy.fx.controller.main.listener;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.source.Icons;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.Glyph;

import java.util.Objects;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class TreeViewSelectListener implements ChangeListener<TreeItem<FlowNode>> {

    @Override
    public void changed(ObservableValue<? extends TreeItem<FlowNode>> observable, TreeItem<FlowNode> oldValue, TreeItem<FlowNode> newValue) {
        if (null != oldValue) {
            FlowNode node = oldValue.getValue();
            Node oldNode = oldValue.getGraphic();
            if (node.getType().equals(EnumFlowType.TARGET) && oldNode instanceof Glyph) {
                Glyph glyph = (Glyph) oldNode;
                glyph.setColor((Color) Objects.requireNonNullElse(glyph.getUserData(), Color.BLACK));
            }
            if (-1 == node.getStatus() && node.getType().equals(EnumFlowType.TARGET)) {
                if (oldNode instanceof ImageView) {
                    ImageView imageView = (ImageView) oldNode;
                    imageView.setImage(Icons.BLACK_LOADING_ICON);
                }
            }
        }
        if (null != newValue) {
            FlowNode node = newValue.getValue();
            Node newNode = newValue.getGraphic();
            if (-1 == node.getStatus() && node.getType().equals(EnumFlowType.TARGET)) {
                if (newNode instanceof ImageView) {
                    ImageView imageView = (ImageView) newNode;
                    imageView.setImage(Icons.WHITE_LOADING_ICON);
                }
            }
            if (node.getType().equals(EnumFlowType.TARGET) && newNode instanceof Glyph) {
                Glyph glyph = (Glyph) newNode;
                glyph.setColor(Color.WHITE);
            }
        }
    }
}
