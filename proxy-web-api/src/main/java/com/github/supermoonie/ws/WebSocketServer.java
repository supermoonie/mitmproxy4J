package com.github.supermoonie.ws;

import com.github.supermoonie.ws.support.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author supermoonie
 * @date 2020-07-30
 */
@ServerEndpoint("/ws/flow")
@Component
@Slf4j
public class WebSocketServer {

    final static LinkedMultiValueMap<String, Session> SESSION_MAP = new LinkedMultiValueMap<>();

    final static Map<String, Filter> FILTER_MAP = new ConcurrentHashMap<>();

    @Resource
    private MessagingTemplate messagingTemplate;

    @OnOpen
    public void onOpen(Session session) {
        InetSocketAddress remoteAddress = getRemoteAddress(session);
        String host = remoteAddress.getHostString();
        int port = remoteAddress.getPort();
        log.info("host: {}, port: {} open", host, port);
        synchronized (SESSION_MAP) {
            SESSION_MAP.add(host, session);
        }
        FILTER_MAP.put(String.format("%s:%d", host, port), new Filter());
    }

    @OnError
    public void onError(Throwable ignore) {
        log.error(ignore.getMessage(), ignore);
        synchronized (SESSION_MAP) {
            for (String key : SESSION_MAP.keySet()) {
                List<Session> sessions = SESSION_MAP.get(key);
                if (!CollectionUtils.isEmpty(sessions)) {
                    sessions.removeIf(Session::isOpen);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        InetSocketAddress remoteAddress = getRemoteAddress(session);
        if (null == remoteAddress) {
            synchronized (SESSION_MAP) {
                for (String key : SESSION_MAP.keySet()) {
                    List<Session> sessions = SESSION_MAP.get(key);
                    if (!CollectionUtils.isEmpty(sessions)) {
                        sessions.removeIf(Session::isOpen);
                    }
                }
            }
            return;
        }
        String host = remoteAddress.getHostString();
        int port = remoteAddress.getPort();
        synchronized (SESSION_MAP) {
            List<Session> sessions = SESSION_MAP.get(host);
            if (!CollectionUtils.isEmpty(sessions)) {
                sessions.remove(session);
            }
        }
        FILTER_MAP.remove(String.format("%s:%d", host, port));
        log.info("host: {}, port: {} closed", host, port);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        InetSocketAddress remoteAddress = getRemoteAddress(session);
        String host = remoteAddress.getHostString();
        int port = remoteAddress.getPort();
        log.info("host: {}, port: {}, message: {}", host, port, message);
        messagingTemplate.sendText(message);
    }

    public static InetSocketAddress getRemoteAddress(Session session) {
        if (session == null) {
            return null;
        }
        RemoteEndpoint.Async async = session.getAsyncRemote();
        return (InetSocketAddress) getFieldInstance(async);
    }

    private static Object getFieldInstance(Object obj) {
        String[] fields = "base#socketWrapper#socket#sc#remoteAddress".split("#");
        for (String field : fields) {
            obj = getField(obj, obj.getClass(), field);
            if (obj == null) {
                return null;
            }
        }

        return obj;
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field;
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception ignore) {
            }
        }
        return null;
    }
}
