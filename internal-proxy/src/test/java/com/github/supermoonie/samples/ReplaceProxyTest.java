package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.nio.charset.StandardCharsets;

/**
 * @author supermoonie
 * @since 2020/9/18
 */
public class ReplaceProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> responseIntercepts.put("response-intercept-0", (ctx, request, response) -> {
            if (response.headers().get(HttpHeaderNames.CONTENT_TYPE).startsWith("text/html")) {
                ByteBuf content = response.content();
                content.markReaderIndex();
                byte[] bytes = new byte[content.readableBytes()];
                content.readBytes(bytes);
                String html = new String(bytes, StandardCharsets.UTF_8);
                html = html.replaceAll("(http://)(cdn|welfare)", "https://$2");
                content.resetReaderIndex();
                ByteBuf buf = Unpooled.wrappedBuffer(html.getBytes(StandardCharsets.UTF_8));
                content.release();
                FullHttpResponse httpResponse = response.replace(buf);
                httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
                return httpResponse;
            }
            return response;
        }));
        proxy.setPort(10801);
        proxy.start();
    }
}
