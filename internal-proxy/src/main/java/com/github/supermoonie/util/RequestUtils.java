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
//    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("^(?:https?://)?(?<host>[^/]*)/?.*$");

    private static final Pattern IP_PATTERN = Pattern.compile("^(?:https?://)?(?<ip>\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(?<port>\\d+)$");

    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("^(?:https?://)?(?<host>[^:/]*):?(?<port>\\d+)?(?:/.*)?$");

    private RequestUtils() {
    }

    public static int tryParsePort(String uri) {
        if (null == uri || uri.trim().length() == 0) {
            return -1;
        }
        Matcher matcher = HOST_PORT_PATTERN.matcher(uri);
        if (matcher.find()) {
            String port = matcher.group("port");
            if (null != port) {
                return Integer.parseInt(port);
            }
        }
        return uri.startsWith("https") ? 443 : uri.startsWith("http") ? 80 : -1;
    }

    public static ConnectionInfo parseRemoteInfo(HttpRequest request, ConnectionInfo info) {
        if (null == info) {
            info = new ConnectionInfo();
        }
        String host = request.headers().get(HttpHeaderNames.HOST);
        String uri = request.uri();
        int port = -1;
        Matcher matcher = HOST_PORT_PATTERN.matcher(uri);
        if (matcher.find()) {
            if (null == host) {
                host = matcher.group("host");
                if (null == host) {
                    return null;
                }
            }
            String portStr = matcher.group("port");
            if (null != portStr) {
                port = Integer.parseInt(portStr);
            }
        }
        if (null == host) {
            return null;
        }
        port = -1 == port ? (uri.startsWith("https") ? 443 : uri.startsWith("http") ? 80 : -1 ): port;
        if (info.isHttps()) {
            port = 443;
        }
        if (-1 == port) {
            return null;
        }
        info.setRemoteHost(host);
        info.setRemotePort(port);
        return info;
    }
}
