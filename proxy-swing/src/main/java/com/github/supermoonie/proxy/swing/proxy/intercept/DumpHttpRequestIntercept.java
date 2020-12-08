package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.gui.MainFrameHelper;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.github.supermoonie.proxy.swing.service.RequestService;
import com.github.supermoonie.proxy.swing.setting.GlobalSetting;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.SQLException;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
public class DumpHttpRequestIntercept implements RequestIntercept {

    private final Logger log = LoggerFactory.getLogger(DumpHttpRequestIntercept.class);

    public static final DumpHttpRequestIntercept INSTANCE = new DumpHttpRequestIntercept();

    private DumpHttpRequestIntercept() {
    }

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        if (GlobalSetting.getInstance().getRecord()) {
            try {
                final Request req = RequestService.saveRequest(ctx, request);
                log.info("request saved, uri: {}", ctx.getConnectionInfo().getUrl());
                ctx.setUserData(req);
                SwingUtilities.invokeLater(() -> {
                    try {
                        FlowList flowList = Application.MAIN_FRAME.getFlowList();
                        Flow flow = new Flow();
                        flow.setRequestId(req.getId());
                        flow.setUrl(req.getUri());
                        flow.setIcon(SvgIcons.UPLOAD);
                        if (null != FilterKeyListener.filter && !FilterKeyListener.filter.test(flow)) {
                            return;
                        }
                        flowList.add(flow);
                        flowList.updateUI();
                        FlowTreeNode rootNode = Application.MAIN_FRAME.getRootNode();
                        rootNode.add(flow);
                        Application.MAIN_FRAME.getFlowTree().updateUI();
//                        Application.PROXY_FRAME.getFlowTree().expandPath(new TreePath(rootNode.getPath()));
                        if (MainFrameHelper.currentRequestId == -1
                            || MainFrameHelper.currentRequestId == req.getId()) {
                            MainFrameHelper.fillOverviewTab(req, null);
                            MainFrameHelper.showRequestContent(req);
                            MainFrameHelper.showResponseContent(req, null);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } catch (SQLException e) {
                log.error(ctx.getConnectionInfo().getUrl() + " error: " + e.getMessage(), e);
            }
        }
        return null;
    }
}
