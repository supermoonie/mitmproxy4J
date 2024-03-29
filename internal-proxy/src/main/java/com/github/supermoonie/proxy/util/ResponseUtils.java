package com.github.supermoonie.proxy.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author supermoonie
 * @date 2020-06-08
 */
public final class ResponseUtils {

    public static void sendInternalServerError(Channel channel, String error) {
        sendError(channel, error, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public static void sendServiceUnavailableError(Channel channel, String error) {
        sendError(channel, error, HttpResponseStatus.SERVICE_UNAVAILABLE);
    }

    private static void sendError(Channel channel, String error, HttpResponseStatus status) {
        error = "Error: " + error;
        FullHttpResponse response = htmlResponse(error, status);
        // Close the connection as soon as the error message is sent.
        channel.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
        ReferenceCountUtil.release(response);
    }

    public static FullHttpResponse htmlResponse(String body, HttpResponseStatus status) {
        StringBuilder buf = new StringBuilder();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append("mitmproxy4J");
        buf.append("</title></head><body>\r\n<h1>\r\n");
        buf.append(body);
        buf.append("\r\n</h1>\r\n</body></html>\r\n");
        ByteBuf content = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        FullHttpResponse response =
                new DefaultFullHttpResponse(HTTP_1_1, status, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
