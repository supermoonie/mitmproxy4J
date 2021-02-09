package com.github.supermoonie.proxy.swing.gui;

import com.formdev.flatlaf.FlatLaf;
import com.github.supermoonie.proxy.swing.MitmProxy4J;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.*;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.gui.table.TableHelper;
import com.github.supermoonie.proxy.swing.gui.treetable.ListTreeTableNode;
import com.github.supermoonie.proxy.swing.prettify.JavascriptBeautifierForJava;
import com.j256.ormlite.dao.Dao;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
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
 * @since 2020/12/1
 */
public class MainFrameHelper {

    private static final Logger log = LoggerFactory.getLogger(MainFrameHelper.class);

    public static volatile int currentRequestId = -1;
    public static volatile boolean firstFlow = true;

    private MainFrameHelper() {
        throw new UnsupportedOperationException();
    }

    public static Flow getSelectedFlow() {
        MainFrame mainFrame = MitmProxy4J.MAIN_FRAME;
        JPanel selectedComponent = (JPanel) mainFrame.getFlowTabPane().getSelectedComponent();
        Flow flow;
        if (selectedComponent.equals(mainFrame.getStructureTab())) {
            JTree flowTree = mainFrame.getFlowTree();
            FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
            if (null == node || !node.isLeaf()) {
                return null;
            }
            flow = (Flow) node.getUserObject();
        } else {
            FlowList flowList = mainFrame.getFlowList();
            flow = flowList.getSelectedValue();
        }
        return flow;
    }


    // -- response tab

    public static void showResponseContent(Request request, Response response) {
        MainFrame mainFrame = MitmProxy4J.MAIN_FRAME;
        if (null == response) {
            mainFrame.getResponseTablePane().removeAll();
            JPanel waitingPanel = new JPanel(new BorderLayout());
            JProgressBar progressBar = new JProgressBar();
            progressBar.setValue(0);
            progressBar.setIndeterminate(true);
            waitingPanel.add(progressBar, BorderLayout.SOUTH);
            mainFrame.getResponseTablePane().add("", waitingPanel);
            return;
        }
        currentRequestId = request.getId();
        try {
            int selectedIndex = Math.max(mainFrame.getResponseTablePane().getSelectedIndex(), 0);
            mainFrame.getResponseTablePane().removeAll();
            Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
            Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
            List<Component> responseTabs = new ArrayList<>();
            List<Header> responseHeaderList = headerDao.queryBuilder().where()
                    .eq(Header.REQUEST_ID_FIELD_NAME, request.getId()).and()
                    .eq(Header.RESPONSE_ID_FIELD_NAME, response.getId()).query();
            fillResponseHeader(responseHeaderList, responseTabs);
            Content content = null;
            if (null != response.getContentId()) {
                content = contentDao.queryForId(response.getContentId());
            }
            fillResponseContent(response, responseHeaderList, content, responseTabs);
            JScrollPane responseRawScrollPane = mainFrame.getResponseRawScrollPane();
            responseRawScrollPane.setName("Raw");
            responseTabs.add(responseRawScrollPane);
            responseTabs.forEach(component -> mainFrame.getResponseTablePane().add(component.getName(), component));
            selectedIndex = Math.min(selectedIndex, responseTabs.size() - 1);
            mainFrame.getResponseTablePane().setSelectedIndex(selectedIndex);
            if (firstFlow) {
                FlatLaf.updateUI();
            }
            firstFlow = false;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void fillResponseContent(Response response, List<Header> responseHeaderList, Content content, List<Component> responseTabs) {
        MainFrame mainFrame = MitmProxy4J.MAIN_FRAME;
        JTextArea responseTextArea = mainFrame.getResponseTextArea();
        RSyntaxTextArea responseCodeArea = mainFrame.getResponseCodeArea();
        JTextArea responseRawArea = mainFrame.getResponseRawArea();
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
            } else if (contentType.startsWith("text/plain")) {
                title = "TEXT";
                String body = new String(content.getRawContent(), StandardCharsets.UTF_8);
                responseTextArea.setText(body);
                raw.append("\n").append(body);
            } else if (contentType.startsWith("image/")) {
                ImageIcon image = new ImageIcon(content.getRawContent());
                JLabel label = new JLabel();
                label.setIcon(image);
                JPanel responseImagePane = mainFrame.getResponseImagePane();
                responseImagePane.removeAll();
                responseImagePane.add(new JLabel(image.getIconWidth() + " x " + image.getIconHeight()), BorderLayout.NORTH);
                responseImagePane.add(label, BorderLayout.WEST);
                JScrollPane responseImageScrollPane = mainFrame.getResponseImageScrollPane();
                responseImageScrollPane.setBorder(new EmptyBorder(5, 1, 5, 5));
                responseImageScrollPane.setName("Image");
                responseTabs.add(responseImageScrollPane);
            }
            if (null != title) {
                JScrollPane responseTextAreaScrollPane = mainFrame.getResponseTextAreaScrollPane();
                responseTextAreaScrollPane.setName("Text");
                responseTabs.add(responseTextAreaScrollPane);
                mainFrame.getResponseTextArea().setCaretPosition(0);
                JPanel responseCodePane = mainFrame.getResponseCodePane();
                responseCodePane.setName(title);
                responseTabs.add(responseCodePane);
                mainFrame.getResponseCodeArea().setCaretPosition(0);
            }
        }
        responseRawArea.setText(raw.toString());
        responseRawArea.setCaretPosition(0);
    }

    private static void fillResponseHeader(List<Header> headerList, List<Component> responseTabs) {
        JTable responseHeaderTable = MitmProxy4J.MAIN_FRAME.getResponseHeaderTable();
        resetHeaderTable(headerList, responseHeaderTable);
        JScrollPane responseHeaderScrollPane = MitmProxy4J.MAIN_FRAME.getResponseHeaderScrollPane();
        responseHeaderScrollPane.setName("Header");
        responseTabs.add(responseHeaderScrollPane);
        TableHelper.fitTableColumns(responseHeaderTable);
    }
    // -- response tab

    // -- request tab

    public static void showRequestContent(Request request) {
        currentRequestId = request.getId();
        try {
            Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
            Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
            List<Header> requestHeaderList = headerDao.queryBuilder().where()
                    .eq(Header.REQUEST_ID_FIELD_NAME, request.getId()).and()
                    .isNull(Header.RESPONSE_ID_FIELD_NAME).query();
            Content requestContent = contentDao.queryForId(request.getContentId());
            MainFrame mainFrame = MitmProxy4J.MAIN_FRAME;
            // 清除Request的所有Tab
            mainFrame.getRequestTablePane().removeAll();
            List<Component> requestTabs = new ArrayList<>();
            // 填充Request的Header Tab
            fillRequestHeader(requestHeaderList, requestTabs);
            // 填充Request的QueryString Tab
            fillRequestQuery(request, requestTabs);
            // 填充Request的Form Tab 以及Raw Tab
            fillRequestForm(request, requestHeaderList, requestContent, requestTabs);
            JScrollPane requestRawScrollPane = mainFrame.getRequestRawScrollPane();
            requestRawScrollPane.setName("Raw");
            requestTabs.add(requestRawScrollPane);
            requestTabs.forEach(component -> mainFrame.getRequestTablePane().add(component.getName(), component));
            if (firstFlow) {
                FlatLaf.updateUI();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void fillRequestHeader(List<Header> headerList, List<Component> requestTabs) {
        JTable requestHeaderTable = MitmProxy4J.MAIN_FRAME.getRequestHeaderTable();
        // 重新加载Header
        resetHeaderTable(headerList, requestHeaderTable);
        JScrollPane requestHeaderScrollPane = MitmProxy4J.MAIN_FRAME.getRequestHeaderScrollPane();
        requestHeaderScrollPane.setName("Header");
        requestTabs.add(requestHeaderScrollPane);
        // Table 宽度自适应
        TableHelper.fitTableColumns(requestHeaderTable);
    }

    private static void fillRequestQuery(Request request, List<Component> requestTabs) {
        URI uri;
        try {
            uri = new URI(request.getUri());
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            return;
        }
        String query = uri.getQuery();
        MainFrame mainFrame = MitmProxy4J.MAIN_FRAME;
        DefaultTableModel model = (DefaultTableModel) mainFrame.getRequestQueryTable().getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        if (null == query) {
            return;
        }
        JScrollPane requestQueryScrollPane = mainFrame.getRequestQueryScrollPane();
        requestQueryScrollPane.setName("Query");
        requestTabs.add(requestQueryScrollPane);
        List<String[]> queryList = splitQuery(query);
        for (String[] q : queryList) {
            model.addRow(q);
        }
        TableHelper.fitTableColumns(mainFrame.getRequestQueryTable());
    }

    private static void fillRequestForm(Request request, List<Header> requestHeaderList, Content requestContent, List<Component> requestTabs) {
        MainFrame mainFrame = MitmProxy4J.MAIN_FRAME;
        DefaultTableModel requestFormModel = (DefaultTableModel) mainFrame.getRequestFormTable().getModel();
        // 清除FormTab、TextTab、JSONTab中的数据
        int rowCount = requestFormModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            requestFormModel.removeRow(i);
        }
        JTextArea requestContentTextArea = mainFrame.getRequestContentTextArea();
        RSyntaxTextArea requestJsonArea = mainFrame.getRequestJsonArea();
        requestContentTextArea.setText("");
        requestJsonArea.setText("");
        if (null == request.getContentType()) {
            // 只填充RawTab中的数据
            fillRequestRaw(request, requestHeaderList, null);
            return;
        }
        JScrollPane requestContentTextScrollPane = mainFrame.getRequestContentTextScrollPane();
        requestContentTextScrollPane.setName("Text");
        requestTabs.add(requestContentTextScrollPane);
        String contentType = request.getContentType().toLowerCase();
        if (contentType.contains("x-www-form-urlencoded")) {
            if (null == requestContent) {
                fillRequestRaw(request, requestHeaderList, null);
                return;
            }
            JScrollPane requestFormScrollPane = mainFrame.getRequestFormScrollPane();
            requestFormScrollPane.setName("Form");
            requestTabs.add(requestFormScrollPane);
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
            requestContentTextArea.setCaretPosition(0);
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
            RTextScrollPane requestJsonScrollPane = mainFrame.getRequestJsonScrollPane();
            requestJsonScrollPane.setName("JSON");
            requestTabs.add(requestJsonScrollPane);
            mainFrame.getRequestJsonArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
            String body = new String(requestContent.getRawContent(), StandardCharsets.UTF_8);
            requestContentTextArea.setText(body);
            requestContentTextArea.setCaretPosition(0);
            try {
                String s = JavascriptBeautifierForJava.INSTANCE.beautifyJavascriptCode(body);
                requestJsonArea.setText(s);
            } catch (Exception ignore) {
                requestJsonArea.setText(body);
            }
            requestJsonArea.setFont(ApplicationPreferences.getFont());
            requestJsonArea.setCaretPosition(0);
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

    private static void fillRequestRaw(Request request, List<Header> requestHeaderList, String body) {
        JTextArea requestRawArea = MitmProxy4J.MAIN_FRAME.getRequestRawArea();
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
        requestRawArea.setCaretPosition(0);
    }

    // -- request tab

    // -- overview tab

    public static void fillOverviewTab(Request request, Response response) {
        if (currentRequestId == -1) {
            FilterKeyListener.setTreeExpandedState(MitmProxy4J.MAIN_FRAME.getFlowTree(), true);
            FlowTreeNode rootNode = MitmProxy4J.MAIN_FRAME.getRootNode();
            FlowTreeNode leaf = rootNode.findLeaf(rootNode, request.getId());
            MitmProxy4J.MAIN_FRAME.getFlowTree().setSelectionPath(new TreePath(leaf.getPath()));
        }
        currentRequestId = request.getId();
        ListTreeTableNode root = MitmProxy4J.MAIN_FRAME.getOverviewTreeTableRoot();
        DefaultTreeTableModel model = MitmProxy4J.MAIN_FRAME.getOverviewTreeTableModel();
        int childCount = model.getChildCount(root);
        for (int i = childCount - 1; i >= 0; i--) {
            model.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(i));
        }
        try {
            Dao<ConnectionOverview, Integer> overviewDao = DaoCollections.getDao(ConnectionOverview.class);
            ConnectionOverview connectionOverview = overviewDao.queryBuilder().where().eq(ConnectionOverview.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
            int index = 0;
            model.insertNodeInto(new ListTreeTableNode("Method", request.getMethod()), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Url", request.getUri()), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Status", null == response ? "Loading" : "Complete"), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Response Code", null == response ? "-" : response.getStatus()), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Protocol", request.getHttpVersion()), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Content-Type", null != response && null != response.getContentType() ? response.getContentType() : "-"), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Host", request.getHost()), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Port", request.getPort()), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Client Address", connectionOverview.getClientHost() + ":" + connectionOverview.getClientPort()), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Remote IP List", Objects.requireNonNullElse(connectionOverview.getRemoteIpList(), "-")), root, index++);
            model.insertNodeInto(new ListTreeTableNode("Selected Remote IP", Objects.requireNonNullElse(connectionOverview.getSelectedRemoteIp(), "-")), root, index++);
            ListTreeTableNode tlsNode = new ListTreeTableNode("TLS", null == response || null == connectionOverview.getServerProtocol() ? "-" : connectionOverview.getServerProtocol() + " (" + connectionOverview.getServerCipherSuite() + ")");
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
                    .eq(CertificateMap.REQUEST_ID_FIELD_NAME, request.getId()).and()
                    .isNull(CertificateMap.RESPONSE_ID_FIELD_NAME).query();
            fillCertInfo(model, clientCertNode, clientCertificateMapList);
            if (null != response) {
                List<CertificateMap> serverCertificateMapList = certificateMapDao.queryBuilder().where()
                        .eq(CertificateMap.REQUEST_ID_FIELD_NAME, request.getId()).and()
                        .eq(CertificateMap.RESPONSE_ID_FIELD_NAME, response.getId()).query();
                fillCertInfo(model, serverCertNode, serverCertificateMapList);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            model.insertNodeInto(new ListTreeTableNode("Request Start Time", null == request.getStartTime() ? "-" : dateFormat.format(request.getStartTime())), timingNode, 0);
            model.insertNodeInto(new ListTreeTableNode("Request End Time", null == request.getEndTime() ? "-" : dateFormat.format(request.getEndTime())), timingNode, 1);
            model.insertNodeInto(new ListTreeTableNode("Connect Start Time", null == connectionOverview.getConnectStartTime() ? "-" : dateFormat.format(connectionOverview.getConnectStartTime())), timingNode, 2);
            model.insertNodeInto(new ListTreeTableNode("Connect End Time", null == connectionOverview.getConnectEndTime() ? "-" : dateFormat.format(connectionOverview.getConnectEndTime())), timingNode, 3);
            model.insertNodeInto(new ListTreeTableNode("Response Start Time", null == response || null == response.getStartTime() ? "-" : dateFormat.format(response.getStartTime())), timingNode, 4);
            model.insertNodeInto(new ListTreeTableNode("Response End Time", null == response || null == response.getEndTime() ? "-" : dateFormat.format(response.getEndTime())), timingNode, 5);
            model.insertNodeInto(new ListTreeTableNode("Request", null == request.getEndTime() ? "-" : request.getEndTime() - request.getStartTime() + " ms"), timingNode, 6);
            model.insertNodeInto(new ListTreeTableNode("Response", null == response || null == response.getEndTime() ? "-" : response.getEndTime() - response.getStartTime() + " ms"), timingNode, 7);
            model.insertNodeInto(new ListTreeTableNode("Duration", null == response || null == request.getEndTime() ? "-" : response.getEndTime() - request.getStartTime() + " ms"), timingNode, 8);
            model.insertNodeInto(new ListTreeTableNode("DNS", null == response || null == connectionOverview.getDnsStartTime() ? "-" : connectionOverview.getDnsEndTime() - connectionOverview.getDnsStartTime() + " ms"), timingNode, 9);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private static void fillCertInfo(DefaultTreeTableModel model, ListTreeTableNode certNode, List<CertificateMap> certificateMapList) throws SQLException {
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

    // --overview tab

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

    private static void resetHeaderTable(List<Header> headerList, JTable headerTable) {
        DefaultTableModel model = (DefaultTableModel) headerTable.getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        for (Header header : headerList) {
            model.addRow(new String[]{header.getName(), header.getValue()});
        }
    }
}
