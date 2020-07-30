package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.dto.SimpleRequestDTO;
import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import com.github.supermoonie.intercept.common.BaseFullResIntercept;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import com.github.supermoonie.service.ResponseService;
import com.github.supermoonie.service.support.AsyncService;
import com.github.supermoonie.util.JSON;
import com.github.supermoonie.ws.support.Filter;
import com.github.supermoonie.ws.support.FilterContext;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-06-09
 */
@Component("dumpFullResponseIntercept")
@Scope("prototype")
@Slf4j
public class DumpFullResponseIntercept extends BaseFullResIntercept {

    @Resource
    private ResponseService responseService;

    @Resource
    private AsyncService asyncService;

//    @Resource
//    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
        return true;
    }

    @Override
    public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
        log.debug("FullHttpResponse received uri: {}", httpRequest.uri());
        Request request = (Request) pipeline.getRequest();
        Response response = responseService.saveResponse(httpResponse, (Request) pipeline.getRequest());
        SimpleRequestDTO simpleRequest = new SimpleRequestDTO();
        simpleRequest.setId(request.getId());
        simpleRequest.setStatus(response.getStatus());
        simpleRequest.setUrl(request.getUri());
//        simpMessagingTemplate.convertAndSend("/topic/flow/list", simpleRequest);
//        asyncService.execute(() -> FilterContext.getAll().forEach((sessionId, filter) -> {
//            if (!StringUtils.isEmpty(filter.getHost()) && !request.getUri().contains(filter.getHost())) {
//                return;
//            }
//            if (!StringUtils.isEmpty(filter.getMethod()) && !request.getMethod().contains(filter.getMethod())) {
//                return;
//            }
//            log.info("sendTo: " + sessionId + " destinationPrefix: " + simpMessagingTemplate.getUserDestinationPrefix());
//            simpMessagingTemplate.convertAndSendToUser(sessionId, "/topic/flow/list", simpleRequest);
//        }));
    }

    @Override
    public void afterException(Channel clientChannel, Channel proxyChannel, Throwable cause, HttpProxyInterceptPipeline pipeline) throws Exception {
        Request request = (Request) pipeline.getRequest();
        log.error(request.getUri() + " " + cause.getMessage(), cause);
    }
}
