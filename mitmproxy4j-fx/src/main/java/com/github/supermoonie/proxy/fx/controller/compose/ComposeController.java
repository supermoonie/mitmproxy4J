package com.github.supermoonie.proxy.fx.controller.compose;

import com.github.supermoonie.proxy.fx.controller.PropertyPair;

/**
 * @author supermoonie
 * @since 2021/3/19
 */
public class ComposeController extends ComposeView {

    public void onParamAddButtonClicked() {
        PropertyPair pair = new PropertyPair("", "");
        paramTableView.getItems().add(pair);
        int rowIndex = paramTableView.getItems().size() - 1;
        paramTableView.getSelectionModel().select(rowIndex);
        paramTableView.edit(rowIndex, paramNameTableColumn);
        paramTableView.requestFocus();
        paramTableView.getFocusModel().focus(rowIndex);
    }
}
