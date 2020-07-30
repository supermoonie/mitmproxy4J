package com.github.supermoonie.ws;

import com.github.supermoonie.ws.support.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author supermoonie
 * @date 2020-07-30
 */
@ServerEndpoint("/ws/{sessionId}")
@Component
@Slf4j
public class WebSocketServer {

    private final static Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    private final static Map<String, Filter> FILTER_MAP = new ConcurrentHashMap<>();

    public static void sendText(final String message) {
        SESSION_MAP.values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sessionId") String sessionId) {
        InetSocketAddress remoteAddress = getRemoteAddress(session);
        String host = remoteAddress.getHostString();
        int port = remoteAddress.getPort();
        log.info("host: {}, port: {}", host, port);
        SESSION_MAP.put(sessionId, session);
        try {
            session.getBasicRemote().sendText("hello world!");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "sessionId") String sessionId) {
        SESSION_MAP.remove(sessionId);
        log.info("{} close", sessionId);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("sessionId: {}, message: {}", session.getId(), message);
        sendText(message);
    }

    public static InetSocketAddress getRemoteAddress(Session session) {
        if(session == null){
            return null;
        }
        RemoteEndpoint.Async async = session.getAsyncRemote();
        return (InetSocketAddress) getFieldInstance(async);
    }

    private static Object getFieldInstance(Object obj) {
        String[] fields = "base#socketWrapper#socket#sc#remoteAddress".split("#");
        for(String field : fields) {
            obj = getField(obj, obj.getClass(), field);
            if(obj == null) {
                return null;
            }
        }

        return obj;
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for(;clazz != Object.class; clazz = clazz.getSuperclass()) {
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
