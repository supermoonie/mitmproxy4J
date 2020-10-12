package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.config.GlobalSetting;
import com.github.supermoonie.proxy.fx.constant.EnumYesNo;
import com.github.supermoonie.proxy.fx.entity.Config;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.mapper.ConfigMapper;
import com.github.supermoonie.proxy.fx.service.ConfigService;
import com.github.supermoonie.proxy.fx.service.RequestService;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
@Component
public class DumpHttpRequestIntercept implements RequestIntercept {

    private final Logger log = LoggerFactory.getLogger(DumpHttpRequestIntercept.class);

    @Resource
    private RequestService requestService;

    @Resource
    private ConfigMapper configMapper;

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        if (GlobalSetting.isRecord) {
            Request req = requestService.saveRequest(request);
            ctx.setUserData(req);
            log.info("request saved, uri: {}", request.uri());
        }
        return null;
    }
}
