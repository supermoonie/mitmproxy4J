package com.github.supermoonie.proxy.swing.gui;

import com.github.supermoonie.proxy.swing.MitmProxy4J;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.gui.panel.LocalAddressDialog;
import com.github.supermoonie.proxy.swing.gui.panel.controller.DnsDialogController;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2021/1/27
 */
public class MainFrameController extends MainFrame {

    public MainFrameController(String title) {
        super(title);
        getDnsMenuItem().addActionListener(e -> new DnsDialogController(this, "DNS", true).setVisible(true));
        getExportRootCertificateMenuItem().addActionListener(e -> {
            InputStream in = MitmProxy4J.class.getClassLoader().getResourceAsStream("ca.crt");
            JFileChooser fileChooser = new JFileChooser();
            Action details = fileChooser.getActionMap().get("viewTypeDetails");
            details.actionPerformed(null);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    FileUtils.writeByteArrayToFile(new File(selectedFile.getAbsolutePath() + "/ca.crt"), IOUtils.readFully(in, Objects.requireNonNull(in).available()));
                } catch (IOException ex) {
                    MitmProxy4J.showError(ex);
                }
            }
        });
        getLocalAddressMenuItem().addActionListener(e -> new LocalAddressDialog(this, "Local IP Address", true).setVisible(true));
        getAllButton().addActionListener(e -> {
            filter(null);
            getAllButton().setSelected(true);
            getJsonFilterButton().setSelected(false);
            getHtmlFilterButton().setSelected(false);
            getImageFilterButton().setSelected(false);
            getXmlFilterButton().setSelected(false);
        });
        getJsonFilterButton().addActionListener(e -> {
            filter("json");
            getAllButton().setSelected(false);
            getJsonFilterButton().setSelected(true);
            getHtmlFilterButton().setSelected(false);
            getImageFilterButton().setSelected(false);
            getXmlFilterButton().setSelected(false);
        });
        getHtmlFilterButton().addActionListener(e -> {
            filter("html");
            getAllButton().setSelected(false);
            getJsonFilterButton().setSelected(false);
            getHtmlFilterButton().setSelected(true);
            getImageFilterButton().setSelected(false);
            getXmlFilterButton().setSelected(false);
        });
        getImageFilterButton().addActionListener(e -> {
            filter("image");
            getAllButton().setSelected(false);
            getJsonFilterButton().setSelected(false);
            getHtmlFilterButton().setSelected(false);
            getImageFilterButton().setSelected(true);
            getXmlFilterButton().setSelected(false);
        });
        getXmlFilterButton().addActionListener(e -> {
            filter("xml");
            getAllButton().setSelected(false);
            getJsonFilterButton().setSelected(false);
            getHtmlFilterButton().setSelected(false);
            getImageFilterButton().setSelected(false);
            getXmlFilterButton().setSelected(true);
        });
    }

    private void filter(String type) {
        FlowTreeNode rootNode = super.getRootNode();
        rootNode.removeAllChildren();
        FlowList flowList = super.getFlowList();
        flowList.clear();
        if (null != type) {
            flowList.filter(flow -> null != flow && flow.getContentType().contains(type));
        } else {
            flowList.filter(flow -> true);
        }
        Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
        Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
        try {
            Where<Response, Integer> where = responseDao.queryBuilder()
                    .orderBy(Request.TIME_CREATED_FIELD_NAME, true)
                    .where()
                    .gt(Response.TIME_CREATED_NAME, MitmProxy4J.START_TIME);
            if (null != type) {
                where.and().like(Response.CONTENT_TYPE_FIELD_NAME, "%" + type + "%");
            }
            List<Response> responseList = where.query();
            for (Response response : responseList) {
                Request req = requestDao.queryForId(response.getRequestId());
                Flow flow = new Flow();
                flow.setRequestId(req.getId());
                flow.setUrl(req.getUri());
                flow.setRequestTime(req.getTimeCreated());
                flow.setStatus(response.getStatus());
                flow.setContentType(response.getContentType());
                flow.setResponseId(response.getId());
                flow.setIcon(SvgIcons.loadIcon(response.getStatus(), response.getContentType()));
                rootNode.add(flow);
                flowList.add(flow);
            }
            flowList.updateUI();
            FilterKeyListener.setTreeExpandedState(super.getFlowTree(), true);
            super.getFlowTree().updateUI();
        } catch (SQLException | URISyntaxException t) {
            MitmProxy4J.showError(t);
        }
    }

}
