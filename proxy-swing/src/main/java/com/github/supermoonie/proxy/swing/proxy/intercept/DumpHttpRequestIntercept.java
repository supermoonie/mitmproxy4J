package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.service.RequestService;
import com.github.supermoonie.proxy.swing.setting.GlobalSetting;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import javafx.application.Platform;
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
            Request req = null;
            try {
                req = RequestService.saveRequest(ctx, request);
                log.info("request saved, uri: {}", request.uri());
                ctx.setUserData(req);
            } catch (SQLException e) {
                log.error(ctx.getConnectionInfo().getUrl() + " error: " + e.getMessage(), e);
            }
            SwingUtilities.invokeLater(() -> {
                try {
//                    MainController mainController = App.getMainController();
//                    mainController.addFlow(ctx.getConnectionInfo(), req, null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
        return null;
    }
}
