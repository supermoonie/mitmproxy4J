package com.github.supermoonie.proxy.swing.gui.lintener;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.*;
import com.github.supermoonie.proxy.swing.gui.ProxyFrame;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.gui.overview.ListTreeTableNode;
import com.j256.ormlite.dao.Dao;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prettify.PrettifyParser;
import prettify.parser.Job;
import prettify.parser.Prettify;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class FlowMouseListener extends MouseAdapter {

    private final Logger log = LoggerFactory.getLogger(FlowMouseListener.class);

    private final JTabbedPane flowTabPane;

    public FlowMouseListener(JTabbedPane flowTabPane) {
        this.flowTabPane = flowTabPane;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JPanel selectedComponent = (JPanel) flowTabPane.getSelectedComponent();
        Flow flow;
        if (selectedComponent.equals(Application.PROXY_FRAME.getStructureTab())) {
            JTree flowTree = Application.PROXY_FRAME.getFlowTree();
            FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
            if (null == node) {
                return;
            }
            if (node.isLeaf()) {
                flow = (Flow) node.getUserObject();
            } else {
                return;
            }
        } else {
            FlowList flowList = Application.PROXY_FRAME.getFlowList();
            flow = flowList.getSelectedValue();
        }
        try {
            Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
            Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
            Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
            Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
            Request request = requestDao.queryForId(flow.getRequestId());
            List<Header> requestHeaderList = headerDao.queryBuilder().where()
                    .eq(Header.REQUEST_ID_FIELD_NAME, flow.getRequestId())
                    .and()
                    .isNull(Header.RESPONSE_ID_FIELD_NAME)
                    .query();
            Content requestContent = contentDao.queryForId(request.getContentId());
            Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
            fillOverviewTab(request, response);
            fillRequestHeader(requestHeaderList);
            fillRequestQuery(request);
            fillRequestForm(requestHeaderList, requestContent);
        } catch (SQLException | URISyntaxException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void fillRequestForm(List<Header> requestHeaderList, Content requestContent) {
        ProxyFrame proxyFrame = Application.PROXY_FRAME;
        DefaultTableModel requestFormModel = (DefaultTableModel) proxyFrame.getRequestFormTable().getModel();
        int rowCount = requestFormModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            requestFormModel.removeRow(i);
        }
        JTextArea requestContentTextArea = proxyFrame.getRequestContentTextArea();
        requestContentTextArea.setText("");
        RSyntaxTextArea requestJsonArea = proxyFrame.getRequestJsonArea();
        requestJsonArea.setText("");
        Optional<Header> contentTypeHeader = requestHeaderList.stream().filter(header ->
                header.getName().trim().equalsIgnoreCase("content-type")
        ).findFirst();
        if (contentTypeHeader.isEmpty()) {
            int index = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestFormScrollPane());
            if (index >= 0) {
                proxyFrame.getRequestTablePane().removeTabAt(index);
                proxyFrame.getRequestTablePane().removeTabAt(index - 1);
            }
            return;
        }
        String contentType = contentTypeHeader.get().getValue().toLowerCase();
        if (contentType.contains("x-www-form-urlencoded")) {
            int jsonTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestJsonScrollPane());
            if (jsonTabIndex >= 0) {
                proxyFrame.getRequestTablePane().removeTabAt(jsonTabIndex);
            }
            int textTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestContentTextScrollPane());
            int queryTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestQueryScrollPane());
            if (textTabIndex == -1) {
                textTabIndex = queryTabIndex == -1 ? 2 : 3;
                proxyFrame.getRequestTablePane().insertTab("Text", null, Application.PROXY_FRAME.getRequestContentTextScrollPane(), null, textTabIndex);
            }
            int formTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestFormScrollPane());
            if (formTabIndex == -1) {
                proxyFrame.getRequestTablePane().insertTab("Form", null, Application.PROXY_FRAME.getRequestFormScrollPane(), null, textTabIndex + 1);
            }
            if (null == requestContent) {
                return;
            }
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
            List<String[]> formList = splitQuery(body);
            for (String[] form : formList) {
                requestFormModel.addRow(form);
            }
        } else if (contentType.contains("json")) {
            int formTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestFormScrollPane());
            if (formTabIndex >= 0) {
                proxyFrame.getRequestTablePane().removeTabAt(formTabIndex);
            }
            int textTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestContentTextScrollPane());
            int queryTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestQueryScrollPane());
            if (textTabIndex == -1) {
                textTabIndex = queryTabIndex == -1 ? 2 : 3;
                proxyFrame.getRequestTablePane().insertTab("Text", null, Application.PROXY_FRAME.getRequestContentTextScrollPane(), null, textTabIndex);
            }
            int jsonTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestJsonScrollPane());
            if (jsonTabIndex == -1) {
                proxyFrame.getRequestTablePane().insertTab("JSON", null, Application.PROXY_FRAME.getRequestJsonScrollPane(), null, textTabIndex + 1);
            }
            if (null == requestContent) {
                return;
            }
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
            try {
                Parser parser = new PrettifyParser();
                List<ParseResult> results = parser.parse("js", body);
                StringBuilder highlighted = new StringBuilder();
                for(ParseResult result : results){
                    String type = result.getStyleKeys().get(0);
                    String content = body.substring(result.getOffset(), result.getOffset() + result.getLength());
                    highlighted.append(content);
                }

//                Job prettifyJob = new Job(0, body);
//                Prettify prettify = new Prettify();
//                prettify.langHandlerForExtension("js", body).decorate(prettifyJob);
//                List<Object> decorations = prettifyJob.getDecorations();
                System.out.println(highlighted.toString());
                requestJsonArea.setText(highlighted.toString());
            } catch (Exception ignore) {
                requestJsonArea.setText(body);
            }
        } else {
            int jsonTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestJsonScrollPane());
            if (jsonTabIndex >= 0) {
                proxyFrame.getRequestTablePane().removeTabAt(jsonTabIndex);
            }
            int formTabIndex = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestFormScrollPane());
            if (formTabIndex >= 0) {
                proxyFrame.getRequestTablePane().removeTabAt(formTabIndex);
            }
            if (null == requestContent) {
                return;
            }
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
        }
    }

//    private String getColor(String type){
//        return COLORS.containsKey(type) ? COLORS.get(type) : COLORS.get("pln");
//    }
//
//    private static Map<String, String> buildColorsMap() {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("typ", "87cefa");
//        map.put("kwd", "00ff00");
//        map.put("lit", "ffff00");
//        map.put("com", "999999");
//        map.put("str", "ff4500");
//        map.put("pun", "eeeeee");
//        map.put("pln", "ffffff");
//        return map;
//    }

    private void fillRequestQuery(Request request) throws URISyntaxException {
        URI uri = new URI(request.getUri());
        String query = uri.getQuery();
        ProxyFrame proxyFrame = Application.PROXY_FRAME;
        DefaultTableModel model = (DefaultTableModel) proxyFrame.getRequestQueryTable().getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        int index = proxyFrame.getRequestTablePane().indexOfComponent(proxyFrame.getRequestQueryScrollPane());
        if (null == query) {
            if (index >= 0) {
                proxyFrame.getRequestTablePane().removeTabAt(index);
            }
            return;
        }
        if (index == -1) {
            proxyFrame.getRequestTablePane().insertTab("Query", null, Application.PROXY_FRAME.getRequestQueryScrollPane(), null, 1);
        }
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

    private void fillRequestHeader(List<Header> headerList) {
        JTable requestHeaderTable = Application.PROXY_FRAME.getRequestHeaderTable();
        DefaultTableModel model = (DefaultTableModel) requestHeaderTable.getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        for (Header header : headerList) {
            model.addRow(new String[]{header.getName(), header.getValue()});
        }
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
        model.insertNodeInto(new ListTreeTableNode("Client Session ID", connectionOverview.getClientSessionId()), tlsNode, 0);
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
