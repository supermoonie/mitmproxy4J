package com.github.supermoonie.proxy.fx.controller.main;

import com.github.supermoonie.proxy.fx.constant.ContentType;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.controller.ColumnMap;
import com.github.supermoonie.proxy.fx.controller.Flow;
import com.github.supermoonie.proxy.fx.controller.FlowNode;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.dao.FlowDao;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.j256.ormlite.dao.Dao;
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
            fillContentsTab(selectedNode);
        } catch (SQLException e) {
            AlertUtil.error(e);
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
