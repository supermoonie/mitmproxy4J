package com.github.supermoonie.proxy.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author supermoonie
 * @date 2020-06-08
 */
public final class ResponseUtils {

    public static void sendError(Channel channel, String error) {
        error = "<h1>Error: " + error + "</h1>";
        HttpResponse response = htmlResponse(error, HttpResponseStatus.OK);
        // Close the connection as soon as the error message is sent.
        channel.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    public static FullHttpResponse htmlResponse(String body, HttpResponseStatus status) {
        StringBuilder buf = new StringBuilder();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append("mitmproxy4J");
        buf.append("</title></head><body>\r\n");
        buf.append(body);
        buf.append("\r\n</body></html>\r\n");
        ByteBuf content = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        FullHttpResponse response =
                new DefaultFullHttpResponse(HTTP_1_1, status, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}