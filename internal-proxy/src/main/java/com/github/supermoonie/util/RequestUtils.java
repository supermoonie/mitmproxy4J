package com.github.supermoonie.util;

import com.github.supermoonie.proxy.ConnectionInfo;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author supermoonie
 * @since 2020/8/9
 */
public final class RequestUtils {

    /**
     * httpbin.org:443
     * /get
     * https://httpbin.org/get
     * http://httpbin.org/get
     * 54.236.246.173:443
     * /index.php/vod/play/id/124615/sid/1/nid/1.html
     */
    private static final Pattern IP_PATTERN = Pattern.compile("^(?:https?://)?(?<ip>\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(?<port>\\d+)$");

    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("^(?:https?://)?(?<host>[^:/]*):?(?<port>\\d+)?(?:/.*)?$");

    private RequestUtils() {
    }

    public static ConnectionInfo parseRemoteInfo(HttpRequest request, ConnectionInfo info) {
        if (null == info) {
            info = new ConnectionInfo();
        }
        String host = request.headers().get(HttpHeaderNames.HOST);
        if (null == host) {
            String uri = request.uri();
            Matcher matcher = HOST_PORT_PATTERN.matcher(uri);
            if (matcher.find()) {
                host = matcher.group("host");
                int port = Integer.parseInt(matcher.group("port"));
                info.setRemoteHost(host);
                info.setRemotePort(port);
            } else {
                return null;
            }
        } else {
            String[] hostAndPort = host.split(":");
            info.setRemoteHost(hostAndPort[0]);
            if (hostAndPort.length == 2) {
                info.setRemotePort(Integer.parseInt(hostAndPort[1]));
            } else {
                if (request.uri().startsWith("https://")) {
                    info.setRemotePort(443);
                } else {
                    info.setRemotePort(80);
                }
            }
        }
        return info;
    }
}
