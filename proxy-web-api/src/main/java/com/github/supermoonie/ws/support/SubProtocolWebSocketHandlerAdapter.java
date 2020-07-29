//package com.github.supermoonie.ws.support;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.SubscribableChannel;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
//
///**
// * @author supermoonie
// * @date 2020-07-29
// */
//@Component
//@Slf4j
//public class SubProtocolWebSocketHandlerAdapter extends SubProtocolWebSocketHandler {
//
//    /**
//     * Create a new {@code SubProtocolWebSocketHandler} for the given inbound and outbound channels.
//     *
//     * @param clientInboundChannel  the inbound {@code MessageChannel}
//     * @param clientOutboundChannel the outbound {@code MessageChannel}
//     */
//    public SubProtocolWebSocketHandlerAdapter(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel) {
//        super(clientInboundChannel, clientOutboundChannel);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//        super.afterConnectionClosed(session, closeStatus);
//        log.info("{} closed ", session.getId());
//    }
//}
