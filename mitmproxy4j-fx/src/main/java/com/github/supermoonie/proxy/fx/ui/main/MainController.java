package com.github.supermoonie.proxy.fx.ui.main;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.constant.EnumMimeType;
import com.github.supermoonie.proxy.fx.constant.RequestRawType;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.dao.FlowDao;
import com.github.supermoonie.proxy.fx.entity.*;
import com.github.supermoonie.proxy.fx.http.FileItem;
import com.github.supermoonie.proxy.fx.http.Multipart;
import com.github.supermoonie.proxy.fx.http.PartHandler;
import com.github.supermoonie.proxy.fx.ui.ColumnMap;
import com.github.supermoonie.proxy.fx.ui.Flow;
import com.github.supermoonie.proxy.fx.ui.FlowNode;
import com.github.supermoonie.proxy.fx.ui.KeyValue;
import com.github.supermoonie.proxy.fx.ui.compose.ComposeRequest;
import com.github.supermoonie.proxy.fx.ui.compose.ComposeView;
import com.github.supermoonie.proxy.fx.ui.compose.FormData;
import com.github.supermoonie.proxy.fx.ui.compose.FormDataAddDialog;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.UrlUtil;
import com.j256.ormlite.dao.Dao;
import io.netty.handler.codec.http.HttpHeaderNames;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2021/2/22
 */
public class MainController extends MainView {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    public void onJsonViewerMenuItemClicked() {
        System.out.println("....");
    }

    public void onAllowListMenuItemClicked() {

    }

    public void onBlockListMenuItemClicked() {

    }

    public void onThrottlingSettingMenuItemClicked() {

    }

    public void onProxySettingMenuItemClicked() {

    }

    public void onSystemProxyMenuItemClicked() {

    }

    public void onThrottlingMenuItemClicked() {

    }

    public void onThrottlingSwitchButtonClicked() {

    }

    public void onOpenMenuItemClicked() {

    }

    public void onSaveMenuClicked() {

    }

    public void onRecordMenuItemClicked() {

    }

    public void onRecordSwitchButtonClicked() {

    }

    public void onRepeatButtonClicked() {

    }

    public void onSendRequestMenuItemClicked() {

    }

    public void onEditButtonClicked() {
        FlowNode selectedFlowNode = getSelectedFlowNode();
        Stage composeStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Compose.fxml"));
            Parent parent = fxmlLoader.load();
            ComposeView composeView = fxmlLoader.getController();
            if (null != selectedFlowNode) {
                Flow flow = FlowDao.getFlow(selectedFlowNode.getId());
                ComposeRequest req = new ComposeRequest();
                req.setMethod(flow.getRequest().getMethod().toUpperCase());
                req.setUrl(flow.getRequest().getUri());
                List<KeyValue> headerList = flow.getRequestHeaders().stream()
                        .map(header -> new KeyValue(header.getName(), header.getValue()))
                        .collect(Collectors.toList());
                req.setHeaderList(headerList);
                if (null != flow.getRequest().getContentId() && null != flow.getRequest().getContentType()) {
                    Content content = DaoCollections.getDao(Content.class).queryForId(flow.getRequest().getContentId());
                    String contentType = flow.getRequest().getContentType().toLowerCase();
                    if (contentType.contains(EnumMimeType.FORM_DATA.getValue())) {
                        req.setMimeType(EnumMimeType.FORM_DATA.getValue());
                        List<FormData> formDataList = parseFormData(flow, content);
                        req.setFormDataList(formDataList);
                    } else if (contentType.contains(EnumMimeType.FORM_URL_ENCODED.getValue())) {
                        req.setMimeType(EnumMimeType.FORM_DATA.getValue());
                        List<KeyValue> urlEncodedList = UrlUtil.queryToList(new String(content.getRawContent(), StandardCharsets.UTF_8));
                        req.setFormUrlencodedList(urlEncodedList);
                    } else if (contentType.contains(RequestRawType.JSON.toLowerCase())
                        || contentType.contains(RequestRawType.XML.toLowerCase())
                        || contentType.contains(RequestRawType.HTML.toLowerCase())
                        || contentType.contains(RequestRawType.JAVASCRIPT.toLowerCase())
                        || contentType.contains(RequestRawType.TEXT.toLowerCase())) {
                        req.setMimeType(EnumMimeType.RAW.getValue());
                        req.setRaw(new String(content.getRawContent(), StandardCharsets.UTF_8));
                        if (contentType.contains(RequestRawType.JSON.toLowerCase())) {
                            req.setRawType(RequestRawType.JSON);
                        } else if (contentType.contains(RequestRawType.XML.toLowerCase())) {
                            req.setRawType(RequestRawType.XML);
                        } else if (contentType.contains(RequestRawType.HTML.toLowerCase())) {
                            req.setRawType(RequestRawType.HTML);
                        } else if (contentType.contains(RequestRawType.JAVASCRIPT.toLowerCase())) {
                            req.setRawType(RequestRawType.JAVASCRIPT);
                        } else {
                            req.setRawType(RequestRawType.TEXT);
                        }
                    }
                }
                composeView.setRequest(req);
            }
            composeStage.setScene(new Scene(parent));
            composeStage.setMinWidth(400);
            composeStage.setMinHeight(300);
            App.setCommonIcon(composeStage, "Compose");
            composeStage.initModality(Modality.APPLICATION_MODAL);
            composeStage.show();
        } catch (IOException | SQLException e) {
            AlertUtil.error(e);
        }
    }

    /**
     * 解析 form-data
     *
     * @param flow    flow
     * @param content content
     * @return list of form-data
     * @throws IOException e
     */
    private List<FormData> parseFormData(Flow flow, Content content) throws IOException {
        List<FormData> formDataList = new ArrayList<>();
        Multipart multipart = new Multipart();
        multipart.parse(flow.getRequest(), content.getRawContent(), StandardCharsets.UTF_8.toString(), new PartHandler() {
            @Override
            public void handleFormItem(String name, String value) {
                FormData formData = new FormData();
                formData.setName(name);
                formData.setValue(value);
                formData.setType(FormDataAddDialog.TEXT);
                formDataList.add(formData);
            }

            @Override
            public void handleFileItem(String name, FileItem fileItem) {
                try {
                    FormData formData = new FormData();
                    formData.setName(name);
                    formData.setValue(fileItem.getFileName());
                    formData.setContentType(fileItem.getContentType());
                    formData.setType(FormDataAddDialog.FILE);
                    formData.setFileContent(FileUtils.readFileToByteArray(fileItem.getFile()));
                    formDataList.add(formData);
                } catch (IOException e) {
                    AlertUtil.error(e);
                }
            }
        });
        return formDataList;
    }

    public void onClearButtonClicked() {

    }

    public void onFilterTextFieldEnter(KeyEvent keyEvent) {

    }

    @Override
    protected void onTreeItemClicked(MouseEvent event) {
        treeViewSelectedItem = treeView.getSelectionModel().getSelectedItem();
        log.info("selected: " + treeViewSelectedItem.getValue().getCurrentUrl());
        doTreeItemSelected(treeViewSelectedItem);
    }

    @Override
    protected void onTreeItemSelected(ObservableValue<? extends TreeItem<FlowNode>> observable, TreeItem<FlowNode> oldValue, TreeItem<FlowNode> newValue) {
//        treeViewSelectedItem = newValue;
//        doTreeItemSelected(treeViewSelectedItem);
    }

    @Override
    protected void onListItemClicked(MouseEvent event) {
        FlowNode selectedNode = listView.getSelectionModel().getSelectedItem();
        if (null == selectedNode) {
            return;
        }
        fillFlow(selectedNode);
    }

    private void doTreeItemSelected(TreeItem<FlowNode> treeItem) {
        if (null == treeItem) {
            return;
        }
        FlowNode selectedNode = treeItem.getValue();
        if (null == selectedNode) {
            return;
        }
        if (!selectedNode.getType().equals(EnumFlowType.TARGET)) {
            return;
        }
        fillFlow(selectedNode);
    }

    private void fillFlow(FlowNode selectedNode) {
        try {
            if (null != currentRequestId && currentRequestId.equals(selectedNode.getId())) {
                return;
            }
            currentRequestId = selectedNode.getId();
            clear();
            fillOverviewTab(selectedNode);
            fillContentsTab(selectedNode);
            Platform.runLater(() -> {
                if (!mainTabPane.getTabs().contains(contentsTab)) {
                    mainTabPane.getTabs().add(contentsTab);
                }
            });
        } catch (SQLException e) {
            AlertUtil.error(e);
        }
    }

    /**
     * 填充公共信息
     *
     * @param selectedNode 选中的 flow 节点
     * @param request      the request
     */
    private void fillOverviewRequestInfo(FlowNode selectedNode, Request request) {
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Url", request.getUri())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Status", selectedNode.getStatus() == -1 ? "Loading" : "Complete")));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Response Code", selectedNode.getStatus() == -1 ? "" : String.valueOf(selectedNode.getStatus()))));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Protocol", request.getHttpVersion())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Method", request.getMethod())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Host", request.getHost())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Port", String.valueOf(request.getPort()))));
    }

    /**
     * 填充证书信息
     *
     * @param certificateInfo 证书信息
     * @return 证书信息节点
     */
    private TreeItem<KeyValue> fillCertificateInfo(CertificateInfo certificateInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        TreeItem<KeyValue> certTreeItem = new TreeItem<>(new KeyValue(certificateInfo.getSubjectCommonName(), ""));
        TreeItem<KeyValue> serialNumberTreeItem = new TreeItem<>(new KeyValue("Serial Number", certificateInfo.getSerialNumber()));
        TreeItem<KeyValue> typeTreeItem = new TreeItem<>(new KeyValue("Type", certificateInfo.getType() + " [v" + certificateInfo.getVersion() + "] (" + certificateInfo.getSigAlgName() + ")"));
        TreeItem<KeyValue> issuedToTreeItem = new TreeItem<>(new KeyValue("Issued To", ""));
        issuedToTreeItem.getChildren().clear();
        issuedToTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Common Name", certificateInfo.getSubjectCommonName())));
        issuedToTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Organization Unit", certificateInfo.getSubjectOrganizationDepartment())));
        issuedToTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Organization Name", certificateInfo.getSubjectOrganizationName())));
        issuedToTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Locality Name", certificateInfo.getSubjectLocalityName())));
        issuedToTreeItem.getChildren().add(new TreeItem<>(new KeyValue("State Name", certificateInfo.getSubjectStateName())));
        issuedToTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Country", certificateInfo.getSubjectCountry())));
        TreeItem<KeyValue> issuedByTreeItem = new TreeItem<>(new KeyValue("Issued By", ""));
        issuedByTreeItem.getChildren().clear();
        issuedByTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Common Name", certificateInfo.getIssuerCommonName())));
        issuedByTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Organization Unit", certificateInfo.getIssuerOrganizationDepartment())));
        issuedByTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Organization Name", certificateInfo.getIssuerOrganizationName())));
        issuedByTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Locality Name", certificateInfo.getIssuerLocalityName())));
        issuedByTreeItem.getChildren().add(new TreeItem<>(new KeyValue("State Name", certificateInfo.getIssuerStateName())));
        issuedByTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Country", certificateInfo.getIssuerCountry())));
        TreeItem<KeyValue> notValidBeforeTreeItem = new TreeItem<>(new KeyValue("Not Valid Before", dateFormat.format(certificateInfo.getNotValidBefore())));
        TreeItem<KeyValue> notValidAfterTreeItem = new TreeItem<>(new KeyValue("Not Valid After", dateFormat.format(certificateInfo.getNotValidAfter())));
        TreeItem<KeyValue> fingerprintsTreeItem = new TreeItem<>(new KeyValue("Fingerprints", ""));
        fingerprintsTreeItem.getChildren().clear();
        fingerprintsTreeItem.getChildren().add(new TreeItem<>(new KeyValue("SHA-1", certificateInfo.getShaOne())));
        fingerprintsTreeItem.getChildren().add(new TreeItem<>(new KeyValue("SHA-256", certificateInfo.getShaTwoFiveSix())));
        TreeItem<KeyValue> fullDetailTreeItem = new TreeItem<>(new KeyValue("Full Detail", certificateInfo.getFullDetail()));
        certTreeItem.getChildren().clear();
        certTreeItem.getChildren().add(serialNumberTreeItem);
        certTreeItem.getChildren().add(typeTreeItem);
        certTreeItem.getChildren().add(issuedToTreeItem);
        certTreeItem.getChildren().add(issuedByTreeItem);
        certTreeItem.getChildren().add(notValidBeforeTreeItem);
        certTreeItem.getChildren().add(notValidAfterTreeItem);
        certTreeItem.getChildren().add(fingerprintsTreeItem);
        certTreeItem.getChildren().add(fullDetailTreeItem);
        return certTreeItem;
    }

    /**
     * 填充耗时信息
     *
     * @param request            请求
     * @param response           响应
     * @param connectionOverview 连接信息
     * @return the timing tree item
     */
    private TreeItem<KeyValue> fillDurationInfo(Request request, Response response, ConnectionOverview connectionOverview) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        TreeItem<KeyValue> timingTreeItem = new TreeItem<>(new KeyValue("Timing", ""));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Request Start Time", dateFormat.format(request.getStartTime()))));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Request End Time", null == request.getEndTime() ? "-" : dateFormat.format(request.getEndTime()))));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Connect Start Time", dateFormat.format(connectionOverview.getConnectStartTime()))));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Connect End Time", null == connectionOverview.getConnectEndTime() ? "-" : dateFormat.format(connectionOverview.getConnectEndTime()))));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Response Start Time", null == response.getStartTime() ? "-" : dateFormat.format(response.getStartTime()))));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Response End Time", null == response.getEndTime() ? "-" : dateFormat.format(response.getEndTime()))));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Request", null == request.getEndTime() ? "-" : (request.getEndTime() - request.getStartTime()) + " ms")));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Response", null == response.getEndTime() ? "-" : (response.getEndTime() - response.getStartTime()) + " ms")));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Duration", null == response.getEndTime() ? "-" :  (response.getEndTime() - request.getStartTime()) + " ms")));
        timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("DNS", null == connectionOverview.getDnsEndTime() ? "-" : (connectionOverview.getDnsEndTime() - connectionOverview.getDnsStartTime()) + " ms")));
        return timingTreeItem;
    }

    /**
     * 填充 overview
     *
     * @param selectedNode 选中的节点
     * @throws SQLException e
     */
    private void fillOverviewTab(FlowNode selectedNode) throws SQLException {
        overviewRoot.getChildren().clear();
        Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
        Request request = requestDao.queryForId(selectedNode.getId());
        overviewTreeTableView.setUserData(selectedNode.getId());
        fillOverviewRequestInfo(selectedNode, request);
        Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
        Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
        if (null != response) {
            overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Content-Type", response.getContentType())));
            Dao<ConnectionOverview, Integer> overviewDao = DaoCollections.getDao(ConnectionOverview.class);
            ConnectionOverview connectionOverview = overviewDao.queryBuilder().where().eq(ConnectionOverview.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
            overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Client Address", connectionOverview.getClientHost() + ":" + connectionOverview.getClientPort())));
            overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Remote Address", connectionOverview.getRemoteIpList())));
            TreeItem<KeyValue> tlsTreeItem = new TreeItem<>(new KeyValue("TLS", connectionOverview.getServerProtocol() + " (" + connectionOverview.getServerCipherSuite() + ")"));
            TreeItem<KeyValue> clientSessionIdTreeItem = new TreeItem<>(new KeyValue("Client Session ID", connectionOverview.getClientSessionId()));
            TreeItem<KeyValue> serverSessionIdTreeItem = new TreeItem<>(new KeyValue("Server Session ID", connectionOverview.getServerSessionId()));
            TreeItem<KeyValue> clientTreeItem = new TreeItem<>(new KeyValue("Client Certificate", ""));
            TreeItem<KeyValue> serverTreeItem = new TreeItem<>(new KeyValue("Server Certificate", ""));
            Dao<CertificateMap, Integer> certificateMapDao = DaoCollections.getDao(CertificateMap.class);
            List<CertificateMap> certificateMaps = certificateMapDao.queryBuilder().where().eq(CertificateMap.REQUEST_ID_FIELD_NAME, request.getId()).query();
            for (CertificateMap certificateMap : certificateMaps) {
                Dao<CertificateInfo, Integer> certificateInfoDao = DaoCollections.getDao(CertificateInfo.class);
                CertificateInfo certificateInfo = certificateInfoDao.queryBuilder().orderBy(CertificateInfo.TIME_CREATED_FIELD_NAME, false).where().eq(CertificateInfo.SERIAL_NUMBER_FIELD_NAME, certificateMap.getCertificateSerialNumber()).queryForFirst();
                TreeItem<KeyValue> certTreeItem = fillCertificateInfo(certificateInfo);
                if (null == certificateMap.getResponseId()) {
                    clientTreeItem.getChildren().add(certTreeItem);
                } else {
                    serverTreeItem.getChildren().add(certTreeItem);
                }
            }
            tlsTreeItem.getChildren().add(clientSessionIdTreeItem);
            tlsTreeItem.getChildren().add(serverSessionIdTreeItem);
            tlsTreeItem.getChildren().add(clientTreeItem);
            tlsTreeItem.getChildren().add(serverTreeItem);
            overviewRoot.getChildren().add(tlsTreeItem);
            TreeItem<KeyValue> timingTreeItem = fillDurationInfo(request, response, connectionOverview);
            overviewRoot.getChildren().add(timingTreeItem);
        }
    }

    /**
     * 填充contents tab
     *
     * @param selectedNode selected node
     * @throws SQLException e
     */
    private void fillContentsTab(FlowNode selectedNode) throws SQLException {
        if (EnumFlowType.TARGET.equals(selectedNode.getType()) && selectedNode.getStatus() > 0) {
            Flow flow = FlowDao.getFlow(currentRequestId);
            Request request = flow.getRequest();
            Response response = flow.getResponse();
            List<Header> requestHeaders = flow.getRequestHeaders();
            List<Header> responseHeaders = flow.getResponseHeaders();
            infoLabel.setText(request.getMethod().toUpperCase() + " " + request.getUri());
            requestHeaderTableView.getItems().setAll(requestHeaders);
            if (!responseHeaders.isEmpty()) {
                responseHeaderTableView.getItems().addAll(responseHeaders);
            }
            removeTabByTitle(requestTabPane, requestQueryTab);
            removeTabByTitle(requestTabPane, requestFormTab);
            fillRequestRawTab(request, requestHeaders);
            fillRequestQueryTab(request);
            if (null != response) {
                fillResponseRawTab(response, responseHeaders);
            }
        }
    }

    private void fillResponseRawTab(Response response, List<Header> responseHeaders) throws SQLException {
        int selectedIndex = responseTabPane.getSelectionModel().getSelectedIndex();
        removeTabByTitle(responseTabPane, responseImageTab);
        removeTabByTitle(responseTabPane, responseContentTab);
        StringBuilder responseRawBuilder = new StringBuilder();
        responseRawBuilder.append("Status : ").append(response.getStatus()).append("\n");
        for (Header header : responseHeaders) {
            responseRawBuilder.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
        }
        if (null != response.getContentId()) {
            Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
            Content content = contentDao.queryForId(response.getContentId());
            byte[] bytes = content.getRawContent();
            responseRawBuilder.append("\n");
            responseHeaders.stream().filter(header -> HttpHeaderNames.CONTENT_TYPE.toString().equalsIgnoreCase(header.getName())).findFirst().ifPresent(header -> {
                WebEngine engine = responseJsonWebView.getEngine();
                String hexRaw = Hex.toHexString(bytes);
                if (header.getValue().startsWith("image")) {
                    responseRawBuilder.append("<Image>");
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
                    appendTab(responseTabPane, responseImageTab);
                    final KeyFrame kf2 = new KeyFrame(Duration.millis(150), e -> {
                        Image image = new Image(new ByteArrayInputStream(bytes));
                        responseImageView.setImage(image);
                        responseImageView.setFitHeight(image.getHeight());
                        responseImageView.setFitWidth(image.getWidth());
                    });
                    final Timeline timeline = new Timeline(kf2);
                    Platform.runLater(timeline::play);
                } else {
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseImageTab.getText()));
                    if (bytes.length > 100_000) {
                        responseRawBuilder.append("<Too Large>");
                        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
                    } else {
                        String raw = new String(bytes, StandardCharsets.UTF_8);
                        responseTextArea.setText(raw);
                        responseRawBuilder.append(raw);
                        appendTab(responseTabPane, responseTextTab);
                    }

                    if (header.getValue().contains(ContentType.APPLICATION_JSON)) {
                        engine.executeScript(String.format("setHexJson('%s')", hexRaw));
                        responseContentTab.setText("JSON");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.TEXT_HTML)) {
                        engine.executeScript(String.format("setHexHtml('%s')", hexRaw));
                        responseContentTab.setText("HTML");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().contains(ContentType.APPLICATION_JAVASCRIPT)) {
                        Platform.runLater(() -> engine.executeScript(String.format("setHexJavaScript('%s')", hexRaw)));
                        responseContentTab.setText("JavaScript");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.APPLICATION_XML) || header.getValue().startsWith(ContentType.TEXT_XML)) {
                        engine.executeScript(String.format("setHexXml('%s')", hexRaw));
                        responseContentTab.setText("XML");
                        appendTab(responseTabPane, responseContentTab);
                    } else if (header.getValue().startsWith(ContentType.TEXT_CSS)) {
                        engine.executeScript(String.format("setHexCss('%s')", hexRaw));
                        responseContentTab.setText("CSS");
                        appendTab(responseTabPane, responseContentTab);
                    } else {
                        responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
                    }
                }

            });
        } else {
            responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
            responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
            responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseImageTab.getText()));
        }
        responseRawTextArea.appendText(responseRawBuilder.toString());
        selectedIndex = Math.min(responseTabPane.getTabs().size() - 1, selectedIndex);
        responseTabPane.getSelectionModel().select(selectedIndex);
    }

    private void appendTab(TabPane tabPane, Tab tab) {
        FilteredList<Tab> list = tabPane.getTabs().filtered(item -> item.getText().equals(tab.getText()));
        if (list.isEmpty()) {
            tabPane.getTabs().add(tabPane.getTabs().size(), tab);
        }
    }

    private void fillRequestQueryTab(Request request) {
        try {
            URI uri = new URI(request.getUri());
            String query = uri.getQuery();
            if (!StringUtils.isEmpty(query)) {
                List<ColumnMap> columnMaps = ColumnMap.listOf(query);
                queryTableView.getItems().setAll(columnMaps);
                requestTabPane.getTabs().add(1, requestQueryTab);
            }
        } catch (URISyntaxException e) {
            AlertUtil.error(e);
        }
    }

    private void fillRequestRawTab(Request request, List<Header> requestHeaders) throws SQLException {
        StringBuilder requestRawBuilder = new StringBuilder();
        requestRawBuilder.append(request.getMethod()).append(" ").append(request.getUri()).append("\n");
        for (Header header : requestHeaders) {
            requestRawBuilder.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
        }
        if (null != request.getContentId()) {
            Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
            Content content = contentDao.queryForId(request.getContentId());
            byte[] bytes = content.getRawContent();
            requestRawBuilder.append("\n");
            // TODO charset utf8 gbk gb2312 iso8859-1
            String raw = new String(bytes);
            requestRawBuilder.append(raw);
            requestHeaders.stream().filter(header -> HttpHeaderNames.CONTENT_TYPE.toString().equalsIgnoreCase(header.getName())).findFirst().ifPresent(header -> {
                if (header.getValue().startsWith(ContentType.APPLICATION_FORM)) {
                    List<ColumnMap> columnMaps = ColumnMap.listOf(raw);
                    formTableView.getItems().setAll(columnMaps);
                    requestTabPane.getTabs().add(1, requestFormTab);
                }
            });
        }
        requestRawTextArea.appendText(requestRawBuilder.toString());
    }

    private void clear() {
        requestHeaderTableView.getItems().clear();
        queryTableView.getItems().clear();
        formTableView.getItems().clear();
        requestRawTextArea.clear();
        responseHeaderTableView.getItems().clear();
        responseRawTextArea.clear();
        responseTextArea.clear();
    }

    private void removeTabByTitle(TabPane tabPane, Tab tabToRemove) {
        tabPane.getTabs().removeIf(tab -> tab.getText().equals(tabToRemove.getText()));
    }

    private FlowNode getSelectedFlowNode() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        FlowNode node;
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return null;
            }
            node = selectedItem.getValue();
        } else {
            node = listView.getSelectionModel().getSelectedItem();
        }
        return node;
    }
}
