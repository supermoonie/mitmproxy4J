package com.github.supermoonie.proxy.swing.gui.lintener;

import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.MainFrameHelper;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.j256.ormlite.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.sql.SQLException;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class FlowSelectionListener implements TreeSelectionListener, ListSelectionListener {

    private final Logger log = LoggerFactory.getLogger(FlowSelectionListener.class);

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectChanged();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        selectChanged();
    }

    private void selectChanged() {
        Flow flow = MainFrameHelper.getSelectedFlow();
        if (null == flow || MainFrameHelper.currentRequestId == flow.getRequestId()) {
            return;
        }
        MainFrameHelper.currentRequestId = flow.getRequestId();
        try {
            Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
            Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
            Request request = requestDao.queryForId(flow.getRequestId());
            Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, flow.getRequestId()).queryForFirst();
            MainFrameHelper.fillOverviewTab(request, response);
            MainFrameHelper.showRequestContent(request);
            MainFrameHelper.showResponseContent(request, response);
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
