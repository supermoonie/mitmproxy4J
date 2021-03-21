package com.github.supermoonie.proxy.fx.controller.main;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.controller.ColumnMap;
import com.github.supermoonie.proxy.fx.controller.Flow;
import com.github.supermoonie.proxy.fx.controller.FlowNode;
import com.github.supermoonie.proxy.fx.controller.KeyValue;
import com.github.supermoonie.proxy.fx.controller.compose.ComposeView;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.dao.FlowDao;
import com.github.supermoonie.proxy.fx.entity.*;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
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
import java.util.List;

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
        Stage composeStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Compose.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            fxmlLoader.getController();
            composeStage.setScene(new Scene(parent));
            composeStage.setMinWidth(400);
            composeStage.setMinHeight(300);
            App.setCommonIcon(composeStage, "Compose");
            composeStage.initModality(Modality.APPLICATION_MODAL);
            composeStage.show();
        } catch (IOException e) {
            AlertUtil.error(e);
        }
    }

    public void onClearButtonClicked() {

    }

    public void onFilterTextFieldEnter(KeyEvent keyEvent) {

    }

    @Override
    protected void onTreeItemClicked(MouseEvent event) {
        treeViewSelectedItem = treeView.getSelectionModel().getSelectedItem();
        doTreeItemSelected(treeViewSelectedItem);
    }

    @Override
    protected void onTreeItemSelected(ObservableValue<? extends TreeItem<FlowNode>> observable, TreeItem<FlowNode> oldValue, TreeItem<FlowNode> newValue) {
        treeViewSelectedItem = newValue;
        doTreeItemSelected(treeViewSelectedItem);
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
//            clear();
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

    private void fillOverviewTab(FlowNode selectedNode) throws SQLException {
        overviewRoot.getChildren().clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
        Request request = requestDao.queryForId(selectedNode.getId());
        overviewTreeTableView.setUserData(selectedNode.getId());
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Url", request.getUri())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Status", selectedNode.getStatus() == -1 ? "Loading" : "Complete")));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Response Code", selectedNode.getStatus() == -1 ? "" : String.valueOf(selectedNode.getStatus()))));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Protocol", request.getHttpVersion())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Method", request.getMethod())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Host", request.getHost())));
        overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Port", String.valueOf(request.getPort()))));
        Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
        Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
        if (null != response) {
            overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Content-Type", response.getContentType())));
            Dao<ConnectionOverview, Integer> overviewDao = DaoCollections.getDao(ConnectionOverview.class);
            ConnectionOverview connectionOverview = overviewDao.queryBuilder().where().eq(ConnectionOverview.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
            overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Client Address", connectionOverview.getClientHost() + ":" + connectionOverview.getClientPort())));
//            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("DNS", connectionOverview.getDnsServer())));
            overviewRoot.getChildren().add(new TreeItem<>(new KeyValue("Remote Address", connectionOverview.getRemoteIpList())));
            TreeItem<KeyValue> tlsTreeItem = new TreeItem<>(new KeyValue("TLS", connectionOverview.getServerProtocol() + " (" + connectionOverview.getServerCipherSuite() + ")"));
            TreeItem<KeyValue> clientSessionIdTreeItem = new TreeItem<>(new KeyValue("Client Session ID", connectionOverview.getClientSessionId()));
            TreeItem<KeyValue> serverSessionIdTreeItem = new TreeItem<>(new KeyValue("Server Session ID", connectionOverview.getServerSessionId()));
            TreeItem<KeyValue> clientTreeItem = new TreeItem<>(new KeyValue("Client Certificate", ""));
            clientTreeItem.getChildren().clear();
            TreeItem<KeyValue> serverTreeItem = new TreeItem<>(new KeyValue("Server Certificate", ""));
            Dao<CertificateMap, Integer> certificateMapDao = DaoCollections.getDao(CertificateMap.class);
            List<CertificateMap> certificateMaps = certificateMapDao.queryBuilder().where().eq(CertificateMap.REQUEST_ID_FIELD_NAME, request.getId()).query();
            for (CertificateMap certificateMap : certificateMaps) {
                Dao<CertificateInfo, Integer> certificateInfoDao = DaoCollections.getDao(CertificateInfo.class);
                CertificateInfo certificateInfo = certificateInfoDao.queryBuilder().orderBy(CertificateInfo.TIME_CREATED_FIELD_NAME, false).where().eq(CertificateInfo.SERIAL_NUMBER_FIELD_NAME, certificateMap.getCertificateSerialNumber()).queryForFirst();
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
//                TreeItem<PropertyPair> fullDetailTreeItem = new TreeItem<>(new PropertyPair("Full Detail", certificateInfo.getFullDetail()));
                certTreeItem.getChildren().clear();
                certTreeItem.getChildren().add(serialNumberTreeItem);
                certTreeItem.getChildren().add(typeTreeItem);
                certTreeItem.getChildren().add(issuedToTreeItem);
                certTreeItem.getChildren().add(issuedByTreeItem);
                certTreeItem.getChildren().add(notValidBeforeTreeItem);
                certTreeItem.getChildren().add(notValidAfterTreeItem);
                certTreeItem.getChildren().add(fingerprintsTreeItem);
//                certTreeItem.getChildren().add(fullDetailTreeItem);
                if (null == certificateMap.getResponseId()) {
                    clientTreeItem.getChildren().add(certTreeItem);
                } else {
                    serverTreeItem.getChildren().add(certTreeItem);
                }
            }
            tlsTreeItem.getChildren().clear();
            tlsTreeItem.getChildren().add(clientSessionIdTreeItem);
            tlsTreeItem.getChildren().add(serverSessionIdTreeItem);
            tlsTreeItem.getChildren().add(clientTreeItem);
            tlsTreeItem.getChildren().add(serverTreeItem);
            overviewRoot.getChildren().add(tlsTreeItem);
            TreeItem<KeyValue> timingTreeItem = new TreeItem<>(new KeyValue("Timing", ""));
            timingTreeItem.getChildren().clear();
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Request Start Time", dateFormat.format(request.getStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Request End Time", dateFormat.format(request.getEndTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Connect Start Time", dateFormat.format(connectionOverview.getConnectStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Connect End Time", dateFormat.format(connectionOverview.getConnectStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Response Start Time", dateFormat.format(response.getStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Response End Time", dateFormat.format(response.getEndTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Request", (request.getEndTime() - request.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Response", (response.getEndTime() - response.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("Duration", (response.getEndTime() - request.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new KeyValue("DNS", (connectionOverview.getDnsEndTime() - connectionOverview.getDnsStartTime()) + " ms")));
            overviewRoot.getChildren().add(timingTreeItem);
        }
    }

    private void fillContentsTab(FlowNode selectedNode) throws SQLException {
        if (EnumFlowType.TARGET.equals(selectedNode.getType()) && selectedNode.getStatus() > 0) {
            if (null != currentRequestId && currentRequestId.equals(selectedNode.getId())) {
                return;
            }
            currentRequestId = selectedNode.getId();
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
//                    final KeyFrame kf1 = new KeyFrame(Duration.millis(150), e -> );
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
}
