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

    public static ConnectionInfo parseUri(String uri) {
        if (null == uri) {
            return null;
        }
        Matcher matcher = HOST_PORT_PATTERN.matcher(uri);
        if (matcher.find()) {
            ConnectionInfo info = new ConnectionInfo();
            String host = matcher.group("host");
            String portStr = matcher.group("port");
            int port;
            if (null == portStr) {
                port = uri.startsWith("https://") ? 443 : 80;
            } else {
             port = Integer.parseInt(portStr);
            }
            info.setRemoteHost(host);
            info.setRemotePort(port);
            return info;
        } else {
            return null;
        }
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
                String uri = request.uri();
                Matcher matcher = HOST_PORT_PATTERN.matcher(uri);
                if (matcher.find()) {
                    String portStr = matcher.group("port");
                    if (null == portStr) {
                        if (uri.startsWith("https://")) {
                            info.setRemotePort(443);
                        } else if (uri.startsWith("http://")) {
                            info.setRemotePort(80);
                        } else {
                            return null;
                        }
                    } else {
                        int port = Integer.parseInt(portStr);
                        info.setRemotePort(port);
                    }
                } else {
                    return null;
                }

            }
        }
        return info;
    }

    public static void main(String[] args) {
        String url = "http://ocsp.apple.com/ocsp03-devid06/ME4wTKADAgEAMEUwQzBBMAkGBSsOAwIaBQAEFDOB0e%2FbaLCFIU0u76%2BMSmlkPCpsBBRXF%2B2iz9x8mKEQ4Py%2Bhy0s8uMXVAIIdVIDQAdHsMs%3D";
        Matcher matcher = HOST_PORT_PATTERN.matcher(url);
        if (matcher.find()) {
            System.out.println(matcher.group("host"));
            System.out.println(matcher.group("port"));
        }
    }
}
