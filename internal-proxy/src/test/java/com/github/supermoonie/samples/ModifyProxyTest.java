package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.StandardCharsets;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public class ModifyProxyTest {

    public static void main(String[] args) {
        RequestIntercept requestIntercept = (ctx, request) -> {
            ConnectionInfo connectionInfo = ctx.getConnectionInfo();
            if (connectionInfo.getUrl().contains("guanaitong.cc")) {
                request.headers().set("Host", "c.guanaitong.tech");
                connectionInfo.setRemoteHost("c.guanaitong.tech");
            }
            return null;
        };
        ResponseIntercept responseIntercept = (ctx, request, response) -> {
            String contentType = response.headers().get(HttpHeaderNames.CONTENT_TYPE);
            if (contentType.contains("javascript") || contentType.contains("html")) {
                ByteBuf content = response.content();
                byte[] bytes = ByteBufUtil.getBytes(content);
                content.release();
                String js = new String(bytes, StandardCharsets.UTF_8);
                js = js.replaceAll("guanaitong.cc", "guanaitong.tech");
                ByteBuf buf = Unpooled.wrappedBuffer(js.getBytes(StandardCharsets.UTF_8));
                FullHttpResponse httpResponse = response.replace(buf);
                httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
                return httpResponse;
            }
            return response;
        };
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            requestIntercepts.put("modify", requestIntercept);
            responseIntercepts.put("modify", responseIntercept);
        });
        proxy.setPort(10801);
        proxy.start();
    }
}
