package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.controller.main.MainController;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.service.ResponseService;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @since 2020/9/11
 */
@Component
public class DumpHttpResponseIntercept implements ResponseIntercept {

    private final Logger log = LoggerFactory.getLogger(DumpHttpResponseIntercept.class);

    @Resource
    private ResponseService responseService;

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        if (null == ctx.getUserData()) {
            return null;
        }
        if (GlobalSetting.getInstance().isRecord()) {
            Request req = (Request) ctx.getUserData();
            Response res = responseService.saveResponse(ctx, response, req);
            log.info("response saved, uri: {}", request.uri());
            Platform.runLater(() -> {
                try {
                    MainController mainController = App.getMainController();
                    mainController.addFlow(ctx.getConnectionInfo(), req, res);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
        return null;
    }
}
