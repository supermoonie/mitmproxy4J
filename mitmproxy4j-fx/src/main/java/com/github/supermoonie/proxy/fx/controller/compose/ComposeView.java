package com.github.supermoonie.proxy.fx.controller.compose;

import com.github.supermoonie.proxy.fx.component.TextFieldCell;
import com.github.supermoonie.proxy.fx.constant.HttpMethod;
import com.github.supermoonie.proxy.fx.controller.PropertyPair;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2021/3/19
 */
public class ComposeView implements Initializable {

    @FXML
    protected ChoiceBox<String> reqMethodChoiceBox;
    @FXML
    protected TextField urlTextField;
    @FXML
    protected Button paramAddButton;
    @FXML
    protected Button paramDelButton;
    @FXML
    protected TableView<PropertyPair> paramTableView;
    @FXML
    protected TableColumn<PropertyPair, String> paramNameTableColumn;
    @FXML
    protected TableColumn<PropertyPair, String> paramValueTableColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reqMethodChoiceBox.getItems().addAll(HttpMethod.ALL_METHOD);
        reqMethodChoiceBox.setValue(HttpMethod.GET);
        paramNameTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
        paramValueTableColumn.setCellFactory(cell -> TextFieldCell.createStringEditCell());
//        paramNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        paramValueTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        Platform.runLater(() -> urlTextField.requestFocus());
    }
}
