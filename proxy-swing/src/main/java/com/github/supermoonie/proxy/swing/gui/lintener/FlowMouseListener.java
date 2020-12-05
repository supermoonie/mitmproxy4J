package com.github.supermoonie.proxy.swing.gui.lintener;

import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.ProxyFrameHelper;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.j256.ormlite.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class FlowMouseListener extends MouseAdapter {

    private final Logger log = LoggerFactory.getLogger(FlowMouseListener.class);

    @Override
    public void mouseClicked(MouseEvent e) {
        Flow flow = ProxyFrameHelper.getSelectedFlow();
        if (null == flow || ProxyFrameHelper.currentRequestId == flow.getRequestId()) {
            return;
        }
        ProxyFrameHelper.currentRequestId = flow.getRequestId();
        try {
            Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
            Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
            Request request = requestDao.queryForId(flow.getRequestId());
            Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, flow.getRequestId()).queryForFirst();
            ProxyFrameHelper.fillOverviewTab(request, response);
            ProxyFrameHelper.showRequestContent(request);
            ProxyFrameHelper.showResponseContent(request, response);
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
