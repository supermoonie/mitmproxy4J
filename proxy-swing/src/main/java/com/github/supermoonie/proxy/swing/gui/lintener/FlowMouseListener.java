package com.github.supermoonie.proxy.swing.gui.lintener;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.*;
import com.github.supermoonie.proxy.swing.gui.ProxyFrame;
import com.github.supermoonie.proxy.swing.gui.ProxyFrameHelper;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.overview.ListTreeTableNode;
import com.j256.ormlite.dao.Dao;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prettify.PrettifyParser;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class FlowMouseListener extends MouseAdapter {

    private final Logger log = LoggerFactory.getLogger(FlowMouseListener.class);

    @Override
    public void mouseClicked(MouseEvent e) {
        ProxyFrame proxyFrame = Application.PROXY_FRAME;
        Flow flow = ProxyFrameHelper.getSelectedFlow();
        if (null == flow) {
            return;
        }
        try {
            Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
            Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
            Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
            Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
            Request request = requestDao.queryForId(flow.getRequestId());
            List<Header> requestHeaderList = headerDao.queryBuilder().where()
                    .eq(Header.REQUEST_ID_FIELD_NAME, flow.getRequestId()).and()
                    .isNull(Header.RESPONSE_ID_FIELD_NAME).query();
            Content requestContent = contentDao.queryForId(request.getContentId());
            Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
            fillOverviewTab(request, response);
            proxyFrame.getRequestTablePane().removeAll();
            proxyFrame.getResponseTablePane().removeAll();
            List<Component> requestTabs = new ArrayList<>();
            fillRequestHeader(requestHeaderList, requestTabs);
            fillRequestQuery(request, requestTabs);
            fillRequestForm(request, requestHeaderList, requestContent, requestTabs);
            JScrollPane requestRawScrollPane = proxyFrame.getRequestRawScrollPane();
            requestRawScrollPane.setName("Raw");
            requestTabs.add(requestRawScrollPane);
            requestTabs.forEach(component -> proxyFrame.getRequestTablePane().add(component.getName(), component));
            if (null != response) {
                List<Component> responseTabs = new ArrayList<>();
                List<Header> responseHeaderList = headerDao.queryBuilder().where()
                        .eq(Header.REQUEST_ID_FIELD_NAME, flow.getRequestId()).and()
                        .eq(Header.RESPONSE_ID_FIELD_NAME, response.getId()).query();
                fillResponseHeader(responseHeaderList, responseTabs);
                Content content = null;
                if (null != response.getContentId()) {
                    content = contentDao.queryForId(response.getContentId());
                }
                fillResponseContent(response, responseHeaderList, content, responseTabs);
                JScrollPane responseRawScrollPane = proxyFrame.getResponseRawScrollPane();
                responseRawScrollPane.setName("Raw");
                responseTabs.add(responseRawScrollPane);
                responseTabs.forEach(component -> proxyFrame.getResponseTablePane().add(component.getName(), component));
            }
        } catch (SQLException | URISyntaxException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void fillResponseContent(Response response, List<Header> responseHeaderList, Content content, List<Component> responseTabs) {
        ProxyFrame proxyFrame = Application.PROXY_FRAME;
        JTextArea responseTextArea = proxyFrame.getResponseTextArea();
        RSyntaxTextArea responseCodeArea = proxyFrame.getResponseCodeArea();
        JTextArea responseRawArea = proxyFrame.getResponseRawArea();
        responseTextArea.setText("");
        responseCodeArea.setText("");
        responseRawArea.setText("");
        StringBuilder raw = new StringBuilder();
        raw.append(response.getHttpVersion()).append(" ").append(response.getStatus()).append("\n");
        for (Header header : responseHeaderList) {
            raw.append(header.getName()).append(" ").append(header.getValue()).append("\n");
        }
        if (null != content && null != response.getContentType()) {
            String contentType = response.getContentType();
            String title = null;
            if (contentType.contains("html")) {
                title = "HTML";
                String body = new String(content.getRawContent(), StandardCharsets.UTF_8);
                responseTextArea.setText(body);
                raw.append("\n").append(body);
            } else if (contentType.contains("json")) {
                title = "JSON";
                String body = new String(content.getRawContent(), StandardCharsets.UTF_8);
                responseTextArea.setText(body);
                raw.append("\n").append(body);
            } else if (contentType.contains("javascript")) {
                title = "JavaScript";
                String body = new String(content.getRawContent(), StandardCharsets.UTF_8);
                responseTextArea.setText(body);
                raw.append("\n").append(body);
            } else if (contentType.contains("css")) {
                title = "CSS";
                String body = new String(content.getRawContent(), StandardCharsets.UTF_8);
                responseTextArea.setText(body);
                raw.append("\n").append(body);
            } else if (contentType.contains("xml")) {
                title = "XML";
                String body = new String(content.getRawContent(), StandardCharsets.UTF_8);
                responseTextArea.setText(body);
                raw.append("\n").append(body);
            }
            if (null != title) {
                JScrollPane responseTextAreaScrollPane = proxyFrame.getResponseTextAreaScrollPane();
                responseTextAreaScrollPane.setName("Text");
                responseTabs.add(responseTextAreaScrollPane);
                JPanel responseCodePane = proxyFrame.getResponseCodePane();
                responseCodePane.setName(title);
                responseTabs.add(responseCodePane);
            }
        }
        responseRawArea.setText(raw.toString());
    }

    private String prettify(String extension, String content) {
        Parser parser = new PrettifyParser();
        List<ParseResult> results = parser.parse(extension, content);
        StringBuilder sb = new StringBuilder();
        for (ParseResult result : results) {
            sb.append(content, result.getOffset(), result.getOffset() + result.getLength());
        }
        return sb.toString();
    }

    private void fillResponseHeader(List<Header> headerList, List<Component> responseTabs) {
        JTable responseHeaderTable = Application.PROXY_FRAME.getResponseHeaderTable();
        resetHeaderTable(headerList, responseHeaderTable);
        JScrollPane responseHeaderScrollPane = Application.PROXY_FRAME.getResponseHeaderScrollPane();
        responseHeaderScrollPane.setName("Header");
        responseTabs.add(responseHeaderScrollPane);
    }

    private void resetHeaderTable(List<Header> headerList, JTable headerTable) {
        DefaultTableModel model = (DefaultTableModel) headerTable.getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        for (Header header : headerList) {
            model.addRow(new String[]{header.getName(), header.getValue()});
        }
    }

    private void fillRequestForm(Request request, List<Header> requestHeaderList, Content requestContent, List<Component> requestTabs) {
        ProxyFrame proxyFrame = Application.PROXY_FRAME;
        DefaultTableModel requestFormModel = (DefaultTableModel) proxyFrame.getRequestFormTable().getModel();
        int rowCount = requestFormModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            requestFormModel.removeRow(i);
        }
        JTextArea requestContentTextArea = proxyFrame.getRequestContentTextArea();
        RSyntaxTextArea requestJsonArea = proxyFrame.getRequestJsonArea();
        requestContentTextArea.setText("");
        requestJsonArea.setText("");
        if (null == request.getContentType()) {
            fillRequestRaw(request, requestHeaderList, null);
            return;
        }
        JScrollPane requestContentTextScrollPane = proxyFrame.getRequestContentTextScrollPane();
        requestContentTextScrollPane.setName("Text");
        requestTabs.add(requestContentTextScrollPane);
        String contentType = request.getContentType().toLowerCase();
        if (contentType.contains("x-www-form-urlencoded")) {
            if (null == requestContent) {
                fillRequestRaw(request, requestHeaderList, null);
                return;
            }
            JScrollPane requestFormScrollPane = proxyFrame.getRequestFormScrollPane();
            requestFormScrollPane.setName("Form");
            requestTabs.add(requestFormScrollPane);
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
            List<String[]> formList = splitQuery(body);
            for (String[] form : formList) {
                requestFormModel.addRow(form);
            }
            fillRequestRaw(request, requestHeaderList, body);
        } else if (contentType.contains("json")) {
            if (null == requestContent) {
                fillRequestRaw(request, requestHeaderList, null);
                return;
            }
            RTextScrollPane requestJsonScrollPane = proxyFrame.getRequestJsonScrollPane();
            requestJsonScrollPane.setName("JSON");
            requestTabs.add(requestJsonScrollPane);
            proxyFrame.getRequestJsonArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
            try {
                String codeText = prettify("json", body);
                requestJsonArea.setText(codeText);
            } catch (Exception ignore) {
                requestJsonArea.setText(body);
            }
            fillRequestRaw(request, requestHeaderList, body);
        } else {
            if (null == requestContent) {
                return;
            }
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
            fillRequestRaw(request, requestHeaderList, body);
        }
    }

    private void fillRequestRaw(Request request, List<Header> requestHeaderList, String body) {
        JTextArea requestRawArea = Application.PROXY_FRAME.getRequestRawArea();
        requestRawArea.setText("");
        StringBuilder raw = new StringBuilder();
        raw.append(request.getMethod()).append(" ").append(request.getUri()).append("\n");
        for (Header header : requestHeaderList) {
            raw.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
        }
        if (null != body) {
            raw.append("\n").append(body);
        }
        requestRawArea.setText(raw.toString());
    }

    private void fillRequestQuery(Request request, List<Component> requestTabs) throws URISyntaxException {
        URI uri = new URI(request.getUri());
        String query = uri.getQuery();
        ProxyFrame proxyFrame = Application.PROXY_FRAME;
        DefaultTableModel model = (DefaultTableModel) proxyFrame.getRequestQueryTable().getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        if (null == query) {
            return;
        }
        JScrollPane requestQueryScrollPane = proxyFrame.getRequestQueryScrollPane();
        requestQueryScrollPane.setName("Query");
        requestTabs.add(requestQueryScrollPane);
        List<String[]> queryList = splitQuery(query);
        for (String[] q : queryList) {
            model.addRow(q);
        }
    }

    public static List<String[]> splitQuery(String query) {
        List<String[]> list = new LinkedList<>();
        String[] params = query.split("&");
        for (String param : params) {
            String[] form = param.split("=");
            if (form.length == 1) {
                list.add(new String[]{form[0], ""});
            } else if (form.length == 2) {
                list.add(new String[]{form[0], URLDecoder.decode(form[1], StandardCharsets.UTF_8)});
            }
        }
        return list;
    }

    private void fillRequestHeader(List<Header> headerList, List<Component> requestTabs) {
        JTable requestHeaderTable = Application.PROXY_FRAME.getRequestHeaderTable();
        resetHeaderTable(headerList, requestHeaderTable);
        JScrollPane requestHeaderScrollPane = Application.PROXY_FRAME.getRequestHeaderScrollPane();
        requestHeaderScrollPane.setName("Header");
        requestTabs.add(requestHeaderScrollPane);
    }

    private void fillOverviewTab(Request request, Response response) throws SQLException {
        ListTreeTableNode root = Application.PROXY_FRAME.getOverviewTreeTableRoot();
        DefaultTreeTableModel model = Application.PROXY_FRAME.getOverviewTreeTableModel();
        int childCount = model.getChildCount(root);
        for (int i = childCount - 1; i >= 0; i--) {
            model.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(i));
        }
        Dao<ConnectionOverview, Integer> overviewDao = DaoCollections.getDao(ConnectionOverview.class);
        ConnectionOverview connectionOverview = overviewDao.queryBuilder().where().eq(ConnectionOverview.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
        int index = 0;
        model.insertNodeInto(new ListTreeTableNode("Method", request.getMethod()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Url", request.getUri()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Status", null == response ? "Loading" : "Complete"), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Response Code", null == response ? "-" : response.getStatus()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Protocol", request.getHttpVersion()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Content-Type", null == response ? "-" : response.getContentType()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Host", request.getHost()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Port", request.getPort()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Client Address", connectionOverview.getClientHost() + ":" + connectionOverview.getClientPort()), root, index++);
        model.insertNodeInto(new ListTreeTableNode("Remote IP", Objects.requireNonNullElse(connectionOverview.getRemoteIp(), "-")), root, index++);
        model.insertNodeInto(new ListTreeTableNode("DNS", Objects.requireNonNullElse(connectionOverview.getDnsServer(), "-")), root, index++);
        ListTreeTableNode tlsNode = new ListTreeTableNode("TLS", null == response ? "-" : connectionOverview.getServerProtocol() + " (" + connectionOverview.getServerCipherSuite() + ")");
        ListTreeTableNode timingNode = new ListTreeTableNode("Timing", "");
        model.insertNodeInto(tlsNode, root, index++);
        model.insertNodeInto(timingNode, root, index);
        model.insertNodeInto(new ListTreeTableNode("Client Session ID", Objects.requireNonNullElse(connectionOverview.getClientSessionId(), "-")), tlsNode, 0);
        model.insertNodeInto(new ListTreeTableNode("Server Session ID", Objects.requireNonNullElse(connectionOverview.getServerSessionId(), "-")), tlsNode, 1);
        ListTreeTableNode clientCertNode = new ListTreeTableNode("Client Certificate", "");
        ListTreeTableNode serverCertNode = new ListTreeTableNode("Server Certificate", "");
        model.insertNodeInto(clientCertNode, tlsNode, 2);
        model.insertNodeInto(serverCertNode, tlsNode, 3);
        Dao<CertificateMap, Integer> certificateMapDao = DaoCollections.getDao(CertificateMap.class);
        List<CertificateMap> clientCertificateMapList = certificateMapDao.queryBuilder().where()
                .eq(CertificateMap.REQUEST_ID_FIELD_NAME, request.getId())
                .and()
                .isNull(CertificateMap.RESPONSE_ID_FIELD_NAME)
                .query();
        this.fillCertInfo(model, clientCertNode, clientCertificateMapList);
        if (null != response) {
            List<CertificateMap> serverCertificateMapList = certificateMapDao.queryBuilder().where()
                    .eq(CertificateMap.REQUEST_ID_FIELD_NAME, request.getId())
                    .and()
                    .eq(CertificateMap.RESPONSE_ID_FIELD_NAME, response.getId())
                    .query();
            this.fillCertInfo(model, serverCertNode, serverCertificateMapList);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        model.insertNodeInto(new ListTreeTableNode("Request Start Time", null == request.getStartTime() ? "-" : dateFormat.format(request.getStartTime())), timingNode, 0);
        model.insertNodeInto(new ListTreeTableNode("Request End Time", null == request.getEndTime() ? "-" : dateFormat.format(request.getEndTime())), timingNode, 1);
        model.insertNodeInto(new ListTreeTableNode("Connect Start Time", null == connectionOverview.getConnectStartTime() ? "-" : dateFormat.format(connectionOverview.getConnectStartTime())), timingNode, 2);
        model.insertNodeInto(new ListTreeTableNode("Connect End Time", null == connectionOverview.getConnectEndTime() ? "-" : dateFormat.format(connectionOverview.getConnectEndTime())), timingNode, 3);
        model.insertNodeInto(new ListTreeTableNode("Response Start Time", null == response ? "-" : dateFormat.format(response.getStartTime())), timingNode, 4);
        model.insertNodeInto(new ListTreeTableNode("Response End Time", null == response ? "-" : dateFormat.format(response.getEndTime())), timingNode, 5);
        model.insertNodeInto(new ListTreeTableNode("Request", null == request.getEndTime() ? "-" : request.getEndTime() - request.getStartTime() + " ms"), timingNode, 6);
        model.insertNodeInto(new ListTreeTableNode("Response", null == response ? "-" : response.getEndTime() - response.getStartTime() + " ms"), timingNode, 7);
        model.insertNodeInto(new ListTreeTableNode("Duration", null == response ? "-" : response.getEndTime() - request.getStartTime() + " ms"), timingNode, 8);
        model.insertNodeInto(new ListTreeTableNode("DNS", null == response ? "-" : connectionOverview.getDnsEndTime() - connectionOverview.getDnsStartTime() + " ms"), timingNode, 9);
    }

    private void fillCertInfo(DefaultTreeTableModel model, ListTreeTableNode certNode, List<CertificateMap> certificateMapList) throws SQLException {
        Dao<CertificateInfo, Integer> certificateDao = DaoCollections.getDao(CertificateInfo.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < certificateMapList.size(); i++) {
            CertificateMap certificateMap = certificateMapList.get(i);
            CertificateInfo certificateInfo = certificateDao.queryBuilder().where().eq(CertificateInfo.SERIAL_NUMBER_FIELD_NAME, certificateMap.getCertificateSerialNumber()).queryForFirst();
            if (null != certificateInfo) {
                ListTreeTableNode clientCertInfoNode = new ListTreeTableNode(certificateInfo.getSubjectCommonName(), "");
                model.insertNodeInto(clientCertInfoNode, certNode, i);
                model.insertNodeInto(new ListTreeTableNode("Serial Number", certificateInfo.getSerialNumber()), clientCertInfoNode, 0);
                model.insertNodeInto(new ListTreeTableNode("Type", certificateInfo.getType()), clientCertInfoNode, 1);
                ListTreeTableNode clientCertIssuedToNode = new ListTreeTableNode("Issued To", "");
                model.insertNodeInto(clientCertIssuedToNode, clientCertInfoNode, 2);
                ListTreeTableNode clientCertIssuedByNode = new ListTreeTableNode("Issued By", "");
                model.insertNodeInto(clientCertIssuedByNode, clientCertInfoNode, 3);
                model.insertNodeInto(new ListTreeTableNode("Not Valid Before", dateFormat.format(certificateInfo.getNotValidBefore())), clientCertInfoNode, 4);
                model.insertNodeInto(new ListTreeTableNode("Not Valid After", dateFormat.format(certificateInfo.getNotValidAfter())), clientCertInfoNode, 5);
                ListTreeTableNode clientFingerprintsNode = new ListTreeTableNode("Fingerprints", "");
                model.insertNodeInto(clientFingerprintsNode, clientCertInfoNode, 6);
                model.insertNodeInto(new ListTreeTableNode("Common Name", certificateInfo.getSubjectCommonName()), clientCertIssuedToNode, 0);
                model.insertNodeInto(new ListTreeTableNode("Organization Unit", certificateInfo.getSubjectOrganizationDepartment()), clientCertIssuedToNode, 1);
                model.insertNodeInto(new ListTreeTableNode("Organization Name", certificateInfo.getSubjectOrganizationName()), clientCertIssuedToNode, 2);
                model.insertNodeInto(new ListTreeTableNode("Locality Name", certificateInfo.getSubjectLocalityName()), clientCertIssuedToNode, 3);
                model.insertNodeInto(new ListTreeTableNode("State Name", certificateInfo.getSubjectStateName()), clientCertIssuedToNode, 4);
                model.insertNodeInto(new ListTreeTableNode("Country", certificateInfo.getSubjectCountry()), clientCertIssuedToNode, 5);
                model.insertNodeInto(new ListTreeTableNode("Common Name", certificateInfo.getIssuerCommonName()), clientCertIssuedByNode, 0);
                model.insertNodeInto(new ListTreeTableNode("Organization Unit", certificateInfo.getIssuerOrganizationDepartment()), clientCertIssuedByNode, 1);
                model.insertNodeInto(new ListTreeTableNode("Organization Name", certificateInfo.getIssuerOrganizationName()), clientCertIssuedByNode, 2);
                model.insertNodeInto(new ListTreeTableNode("Locality Name", certificateInfo.getIssuerLocalityName()), clientCertIssuedByNode, 3);
                model.insertNodeInto(new ListTreeTableNode("State Name", certificateInfo.getIssuerStateName()), clientCertIssuedByNode, 4);
                model.insertNodeInto(new ListTreeTableNode("Country", certificateInfo.getIssuerCountry()), clientCertIssuedByNode, 5);
                model.insertNodeInto(new ListTreeTableNode("SHA-1", certificateInfo.getShaOne()), clientFingerprintsNode, 0);
                model.insertNodeInto(new ListTreeTableNode("SHA-256", certificateInfo.getShaTwoFiveSix()), clientFingerprintsNode, 1);
            }
        }
    }
}
