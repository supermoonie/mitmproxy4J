package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.gui.tree.FlowTreeNode;
import com.github.supermoonie.proxy.swing.service.RequestService;
import com.github.supermoonie.proxy.swing.setting.GlobalSetting;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.TreePath;
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
                log.info("request saved, uri: {}", request.uri());
                ctx.setUserData(req);
                SwingUtilities.invokeLater(() -> {
                    try {
                        FlowTreeNode rootNode = Application.PROXY_FRAME.getRootNode();
                        rootNode.add(ctx.getConnectionInfo(), req, null);
                        Application.PROXY_FRAME.getFlowTree().updateUI();
                        Application.PROXY_FRAME.getFlowTree().expandPath(new TreePath(rootNode.getPath()));
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
