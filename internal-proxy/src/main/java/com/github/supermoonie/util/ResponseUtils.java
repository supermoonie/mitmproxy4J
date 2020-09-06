package com.github.supermoonie.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author supermoonie
 * @date 2020-06-08
 */
public class ResponseUtils {

    public static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void sendText(ChannelHandlerContext ctx, String text) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(text, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response);
    }

    public static void sendHtml(ChannelHandlerContext ctx, String html) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(html, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response);
    }

    public static void sendHtml(Channel channel, String html) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(html, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        channel.writeAndFlush(response);
    }

    public static void sendError(Channel channel, String error) {
        error = "<h1>Error: " + error + "</h1>";
        HttpResponse response = htmlResponse(error, HttpResponseStatus.OK);
        // Close the connection as soon as the error message is sent.
        channel.writeAndFlush(response).addListener((ChannelFutureListener) future -> channel.close());
    }

    public static FullHttpResponse htmlResponse(String body, HttpResponseStatus status) {
        StringBuilder buf = new StringBuilder();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append("mitmproxy4J");
        buf.append("</title></head><body>\r\n");
        buf.append(body);
        buf.append("\r\n</body></html>\r\n");
        FullHttpResponse response =
                new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        return response;
    }
}
