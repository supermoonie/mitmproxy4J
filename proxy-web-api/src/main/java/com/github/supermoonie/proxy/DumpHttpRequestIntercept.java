package com.github.supermoonie.proxy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.constant.EnumYesNo;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.service.ConfigService;
import com.github.supermoonie.service.RequestService;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
@Component
@Slf4j
public class DumpHttpRequestIntercept implements RequestIntercept {

    @Resource
    private RequestService requestService;

    @Resource
    private ConfigMapper configMapper;

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", ConfigService.RECORD_KEY);
        Config config = configMapper.selectOne(queryWrapper);
        if (null == config || config.getValue().equals(EnumYesNo.YES.toString())) {
            Request req = requestService.saveRequest(request);
            ctx.setUserData(req);
            log.info("{} saved", req.getUri());
        }
        return null;
    }
}
