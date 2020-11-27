package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.tree.FlowTreeNode;
import com.github.supermoonie.proxy.swing.service.ResponseService;
import com.github.supermoonie.proxy.swing.setting.GlobalSetting;
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
        SwingUtilities.invokeLater(() -> {
            try {
                Request req = (Request) ctx.getUserData();
                FlowTreeNode rootNode = Application.PROXY_FRAME.getRootNode();
                rootNode.update(req, null);
                Application.PROXY_FRAME.getFlowTree().updateUI();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        if (null == ctx.getUserData()) {
            return null;
        }
        if (GlobalSetting.getInstance().getRecord()) {
            Request req = (Request) ctx.getUserData();
            try {
                Response res = ResponseService.saveResponse(ctx, req, response);
                log.info("response saved, uri: {}", request.uri());
                SwingUtilities.invokeLater(() -> {
                    try {
                        FlowTreeNode rootNode = Application.PROXY_FRAME.getRootNode();
                        rootNode.update(req, res);
                        Application.PROXY_FRAME.getFlowTree().updateUI();
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
