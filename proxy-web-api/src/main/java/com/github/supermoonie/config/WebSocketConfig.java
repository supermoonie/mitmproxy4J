package com.github.supermoonie.config;

import com.github.supermoonie.config.support.StompPrincipal;
import com.github.supermoonie.ws.support.Filter;
import com.github.supermoonie.ws.support.FilterContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author supermoonie
 * @date 2020-07-28
 */
@Configuration
//@EnableWebSocketMessageBroker
public class WebSocketConfig {

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic");
//        config.setApplicationDestinationPrefixes("/flow");
//        config.setUserDestinationPrefix("/flow");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOrigins("*")
//                .setHandshakeHandler(new DefaultHandshakeHandler() {
//                    @Override
//                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//                        InetSocketAddress remoteAddress = request.getRemoteAddress();
//                        String host = remoteAddress.getHostString();
//                        int port = remoteAddress.getPort();
//                        String id = host + ":" + port;
//                        FilterContext.addFilter(id, new Filter());
//                        System.out.println("address: " + id);
//                        return new StompPrincipal(id);
//                    }
//                })
//                .withSockJS();
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                StompCommand command = accessor.getCommand();
//
//                if (StompCommand.DISCONNECT.equals(command)){
//                    String id = Objects.requireNonNull(accessor.getUser()).getName();
//                    System.out.println("dis address: " + id);
//                    FilterContext.removeFilter(id);
//                }
//            }
//        });
//    }


}
