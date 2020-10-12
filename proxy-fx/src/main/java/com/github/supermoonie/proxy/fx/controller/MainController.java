package com.github.supermoonie.proxy.fx.controller;

import com.github.supermoonie.proxy.fx.dto.FlowNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.springframework.cglib.core.CollectionUtils;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(FlowNode entity, boolean empty) {
                super.updateItem(entity, empty);
                PropertyTreeItem<?> item = (PropertyTreeItem<?>) getTreeItem();
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getPropertyValue().toString());
                }
            }
        });
    }

    public static class PropertyTreeItem<T> extends TreeItem<FlowNode> {

        private final T propertyValue ;

        public PropertyTreeItem(FlowNode entity, T value) {
            super(entity);
            this.propertyValue = value ;
        }

        public static PropertyTreeItem<FlowNode> baseItem(FlowNode entity) {
            return new PropertyTreeItem<>(entity, entity);
        }

        public T getPropertyValue() {
            return propertyValue ;
        }
    }

    public void addFlow(List<FlowNode> flow) {
        if (null == flow || flow.size() == 0) {
            return;
        }
        FlowNode rootFlow = flow.get(0);
        TreeItem<FlowNode> rootItem = new PropertyTreeItem<>(rootFlow, rootFlow.getUrl());
        rootItem.setExpanded(true);
        List<FlowNode> children = rootFlow.getChildren();
        while (null != children && children.size() != 0) {

        }
    }
}
