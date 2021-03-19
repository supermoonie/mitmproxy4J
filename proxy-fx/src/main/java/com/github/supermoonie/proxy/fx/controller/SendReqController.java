package com.github.supermoonie.proxy.fx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.constant.EnumFormValueType;
import com.github.supermoonie.proxy.fx.constant.EnumReqBodyType;
import com.github.supermoonie.proxy.fx.constant.HttpMethod;
import com.github.supermoonie.proxy.fx.constant.RequestRawType;
import com.github.supermoonie.proxy.fx.dto.ColumnMap;
import com.github.supermoonie.proxy.fx.dto.FormDataColumnMap;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.mapper.ContentMapper;
import com.github.supermoonie.proxy.fx.mapper.HeaderMapper;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.support.PropertyPair;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import com.github.supermoonie.proxy.fx.util.HttpClientUtil;
import com.github.supermoonie.proxy.fx.util.UrlUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/10/18
 */
public class SendReqController implements Initializable {

    private final Logger log = LoggerFactory.getLogger(SendReqController.class);

    @FXML
    protected ChoiceBox<String> reqMethodChoiceBox;
    @FXML
    protected TextField requestUrlTextField;
    @FXML
    protected TableView<PropertyPair> paramsTableView;
    @FXML
    protected TableColumn<PropertyPair, String> requestParamNameColumn;
    @FXML
    protected TableColumn<PropertyPair, String> requestParamValueColumn;
    @FXML
    protected TableView<ColumnMap> headerTableView;
    @FXML
    protected TableColumn<ColumnMap, String> requestHeaderNameColumn;
    @FXML
    protected TableColumn<ColumnMap, String> requestHeaderValueColumn;
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
    protected Tab requestBodyTab;
    @FXML
    protected TabPane bodyTabPane;
    @FXML
    protected Tab noneTab;
    @FXML
    protected Tab formDataTab;
    @FXML
    protected Tab formUrlEncodedTab;
    @FXML
    protected Tab binaryTab;
    @FXML
    protected Tab rawTab;
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
    @FXML
    protected ChoiceBox<String> rawTypeChoiceBox;
    @FXML
    protected TableView<FormDataColumnMap> formDataTableView;
    @FXML
    protected TableColumn<FormDataColumnMap, String> formDataNameColumn;
    @FXML
    protected TableColumn<FormDataColumnMap, String> formDataValueColumn;
    @FXML
    protected TableColumn<FormDataColumnMap, String> formDataFileColumn;
    @FXML
    protected TableColumn<FormDataColumnMap, String> contentTypeColumn;
    @FXML
    protected TableColumn<FormDataColumnMap, String> valueTypeColumn;
    @FXML
    protected TextField formDataNameTextField;
    @FXML
    protected TextField formDataValueTextField;
    @FXML
    protected Button formDataAddButton;
    @FXML
    protected Button formDataDelButton;
    @FXML
    protected TableView<ColumnMap> formUrlEncodedTableView;
    @FXML
    protected TableColumn<ColumnMap, String> formUrlEncodedNameColumn;
    @FXML
    protected TableColumn<ColumnMap, String> formUrlEncodedValueColumn;
    @FXML
    protected TextField formUrlEncodedNameTextField;
    @FXML
    protected TextField formUrlEncodedValueTextField;
    @FXML
    protected Label binaryFileNameLabel;
    @FXML
    protected WebView rawWebView;
    @FXML
    protected TabPane requestTabPane;
    @FXML
    protected Tab requestBodyTextTab;
    @FXML
    protected TextArea requestBodyTextArea;
    @FXML
    protected Button sendButton;
    @FXML
    protected Button cancelButton;
    private ToggleGroup radioGroup;

    private final RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
    private final HeaderMapper headerMapper = ApplicationContextUtil.getBean(HeaderMapper.class);
    private final ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);

    private String requestId;
    private Stage stage;
    private File binaryFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initRequestUrlTextField();
        initRequestMethodChoiceBox();
        initRequestQueryParamsTab();
        initRequestHeaderTab();
        initRequestRawTypeChoiceBox();
        initRequestBodyRadioGroup();
        initRequestFormDataTab();
        initRequestFormUrlEncodedTab();
        requestTabPane.getTabs().removeIf(tab -> tab.getText().equals(requestBodyTextTab.getText()));
        Platform.runLater(() -> requestUrlTextField.requestFocus());
    }

    private void initRequestUrlTextField() {
        requestUrlTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                updateParamTabView();
            }
        });
        requestUrlTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                updateParamTabView();
            }
        });
    }

    private void updateParamTabView() {
        try {
            URI uri = new URI(requestUrlTextField.getText());
            paramsTableView.getItems().removeListener(paramListChangeListener);
            String query = uri.getQuery();
            if (!StringUtils.isEmpty(query)) {
                List<PropertyPair> pairList = UrlUtil.queryToList(query);
                pairList.forEach(pair -> {
                    pair.keyProperty().addListener((observable1, oldValue1, newValue1) -> updateRequestUrl());
                    pair.valueProperty().addListener((observable1, oldValue1, newValue1) -> updateRequestUrl());
                });
                paramsTableView.getItems().setAll(pairList);
            }
            paramsTableView.getItems().addListener(paramListChangeListener);
        } catch (URISyntaxException ignore) {
        }
    }

    private void initRequestRawTypeChoiceBox() {
        rawTypeChoiceBox.getItems().addAll(RequestRawType.RAW_TYPE_LIST);
        rawTypeChoiceBox.setValue(RequestRawType.JSON);
        rawTypeChoiceBox.setDisable(true);
        rawTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (RequestRawType.JSON.equals(newValue)) {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/json');");
            } else if (RequestRawType.XML.equals(newValue) || RequestRawType.HTML.equals(newValue)) {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/html');");
            } else if (RequestRawType.JAVASCRIPT.equals(newValue)) {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/javascript');");
            } else {
                rawWebView.getEngine().executeScript("codeEditor.getSession().setMode('ace/mode/text');");
            }
        });
    }

    public void onSendButtonClicked() {
        String sending = "Sending...";
        if (sending.equals(sendButton.getText())) {
            return;
        }
        final List<String> autoCalculateHeaders = List.of("Host", "Content-Length", "host", "content-length");
        final String method = reqMethodChoiceBox.getValue();
        final String urlStr = requestUrlTextField.getText();
        final ObservableList<ColumnMap> headers = headerTableView.getItems();
        sendButton.setText(sending);
        if (!StringUtils.isEmpty(requestId)) {
            App.EXECUTOR.execute(() -> {
                String rawText = requestBodyTextArea.getText();
                try (CloseableHttpClient httpClient = HttpClientUtil.createTrustAllApacheHttpClientBuilder()
                        .setProxy(new HttpHost("127.0.0.1", GlobalSetting.getInstance().getPort()))
                        .build()) {
                    RequestBuilder requestBuilder = RequestBuilder.create(method).setUri(urlStr);
                    headers.forEach(header -> {
                        if (!autoCalculateHeaders.contains(header.getName())) {
                            requestBuilder.addHeader(header.getName(), header.getValue());
                        }
                    });
                    if (!StringUtils.isEmpty(rawText)) {
                        requestBuilder.setEntity(new ByteArrayEntity(rawText.getBytes(StandardCharsets.UTF_8)));
                    }
                    httpClient.execute(requestBuilder.build()).close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    Platform.runLater(() -> {
                        sendButton.setText("Send");
                        stage.close();
                    });
                }
            });
        } else {
            App.EXECUTOR.execute(() -> {
                try (CloseableHttpClient httpClient = HttpClientUtil.createTrustAllApacheHttpClientBuilder()
                        .setProxy(new HttpHost("127.0.0.1", GlobalSetting.getInstance().getPort()))
                        .build()) {
                    RequestBuilder requestBuilder = RequestBuilder.create(method).setUri(urlStr);
                    headers.forEach(header -> {
                        if (!autoCalculateHeaders.contains(header.getName())) {
                            requestBuilder.addHeader(header.getName(), header.getValue());
                        }
                    });
                    RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();
                    if (selectedRadioButton.equals(formDataRadioButton)) {
                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                        ObservableList<FormDataColumnMap> mapList = formDataTableView.getItems();
                        for (FormDataColumnMap map : mapList) {
                            if (EnumFormValueType.TEXT.toString().equals(map.getValueType())) {
                                entityBuilder.addTextBody(map.getName(), map.getValue(), ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
                            } else {
                                String mimeType;
                                if ("Auto".equals(map.getContentType())) {
                                    mimeType = MimeMappings.DEFAULT.get(map.getFileName().substring(map.getFileName().lastIndexOf(".") + 1));
                                } else {
                                    mimeType = map.getContentType();
                                }
                                entityBuilder.addBinaryBody(map.getName(), map.getFile(), ContentType.getByMimeType(mimeType), map.getFileName());
                            }
                        }
                        requestBuilder.setEntity(entityBuilder.build());
                    } else if (selectedRadioButton.equals(formUrlEncodedRadioButton)) {
                        ObservableList<ColumnMap> mapList = formUrlEncodedTableView.getItems();
                        StringBuilder params = new StringBuilder();
                        for (ColumnMap map : mapList) {
                            params.append(map.getName()).append("=").append(map.getValue()).append("&");
                        }
                        StringEntity entity = new StringEntity(params.toString(), ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));
                        requestBuilder.setEntity(entity);
                    } else if (selectedRadioButton.equals(binaryRadioButton)) {
                        if (null != binaryFile) {
                            requestBuilder.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.DEFAULT_BINARY.toString());
                            requestBuilder.setEntity(new FileEntity(binaryFile));
                        }
                    } else if (selectedRadioButton.equals(rawRadioButton)) {
                        String rawType = rawTypeChoiceBox.getValue();
                        FutureTask<String> futureTask = new FutureTask<>(() -> rawWebView.getEngine().executeScript("codeEditor.getValue()").toString());
                        Platform.runLater(futureTask);
                        String raw = futureTask.get();
                        switch (rawType) {
                            case RequestRawType.JSON:
                                requestBuilder.setEntity(new StringEntity(raw, ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));
                                break;
                            case RequestRawType.HTML:
                                requestBuilder.setEntity(new StringEntity(raw, ContentType.TEXT_HTML.withCharset(StandardCharsets.UTF_8)));
                                break;
                            case RequestRawType.JAVASCRIPT:
                                requestBuilder.setEntity(new StringEntity(raw, ContentType.create("application/javascript", StandardCharsets.UTF_8)));
                                break;
                            case RequestRawType.XML:
                                requestBuilder.setEntity(new StringEntity(raw, ContentType.APPLICATION_XML.withCharset(StandardCharsets.UTF_8)));
                                break;
                            case RequestRawType.TEXT:
                                requestBuilder.setEntity(new StringEntity(raw, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8)));
                                break;
                            default:
                                break;
                        }
                    }
                    httpClient.execute(requestBuilder.build()).close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    Platform.runLater(() -> {
                        sendButton.setText("Send");
                        stage.close();
                    });
                }
            });

        }
    }

    public void onCancelButtonClicked() {
        stage.close();
    }

    private void initRequestBodyRadioGroup() {
        noneRadioButton.setUserData(EnumReqBodyType.NONE);
        formDataRadioButton.setUserData(EnumReqBodyType.FORM_DATA);
        formUrlEncodedRadioButton.setUserData(EnumReqBodyType.X_WWW_FORM_URLENCODED);
        binaryRadioButton.setUserData(EnumReqBodyType.BINARY);
        rawRadioButton.setUserData(EnumReqBodyType.RAW);
        radioGroup = new ToggleGroup();
        noneRadioButton.setToggleGroup(radioGroup);
        formDataRadioButton.setToggleGroup(radioGroup);
        formUrlEncodedRadioButton.setToggleGroup(radioGroup);
        binaryRadioButton.setToggleGroup(radioGroup);
        rawRadioButton.setToggleGroup(radioGroup);
        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            EnumReqBodyType type = (EnumReqBodyType) newValue.getUserData();
            SingleSelectionModel<Tab> selectionModel = bodyTabPane.getSelectionModel();
            switch (type) {
                case FORM_DATA:
                    selectionModel.select(formDataTab);
                    break;
                case X_WWW_FORM_URLENCODED:
                    selectionModel.select(formUrlEncodedTab);
                    break;
                case BINARY:
                    selectionModel.select(binaryTab);
                    break;
                case RAW:
                    selectionModel.select(rawTab);
                    break;
                default:
                    selectionModel.select(noneTab);
            }
            rawTypeChoiceBox.setDisable(!type.equals(EnumReqBodyType.RAW));
        });
    }

    private void initRequestMethodChoiceBox() {
        reqMethodChoiceBox.getItems().addAll(HttpMethod.ALL_METHOD);
        reqMethodChoiceBox.setValue(HttpMethod.GET);
    }

    private final ListChangeListener<PropertyPair> paramListChangeListener = change -> updateRequestUrl();

    private void updateRequestUrl() {
        ObservableList<PropertyPair> list = paramsTableView.getItems();
        StringBuilder urlBuilder = new StringBuilder();
        String url = requestUrlTextField.getText();
        if (!StringUtils.isEmpty(url)) {
            if (url.contains("?")) {
                urlBuilder.append(url, 0, url.indexOf("?") + 1);
            } else {
                urlBuilder.append(url).append("?");
            }
        }
        for (PropertyPair pair : list) {
            urlBuilder.append(pair.getKey()).append("=").append(pair.getValue()).append("&");
        }
        requestUrlTextField.setText(urlBuilder.substring(0, urlBuilder.length() - 1));
    }

    /**
     * https://stackoverflow.com/questions/29576577/tableview-doesnt-commit-values-on-focus-lost-event
     */
    private void initRequestQueryParamsTab() {
        paramsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        requestParamNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestParamValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        paramsTableView.getItems().addListener(paramListChangeListener);
    }

    private void initRequestHeaderTab() {
        headerTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> autoCalculateHeaders = List.of("Host", "Content-Type", "Content-Length", "host", "content-type", "content-length");
        requestHeaderNameColumn.setCellFactory(col -> new TextFieldTableCell<>(new DefaultStringConverter()) {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (autoCalculateHeaders.contains(item)) {
                        setEditable(false);
                        setDisable(true);
                        setTextFill(Color.GRAY);
                    }
                }
            }
        });
        requestHeaderValueColumn.setCellFactory(col -> new TextFieldTableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ColumnMap map = getTableRow().getItem();
                    if (map != null && autoCalculateHeaders.contains(map.getName())) {
                        setEditable(false);
                        setDisable(true);
                        setTextFill(Color.GRAY);
                    }
                }
            }
        });
        requestHeaderNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        requestHeaderValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    private void initRequestFormDataTab() {
        List<String> mimeTypeList = MimeMappings.DEFAULT.getAll().stream().map(MimeMappings.Mapping::getMimeType).distinct().sorted().collect(Collectors.toList());
        mimeTypeList.add(0, "Auto");
        ObservableList<String> sortedMimeTypeList = FXCollections.observableList(mimeTypeList);
        formDataNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        formDataValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        formDataFileColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        valueTypeColumn.setCellValueFactory(new PropertyValueFactory<>("valueType"));
        contentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("contentType"));
        formDataNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        formDataValueColumn.setCellFactory(col -> new TextFieldTableCell<>(new DefaultStringConverter()) {
            @Override
            public void startEdit() {
                FormDataColumnMap map = getTableRow().getItem();
                if (null == map || map.getValueType().equals(EnumFormValueType.FILE.toString())) {
                    return;
                }
                super.startEdit();
            }

            @Override
            public void commitEdit(String newValue) {
                super.commitEdit(newValue);
                FormDataColumnMap map = getTableRow().getItem();
                map.setFileName(null);
            }
        });
        formDataFileColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            public void startEdit() {
                FormDataColumnMap map = getTableRow().getItem();
                if (null == map || map.getValueType().equals(EnumFormValueType.TEXT.toString())) {
                    return;
                }
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(stage);
                if (null != file) {
                    map.setFile(file);
                    map.setFileName(file.getName());
                    map.setValue(null);
                }
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                }
            }
        });
        valueTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(EnumFormValueType.TEXT.toString(), EnumFormValueType.FILE.toString()));
        contentTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(sortedMimeTypeList));
    }

    private void initRequestFormUrlEncodedTab() {
        formUrlEncodedTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        formUrlEncodedNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        formUrlEncodedValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        formUrlEncodedNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        formUrlEncodedValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        rawWebView.setContextMenuEnabled(false);
        rawWebView.getEngine().load(App.class.getResource("/static/RichText.html").toExternalForm());
        rawWebView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Worker.State.SUCCEEDED)) {
                rawWebView.getEngine().executeScript(String.format("setHexJson('%s');codeEditor.setReadOnly(false);", Hex.toHexString("".getBytes(StandardCharsets.UTF_8))));
            }
        });
    }

    public void onBinarySelectButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (null != file) {
            binaryFile = file;
            binaryFileNameLabel.setText(binaryFile.getName());
        }
    }

    public void onFormUrlEncodedValueTextFieldPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onFormUrlEncodedAddButtonClicked();
        }
    }

    public void onFormUrlEncodedAddButtonClicked() {
        String name = formUrlEncodedNameTextField.getText();
        if (StringUtils.isEmpty(name)) {
            AlertUtil.warning("Name is empty !");
            return;
        }
        String value = formUrlEncodedValueTextField.getText();
        formUrlEncodedTableView.getItems().add(new ColumnMap(name, value));
    }

    public void onFormUrlEncodedDelButtonClicked() {
        removeSelectRow(formUrlEncodedTableView);
    }

    public void onFormDataValueTextFieldPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onFormDataAddButtonClicked();
        }
    }

    public void onFormDataAddButtonClicked() {
        String name = formDataNameTextField.getText();
        if (StringUtils.isEmpty(name)) {
            AlertUtil.warning("Name is empty !");
            return;
        }
        String value = formDataValueTextField.getText();
        FormDataColumnMap map = new FormDataColumnMap();
        map.setName(name);
        map.setValue(value);
        map.setValueType(EnumFormValueType.TEXT.toString());
        map.setContentType("Auto");
        formDataTableView.getItems().add(map);
    }

    public void onFormDataDelButtonClicked() {
        ObservableList<Integer> indices = formDataTableView.getSelectionModel().getSelectedIndices();
        List<Integer> indexList = new LinkedList<>(indices);
        Collections.reverse(indexList);
        indexList.forEach(index -> formDataTableView.getItems().remove(index.intValue()));
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
        List<String> headerNameList = headerTableView.getItems().stream().map(ColumnMap::getName).collect(Collectors.toList());
        if (headerNameList.contains(name)) {
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
        PropertyPair pair = new PropertyPair();
        pair.setKey("");
        pair.setValue("");
        pair.keyProperty().addListener((observable, oldValue, newValue) -> updateRequestUrl());
        pair.valueProperty().addListener((observable, oldValue, newValue) -> updateRequestUrl());
        paramsTableView.getItems().add(pair);
    }

    public void onParamsDelButtonClicked() {
        ObservableList<Integer> indices = paramsTableView.getSelectionModel().getSelectedIndices();
        List<Integer> indexList = new LinkedList<>(indices);
        Collections.reverse(indexList);
        indexList.forEach(index -> paramsTableView.getItems().remove(index.intValue()));
    }

    private <T extends ColumnMap> void removeSelectRow(TableView<T> tableView) {
        ObservableList<Integer> indices = tableView.getSelectionModel().getSelectedIndices();
        List<Integer> indexList = new LinkedList<>(indices);
        Collections.reverse(indexList);
        indexList.forEach(index -> {
            T columnMap = tableView.getItems().get(index);
            if (columnMap.isEditable()) {
                tableView.getItems().remove(index.intValue());
            }
        });
    }

    public void setRequestId(String requestId) {
        log.info("requestId: {}", requestId);
        if (!StringUtils.isEmpty(requestId)) {
            this.requestId = requestId;
            requestTabPane.getTabs().removeIf(tab -> tab.getText().equals(requestBodyTab.getText()));
            requestTabPane.getTabs().add(requestBodyTextTab);
            Request request = requestMapper.selectById(requestId);
            reqMethodChoiceBox.setValue(request.getMethod().toUpperCase());
            requestUrlTextField.setText(request.getUri());
            try {
                URI uri = new URI(request.getUri());
                String query = uri.getQuery();
                if (!StringUtils.isEmpty(query)) {
                    List<PropertyPair> pairList = UrlUtil.queryToList(query);
                    pairList.forEach(pair -> {
                        pair.keyProperty().addListener((observable, oldValue, newValue) -> updateRequestUrl());
                        pair.valueProperty().addListener((observable, oldValue, newValue) -> updateRequestUrl());
                    });
                    paramsTableView.getItems().addAll(pairList);
                }
            } catch (URISyntaxException e) {
                log.error(e.getMessage(), e);
            }
            QueryWrapper<Header> requestHeaderQueryWrapper = new QueryWrapper<>();
            requestHeaderQueryWrapper.eq("request_id", request.getId());
            List<Header> requestHeaders = headerMapper.selectList(requestHeaderQueryWrapper);
            List<String> autoCalculateHeaders = List.of("Host", "Content-Type", "Content-Length", "host", "content-type", "content-length");
            List<ColumnMap> headers = requestHeaders.stream().map(header -> {
                ColumnMap columnMap = new ColumnMap();
                columnMap.setName(header.getName());
                if (autoCalculateHeaders.contains(header.getName())) {
//                    columnMap.setValue("<calculated when request is sent>");
                    columnMap.setEditable(false);
                }
                columnMap.setValue(header.getValue());
                return columnMap;
            }).collect(Collectors.toList());
            headerTableView.getItems().addAll(headers);
            String contentId = request.getContentId();
            if (!StringUtils.isEmpty(contentId)) {
                Content content = contentMapper.selectById(contentId);
                requestBodyTextArea.setText(new String(content.getContent(), StandardCharsets.UTF_8));
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
