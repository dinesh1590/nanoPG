package com.NPG.nanoPG.controller;


import com.NPG.nanoPG.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameSocketController {

    @MessageMapping("/chat.send")  // listen for messages sent to /app/chat.send
    @SendTo("/topic/messages")     // broadcast to /topic/messages subscribers
    public Message send(Message message) {
        // You can add logic here (save, enrich message, etc.)
        return message; // will be sent to clients subscribed to /topic/messages
    }
}
