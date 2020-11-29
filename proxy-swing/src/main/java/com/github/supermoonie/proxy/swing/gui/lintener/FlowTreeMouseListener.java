package com.github.supermoonie.proxy.swing.gui.lintener;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.*;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.gui.overview.ListTreeTableNode;
import com.j256.ormlite.dao.Dao;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class FlowTreeMouseListener extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
        JTree flowTree = Application.PROXY_FRAME.getFlowTree();
        FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
        if (null == node) {
            return;
        }
        if (node.isLeaf()) {
            Flow flow = (Flow) node.getUserObject();
            Integer requestId = flow.getRequestId();
            ListTreeTableNode root = Application.PROXY_FRAME.getOverviewTreeTableRoot();
            DefaultTreeTableModel model = Application.PROXY_FRAME.getOverviewTreeTableModel();
            int childCount = model.getChildCount(root);
            for (int i = childCount - 1; i >= 0; i --) {
                model.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(i));
            }
            try {
                Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
                Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
                Dao<ConnectionOverview, Integer> overviewDao = DaoCollections.getDao(ConnectionOverview.class);
                Request request = requestDao.queryForId(requestId);
                Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
                ConnectionOverview connectionOverview = overviewDao.queryBuilder().where().eq(ConnectionOverview.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
                int index = 0;
                model.insertNodeInto(new ListTreeTableNode("Method", request.getMethod()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Url", request.getUri()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Status", null == response ? "Loading" : "Complete"), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Response Code", null == response ? "-" : response.getStatus()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Protocol", request.getHttpVersion()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Content-Type", null == response ? "-" : response.getContentType()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Host", request.getHost()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Port", request.getPort()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Client Address", connectionOverview.getClientHost() + ":" + connectionOverview.getClientPort()), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("Remote IP", Objects.requireNonNullElse(connectionOverview.getRemoteIp(), "-")), root, index ++);
                model.insertNodeInto(new ListTreeTableNode("DNS", Objects.requireNonNullElse(connectionOverview.getDnsServer(), "-")), root, index ++);
                ListTreeTableNode tlsNode = new ListTreeTableNode("TLS", null == response ? "-" : connectionOverview.getServerProtocol() + " (" + connectionOverview.getServerCipherSuite() + ")");
                ListTreeTableNode timingNode = new ListTreeTableNode("Timing", "");
                model.insertNodeInto(tlsNode, root, index ++);
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void fillCertInfo(DefaultTreeTableModel model, ListTreeTableNode certNode, List<CertificateMap> certificateMapList) throws SQLException {
        Dao<CertificateInfo, Integer> certificateDao = DaoCollections.getDao(CertificateInfo.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < certificateMapList.size(); i ++) {
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
