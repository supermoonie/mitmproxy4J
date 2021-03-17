package com.github.supermoonie.proxy.fx.controller.main;

import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.controller.ColumnMap;
import com.github.supermoonie.proxy.fx.controller.Flow;
import com.github.supermoonie.proxy.fx.controller.FlowNode;
import com.github.supermoonie.proxy.fx.controller.PropertyPair;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.dao.FlowDao;
import com.github.supermoonie.proxy.fx.entity.*;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import io.netty.handler.codec.http.HttpHeaderNames;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
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

    }

    public void onClearButtonClicked() {

    }

    public void onFilterTextFieldEnter(KeyEvent keyEvent) {

    }

    @Override
    protected void onTreeViewClicked(MouseEvent event) {
        TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (null == selectedItem) {
            return;
        }
        FlowNode selectedNode = selectedItem.getValue();
        if (null == selectedNode) {
            return;
        }
        if (!selectedNode.getType().equals(EnumFlowType.TARGET)) {
            return;
        }
        try {
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
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Url", request.getUri())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Status", selectedNode.getStatus() == -1 ? "Loading" : "Complete")));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Response Code", selectedNode.getStatus() == -1 ? "" : String.valueOf(selectedNode.getStatus()))));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Protocol", request.getHttpVersion())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Method", request.getMethod())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Host", request.getHost())));
        overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Port", String.valueOf(request.getPort()))));
        Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
        Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
        if (null != response) {
            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Content-Type", response.getContentType())));
            Dao<ConnectionOverview, Integer> overviewDao = DaoCollections.getDao(ConnectionOverview.class);
            ConnectionOverview connectionOverview = overviewDao.queryBuilder().where().eq(ConnectionOverview.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Client Address", connectionOverview.getClientHost() + ":" + connectionOverview.getClientPort())));
//            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("DNS", connectionOverview.getDnsServer())));
            overviewRoot.getChildren().add(new TreeItem<>(new PropertyPair("Remote Address", connectionOverview.getRemoteIpList())));
            TreeItem<PropertyPair> tlsTreeItem = new TreeItem<>(new PropertyPair("TLS", connectionOverview.getServerProtocol() + " (" + connectionOverview.getServerCipherSuite() + ")"));
            TreeItem<PropertyPair> clientSessionIdTreeItem = new TreeItem<>(new PropertyPair("Client Session ID", connectionOverview.getClientSessionId()));
            TreeItem<PropertyPair> serverSessionIdTreeItem = new TreeItem<>(new PropertyPair("Server Session ID", connectionOverview.getServerSessionId()));
            TreeItem<PropertyPair> clientTreeItem = new TreeItem<>(new PropertyPair("Client Certificate", ""));
            clientTreeItem.getChildren().clear();
            TreeItem<PropertyPair> serverTreeItem = new TreeItem<>(new PropertyPair("Server Certificate", ""));
            Dao<CertificateMap, Integer> certificateMapDao = DaoCollections.getDao(CertificateMap.class);
            List<CertificateMap> certificateMaps = certificateMapDao.queryBuilder().where().eq(CertificateMap.REQUEST_ID_FIELD_NAME, request.getId()).query();
            for (CertificateMap certificateMap : certificateMaps) {
                Dao<CertificateInfo, Integer> certificateInfoDao = DaoCollections.getDao(CertificateInfo.class);
                CertificateInfo certificateInfo = certificateInfoDao.queryBuilder().orderBy(CertificateInfo.TIME_CREATED_FIELD_NAME, false).where().eq(CertificateInfo.SERIAL_NUMBER_FIELD_NAME, certificateMap.getCertificateSerialNumber()).queryForFirst();
                TreeItem<PropertyPair> certTreeItem = new TreeItem<>(new PropertyPair(certificateInfo.getSubjectCommonName(), ""));
                TreeItem<PropertyPair> serialNumberTreeItem = new TreeItem<>(new PropertyPair("Serial Number", certificateInfo.getSerialNumber()));
                TreeItem<PropertyPair> typeTreeItem = new TreeItem<>(new PropertyPair("Type", certificateInfo.getType() + " [v" + certificateInfo.getVersion() + "] (" + certificateInfo.getSigAlgName() + ")"));
                TreeItem<PropertyPair> issuedToTreeItem = new TreeItem<>(new PropertyPair("Issued To", ""));
                issuedToTreeItem.getChildren().clear();
                issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Common Name", certificateInfo.getSubjectCommonName())));
                issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Unit", certificateInfo.getSubjectOrganizationDepartment())));
                issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Name", certificateInfo.getSubjectOrganizationName())));
                issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Locality Name", certificateInfo.getSubjectLocalityName())));
                issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("State Name", certificateInfo.getSubjectStateName())));
                issuedToTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Country", certificateInfo.getSubjectCountry())));
                TreeItem<PropertyPair> issuedByTreeItem = new TreeItem<>(new PropertyPair("Issued By", ""));
                issuedByTreeItem.getChildren().clear();
                issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Common Name", certificateInfo.getIssuerCommonName())));
                issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Unit", certificateInfo.getIssuerOrganizationDepartment())));
                issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Organization Name", certificateInfo.getIssuerOrganizationName())));
                issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Locality Name", certificateInfo.getIssuerLocalityName())));
                issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("State Name", certificateInfo.getIssuerStateName())));
                issuedByTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Country", certificateInfo.getIssuerCountry())));
                TreeItem<PropertyPair> notValidBeforeTreeItem = new TreeItem<>(new PropertyPair("Not Valid Before", dateFormat.format(certificateInfo.getNotValidBefore())));
                TreeItem<PropertyPair> notValidAfterTreeItem = new TreeItem<>(new PropertyPair("Not Valid After", dateFormat.format(certificateInfo.getNotValidAfter())));
                TreeItem<PropertyPair> fingerprintsTreeItem = new TreeItem<>(new PropertyPair("Fingerprints", ""));
                fingerprintsTreeItem.getChildren().clear();
                fingerprintsTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("SHA-1", certificateInfo.getShaOne())));
                fingerprintsTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("SHA-256", certificateInfo.getShaTwoFiveSix())));
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
            TreeItem<PropertyPair> timingTreeItem = new TreeItem<>(new PropertyPair("Timing", ""));
            timingTreeItem.getChildren().clear();
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Request Start Time", dateFormat.format(request.getStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Request End Time", dateFormat.format(request.getEndTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Connect Start Time", dateFormat.format(connectionOverview.getConnectStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Connect End Time", dateFormat.format(connectionOverview.getConnectStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Response Start Time", dateFormat.format(response.getStartTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Response End Time", dateFormat.format(response.getEndTime()))));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Request", (request.getEndTime() - request.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Response", (response.getEndTime() - response.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("Duration", (response.getEndTime() - request.getStartTime()) + " ms")));
            timingTreeItem.getChildren().add(new TreeItem<>(new PropertyPair("DNS", (connectionOverview.getDnsEndTime() - connectionOverview.getDnsStartTime()) + " ms")));
            overviewRoot.getChildren().add(timingTreeItem);
        }
    }

    private void fillContentsTab(FlowNode selectedNode) throws SQLException {
        if (EnumFlowType.TARGET.equals(selectedNode.getType())) {
            if (null != currentRequestId && currentRequestId.equals(selectedNode.getId())) {
                return;
            }
            clear();
            currentRequestId = selectedNode.getId();
            Flow flow = FlowDao.getFlow(currentRequestId);
            Request request = flow.getRequest();
            Response response = flow.getResponse();
            List<Header> requestHeaders = flow.getRequestHeaders();
            List<Header> responseHeaders = flow.getResponseHeaders();
            infoLabel.setText(request.getMethod().toUpperCase() + " " + request.getUri());
            requestHeaderTableView.getItems().addAll(requestHeaders);
            if (!responseHeaders.isEmpty()) {
                responseHeaderTableView.getItems().addAll(responseHeaders);
            }
            fillRequestRawTab(request, requestHeaders);
            fillRequestQueryTab(request);
            if (null != response) {
                fillResponseRawTab(response, responseHeaders);
            }
        }
    }

    private void fillResponseRawTab(Response response, List<Header> responseHeaders) throws SQLException {
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
                    Image image = new Image(new ByteArrayInputStream(bytes));
                    responseImageView.setImage(image);
                    responseImageView.setFitHeight(image.getHeight());
                    responseImageView.setFitWidth(image.getWidth());
                    responseRawBuilder.append("<Image>");
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseTextTab.getText()));
                    responseTabPane.getTabs().removeIf(tab -> tab.getText().equals(responseContentTab.getText()));
                    appendTab(responseTabPane, responseImageTab);
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
        System.out.println("--------------------------");
        System.out.println(currentResponseTabIndex);
        System.out.println(responseTabPane.getTabs().size());
        currentResponseTabIndex = Math.min(responseTabPane.getTabs().size() - 1, currentResponseTabIndex);
        System.out.println(currentResponseTabIndex);
        responseTabPane.getSelectionModel().select(currentResponseTabIndex);
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
                queryTableView.getItems().addAll(columnMaps);
                requestTabPane.getTabs().add(1, requestQueryTab);
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
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
                    formTableView.getItems().addAll(columnMaps);
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
        removeTabByTitle(requestTabPane, requestQueryTab);
        removeTabByTitle(requestTabPane, requestFormTab);
        removeTabByTitle(responseTabPane, responseImageTab);
        removeTabByTitle(responseTabPane, responseContentTab);
    }

    private void removeTabByTitle(TabPane tabPane, Tab tabToRemove) {
        tabPane.getTabs().removeIf(tab -> tab.getText().equals(tabToRemove.getText()));
    }
}
