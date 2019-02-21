package com.forteach.websocket.config;

import com.forteach.websocket.filter.WebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-21 10:35
 * @version: 1.0
 * @description:
 */
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebStompConfig implements WebSocketMessageBrokerConfigurer {
//    @Autowired
//    private WebSocketInterceptor webSocketInterceptor;
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry){
//        registry.addEndpoint("/interactive/**").setAllowedOrigins("*").withSockJS();
//    }
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //定义了两个客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息
//        registry.enableSimpleBroker("/message", "/notice");
        //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀
//        registry.setApplicationDestinationPrefixes("/**");
//    }
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        //注册拦截客户端通道信息
//        registration.interceptors(webSocketInterceptor);
//    }
//    private final WebSocketInterceptor webSocketInterceptor;
//    private WebStompConfig(WebSocketInterceptor webSocketInterceptor){
//        this.webSocketInterceptor = webSocketInterceptor;
//    }
//}
