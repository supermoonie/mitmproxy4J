package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import com.github.supermoonie.proxy.swing.MitmProxy4J;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.MainFrameHelper;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.github.supermoonie.proxy.swing.service.ResponseService;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.SQLException;

/**
 * @author supermoonie
 * @since 2020/9/11
 */
public class DumpHttpResponseIntercept implements ResponseIntercept {

    private final Logger log = LoggerFactory.getLogger(DumpHttpResponseIntercept.class);

    public static final DumpHttpResponseIntercept INSTANCE = new DumpHttpResponseIntercept();

    private DumpHttpResponseIntercept() {

    }

    @Override
    public void onRead(InterceptContext ctx, HttpRequest request) {
        if (MitmProxy4J.RECORD_FLAG.get()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    Request req = (Request) ctx.getUserData();
                    FlowList flowList = MitmProxy4J.MAIN_FRAME.getFlowList();
                    flowList.findFirst(req.getId()).ifPresent(flow -> {
                        flow.setIcon(SvgIcons.DOWNLOAD);
                        flowList.updateUI();
                        FlowTreeNode rootNode = MitmProxy4J.MAIN_FRAME.getRootNode();
                        rootNode.update(req, null);
                        MitmProxy4J.MAIN_FRAME.getFlowTree().updateUI();
                    });
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        if (null == ctx.getUserData()) {
            return null;
        }
        if (MitmProxy4J.RECORD_FLAG.get()) {
            Request req = (Request) ctx.getUserData();
            try {
                Response res = ResponseService.saveResponse(ctx, req, response);
                log.info("response saved, uri: {}", ctx.getConnectionInfo().getUrl());
                SwingUtilities.invokeLater(() -> {
                    try {
                        FlowTreeNode rootNode = MitmProxy4J.MAIN_FRAME.getRootNode();
                        rootNode.update(req, res);
                        MitmProxy4J.MAIN_FRAME.getFlowTree().updateUI();
                        FlowList flowList = MitmProxy4J.MAIN_FRAME.getFlowList();
                        flowList.findFirst(req.getId()).ifPresent(flow -> {
                            if (null == res) {
                                flow.setIcon(SvgIcons.BOMB);
                            } else {
                                flow.setContentType(res.getContentType());
                                flow.setResponseId(res.getId());
                                flow.setIcon(SvgIcons.loadIcon(res.getStatus(), res.getContentType()));
                                if (MainFrameHelper.currentRequestId == -1
                                        || MainFrameHelper.currentRequestId == req.getId()) {
                                    MainFrameHelper.fillOverviewTab(req, res);
                                    MainFrameHelper.showResponseContent(req, res);
                                }
                            }
                            flowList.updateUI();
                        });
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
