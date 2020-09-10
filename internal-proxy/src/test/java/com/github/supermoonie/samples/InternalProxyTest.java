package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author supermoonie
 * @since 2020/8/20
 */
public class InternalProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
//            LoggingIntercept loggingIntercept = new LoggingIntercept();
//            requestIntercepts.put("logging-intercept", loggingIntercept);
//            responseIntercepts.put("logging-intercept", loggingIntercept);
//            requestIntercepts.put("request-intercept-0", (ctx, request) -> {
//                request.headers().add("foo", "bar");
//                return null;
//            });
//            responseIntercepts.put("response-intercept-0", (ctx, response) -> {
//                ByteBuf content = response.content();
//                content.markReaderIndex();
//                byte[] bytes = new byte[content.readableBytes()];
//                content.readBytes(bytes);
//                System.out.println(new String(bytes, StandardCharsets.UTF_8));
//                response.headers().add("bar", "foo");
//                content.resetReaderIndex();
//                return response;
//            });
        });
        proxy.setPort(10801);
        proxy.start();
    }
}
