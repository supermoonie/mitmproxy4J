package com.github.supermoonie.proxy.fx.controller.main.factory;

import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.source.Icons;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class ListViewCellFactory implements Callback<ListView<FlowNode>, ListCell<FlowNode>> {
    @Override
    public ListCell<FlowNode> call(ListView<FlowNode> param) {
        return new ListCell<>() {
            private final ChangeListener<Number> statusChangeListener = (observable, oldValue, newValue) -> {
                if (null == newValue || null == getItem()) {
                    return;
                }
                setGraphic(Icons.loadIcon(getItem().getStatus(), getItem().getContentType(), false));
            };

            @Override
            protected void updateItem(FlowNode item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getUrl());
                    setGraphic(Icons.loadIcon(item.getStatus(), item.getContentType(), false));
                    item.statusPropertyProperty().removeListener(statusChangeListener);
                    item.statusPropertyProperty().addListener(statusChangeListener);
                }
            }
        };
    }
}
