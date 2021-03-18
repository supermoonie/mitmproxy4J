package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.controller.FlowNode;
import com.github.supermoonie.proxy.fx.controller.main.MainController;
import com.github.supermoonie.proxy.fx.controller.main.MainView;
import com.github.supermoonie.proxy.fx.dao.ResponseDao;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (null == ctx.getUserData()) {
            return;
        }
        ConnectionInfo connectionInfo = ctx.getConnectionInfo();
        Request req = (Request) ctx.getUserData();
        FlowNode flowNode = new FlowNode();
        flowNode.setId(req.getId());
        flowNode.setType(EnumFlowType.TARGET);
        flowNode.setUrl(connectionInfo.getUrl());
        flowNode.setRequestTime(req.getTimeCreated());
        flowNode.setStatus(0);
        Platform.runLater(() -> {
            try {
                MainController mainController = App.getMainController();
                mainController.updateTreeItem(flowNode);
                mainController.updateListItem(flowNode);
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
        Request req = (Request) ctx.getUserData();
        Response res;
        try {
            res = ResponseDao.saveResponse(ctx, req, response);
        } catch (SQLException e) {
            AlertUtil.error(e);
            return null;
        }
        log.info("response saved, uri: {}", request.uri());
        Platform.runLater(() -> {
            try {
                MainController mainController = App.getMainController();
                mainController.addFlow(ctx.getConnectionInfo(), req, res);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
//        if (GlobalSetting.getInstance().isRecord()) {
//
//        }
        return null;
    }
}
