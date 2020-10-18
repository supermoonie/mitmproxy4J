package com.github.supermoonie.proxy.fx.controller;

import com.github.supermoonie.proxy.fx.dto.ColumnMap;
import com.github.supermoonie.proxy.fx.mapper.ContentMapper;
import com.github.supermoonie.proxy.fx.mapper.HeaderMapper;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.mapper.ResponseMapper;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/10/18
 */
public class SendReqController implements Initializable {

    @FXML
    protected ChoiceBox<String> reqMethodChoiceBox;
    @FXML
    protected TableView<ColumnMap> paramsTableView;
    @FXML
    protected TableColumn<ColumnMap, String> requestParamNameColumn;
    @FXML
    protected TableColumn<ColumnMap, String> requestParamValueColumn;
    @FXML
    protected TableView<ColumnMap> headerTableView;
    @FXML
    protected TableColumn<ColumnMap, String> requestHeaderNameColumn;
    @FXML
    protected TableColumn<ColumnMap, String> requestHeaderValueColumn;
    @FXML
    protected TextField paramsNameTextField;
    @FXML
    protected TextField paramsValueTextField;
    @FXML
    protected Button paramsAddButton;
    @FXML
    protected Button paramsDelButton;
    @FXML
    protected TextField headerNameTextField;
    @FXML
    protected TextField headerValueTextField;
    @FXML
    protected Button headerAddButton;
    @FXML
    protected Button headerDelButton;
    @FXML
    protected ToggleGroup radioGroup = new ToggleGroup();
    @FXML
    protected RadioButton noneRadioButton;
    @FXML
    protected RadioButton formDataRadioButton;
    @FXML
    protected RadioButton formUrlEncodedRadioButton;
    @FXML
    protected RadioButton binaryRadioButton;
    @FXML
    protected RadioButton rawRadioButton;

    private final RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
    private final HeaderMapper headerMapper = ApplicationContextUtil.getBean(HeaderMapper.class);
    private final ResponseMapper responseMapper = ApplicationContextUtil.getBean(ResponseMapper.class);
    private final ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);

    private String requestId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reqMethodChoiceBox.getItems().addAll("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE");
        reqMethodChoiceBox.setValue("GET");
        paramsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        requestParamNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestParamValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        headerTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        requestHeaderNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestHeaderValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        headerTableView.setRowFactory(param -> new TableRow<>() {
            @Override
            protected void updateItem(ColumnMap item, boolean empty) {
                if (null != item && !item.isEditable()) {
                    setDisable(true);
                    setEditable(false);
                } else {
                    super.updateItem(item, empty);
                }
            }
        });
        ColumnMap hostMap = new ColumnMap("Host", "<calculated when request is sent>");
        hostMap.setEditable(false);
        ColumnMap contentLengthMap = new ColumnMap("Content-Length", "<calculated when request is sent>");
        contentLengthMap.setEditable(false);
        ColumnMap contentTypeMap = new ColumnMap("Content-Type", "<calculated when request is sent>");
        contentTypeMap.setEditable(false);
        headerTableView.getItems().addAll(hostMap, contentLengthMap, contentTypeMap);
    }

    public void onHeaderValueTextFieldPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onHeaderAddButtonClicked();
        }
    }

    public void onHeaderAddButtonClicked() {
        String name = headerNameTextField.getText();
        if (StringUtils.isEmpty(name)) {
            AlertUtil.warning("Name is empty !");
            return;
        }
        if (List.of("Host", "Content-Type", "Content-Length").contains(name)) {
            AlertUtil.warning("Name: " + name + " duplicate !");
            return;
        }
        String value = headerValueTextField.getText();
        headerTableView.getItems().add(new ColumnMap(name, value));
    }

    public void onHeaderDelButtonClicked() {
        removeSelectRow(headerTableView);
    }

    public void onParamsValueTextFieldPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onParamsAddButtonClicked();
        }
    }

    public void onParamsAddButtonClicked() {
        String name = paramsNameTextField.getText();
        if (StringUtils.isEmpty(name)) {
            AlertUtil.warning("Name is empty !");
            return;
        }
        String value = paramsValueTextField.getText();
        paramsTableView.getItems().add(new ColumnMap(name, value));
        // TODO 更新URL
    }

    public void onParamsDelButtonClicked() {
        removeSelectRow(paramsTableView);
        // TODO 更新URL
    }

    private void removeSelectRow(TableView<ColumnMap> tableView) {
        ObservableList<Integer> indices = tableView.getSelectionModel().getSelectedIndices();
        List<Integer> indexList = new LinkedList<>(indices);
        Collections.reverse(indexList);
        indexList.forEach(index -> {
            ColumnMap columnMap = tableView.getItems().get(index);
            if (columnMap.isEditable()) {
                tableView.getItems().remove(index.intValue());
            }
        });
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
