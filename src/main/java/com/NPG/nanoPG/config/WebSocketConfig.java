package com.NPG.nanoPG.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // @Override
    // public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    //     registry.addHandler(new ChatHandler(), "/ws").setAllowedOrigins("*");
    // }
@Override
public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new ChatHandler(), "/ws")
            .setAllowedOrigins("https://riddlemesilly.netlify.app", "http://localhost:4200");
 }
}
