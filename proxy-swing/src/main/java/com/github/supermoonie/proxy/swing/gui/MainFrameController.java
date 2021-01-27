package com.github.supermoonie.proxy.swing.gui;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.j256.ormlite.dao.Dao;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2021/1/27
 */
public class MainFrameController extends MainFrame {

    public MainFrameController() {
        super();
        getJsonFilterButton().addActionListener(e -> {
            FlowTreeNode rootNode = super.getRootNode();
            rootNode.removeAllChildren();
            FlowList flowList = super.getFlowList();
            flowList.clear();
            Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
            Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
            try {
                List<Response> responseList = responseDao.queryBuilder()
                        .orderBy(Request.TIME_CREATED_FIELD_NAME, true)
                        .where()
                        .gt(Response.TIME_CREATED_NAME, Application.START_TIME)
                        .and()
                        .like(Response.CONTENT_TYPE_FIELD_NAME, "%json%")
                        .query();
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
                t.printStackTrace();
            }
        });
    }

}
