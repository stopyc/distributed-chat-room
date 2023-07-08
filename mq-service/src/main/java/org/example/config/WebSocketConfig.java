package org.example.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * @author YC104
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig{

    /**
     * 注入一个ServerEndpointExporter，该Bean会自动使用@ServerEndpoint注解声明的websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
    //
    //@Override
    //public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    //    registry
    //            //添加处理器到对应的路径
    //            .addHandler(new ServletWebSocketServerHandler(), "/ws")
    //            .setAllowedOrigins("*");
    //
    //}

}