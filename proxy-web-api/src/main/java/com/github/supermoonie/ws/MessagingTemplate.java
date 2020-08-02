package com.github.supermoonie.ws;

import com.github.supermoonie.util.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * @author supermoonie
 * @since 2020/8/2
 */
@Component
@Slf4j
public class MessagingTemplate {

    public void sendJson(final Object object) {
        send(session -> {
            try {
                session.getBasicRemote().sendText(JSON.toJsonString(object));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public void sendText(final String message) {
        send(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public void sendBinary(final ByteBuffer buffer, boolean isLat) {
        send(session -> {
            try {
                session.getBasicRemote().sendBinary(buffer, isLat);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public void sendObject(final Object object) {
        send(session -> {
            try {
                session.getBasicRemote().sendObject(object);
            } catch (IOException | EncodeException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public void send(Consumer<Session> consumer) {
        WebSocketServer.SESSION_MAP.values().forEach(sessions -> {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    consumer.accept(session);
                }
            }
        });
    }

}
