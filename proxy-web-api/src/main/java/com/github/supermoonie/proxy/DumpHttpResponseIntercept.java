package com.github.supermoonie.proxy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.constant.EnumYesNo;
import com.github.supermoonie.dto.SimpleRequestDTO;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import com.github.supermoonie.service.ConfigService;
import com.github.supermoonie.service.ResponseService;
import com.github.supermoonie.service.support.AsyncService;
import com.github.supermoonie.ws.MessagingTemplate;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @since 2020/9/11
 */
@Component
@Slf4j
public class DumpHttpResponseIntercept implements ResponseIntercept {

    @Resource
    private ResponseService responseService;

    @Resource
    private AsyncService asyncService;

    @Resource
    private MessagingTemplate messagingTemplate;

    @Resource
    private ConfigMapper configMapper;

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        if (null == ctx.getUserData()) {
            return null;
        }
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", ConfigService.RECORD_KEY);
        Config config = configMapper.selectOne(queryWrapper);
        if (null == config || config.getValue().equals(EnumYesNo.YES.toString())) {
            Request req = (Request) ctx.getUserData();
            Response res = responseService.saveResponse(response, req);
            SimpleRequestDTO simpleRequest = new SimpleRequestDTO();
            simpleRequest.setId(req.getId());
            simpleRequest.setStatus(res.getStatus());
            simpleRequest.setUrl(req.getUri());
            asyncService.execute(() -> messagingTemplate.sendJson(simpleRequest));
        }
        return null;
    }
}
