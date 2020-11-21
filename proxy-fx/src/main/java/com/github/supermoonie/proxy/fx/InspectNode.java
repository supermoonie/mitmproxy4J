package com.github.supermoonie.proxy.fx;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;

/**
 * @author LYG
 * 检查元素
 */
public class InspectNode {
    private final EventHandler<? super MouseEvent> eventFilter;
    Stage stage;
    NumberFormat nf = new DecimalFormat("#.##");
    /**
     * 悬浮框窗口
     */
    PopupWindow overlay = new PopupWindow() {
    };
    /**
     * 悬浮框
     */
    Pane overlayPane = new Pane();
    /**
     * 属性显示Window
     */
    PropertyWindow propertyOverlay = new PropertyWindow();

    public InspectNode(Stage scene) {
        this.stage = scene;
        overlay.getScene().setRoot(overlayPane);
        overlayPane.setStyle("-fx-border-color:red; -fx-border-width:1;");

        // 鼠标过滤器事件
        eventFilter = event -> {

        };
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, this::handle);
    }

    private void handle(MouseEvent event) {
        EventTarget target = event.getTarget();
        if (target instanceof Node) {
            Node node = (Node) target;
            Bounds boundsScreen = node.localToScreen(node.getLayoutBounds());
            Bounds boundsParent = node.localToParent(node.getLayoutBounds());

            // 根据node尺寸设置悬浮框尺寸
            overlayPane.setPrefSize(boundsScreen.getWidth(), boundsScreen.getHeight());
            // 根据node位置显示悬浮窗口
            overlay.show(node, boundsScreen.getMinX(), boundsScreen.getMinY());

            // 根据node位置显示属性Window
            propertyOverlay.show(node, boundsScreen.getMinX(), boundsScreen.getMaxY());

            // 设置属性Window文字内容
            propertyOverlay.setText("Name: " + node.getClass().getName() + "\n" +
                    "Size: " + nf.format(boundsScreen.getWidth()) + " x " + nf.format(boundsScreen.getHeight()) + "\n" +
                    "LocalInParent: (" + nf.format(boundsParent.getMinX()) + "," + nf.format(boundsParent.getMinY()) + ") (" + nf.format(boundsParent.getMaxX()) + "," + nf.format(boundsParent.getMaxY()) + ")\n" +
                    "LocalInScreen: (" + nf.format(boundsScreen.getMinX()) + "," + nf.format(boundsScreen.getMinY()) + ") (" + nf.format(boundsScreen.getMaxX()) + "," + nf.format(boundsScreen.getMaxY()) + ")\n"
            );
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        stage.removeEventFilter(MouseEvent.MOUSE_MOVED, eventFilter);
    }

    /**
     * 属性显示Window
     */
    static class PropertyWindow extends PopupWindow {
        Label textLabel = new Label();

        public PropertyWindow() {
            textLabel.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white; -fx-border-color: black; -fx-border-width: 1;-fx-padding: 10");

            getScene().setRoot(textLabel);
        }

        /**
         * 设置文字内容
         *
         * @param text 属性文本
         */
        void setText(String text) {
            textLabel.setText(text);
        }
    }
}
