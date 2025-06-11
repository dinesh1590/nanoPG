package com.NPG.nanoPG.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChatHandler extends TextWebSocketHandler {
      // Map of userId → WebSocketSession
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // Map of sessionId → userInfo
    private final Map<String, UserInfo> userInfoMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // New connection
        System.out.println("New connection: " + session.getId());
        // Wait for "join" message to add user info
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> msg = objectMapper.readValue(payload, Map.class);

        String type = (String) msg.get("type");

        if ("join".equals(type)) {
            // User joining
            String name = (String) msg.get("name");
            int age = (Integer) msg.get("age");
            String gender = (String) msg.get("gender");

            String userId = session.getId();
            UserInfo userInfo = new UserInfo(userId, name, age, gender);
            sessions.put(userId, session);
            userInfoMap.put(userId, userInfo);

            // Send back init with userId
            Map<String, Object> initMsg = Map.of(
                    "type", "init",
                    "userId", userId
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(initMsg)));

            // Broadcast user_list to all users
            broadcastUserList();

            // Notify others that user is online
            Map<String, Object> onlineMsg = Map.of(
                    "type", "user_online",
                    "user", userInfo
            );
            broadcastToAll(onlineMsg);
        }

        else if ("chat".equals(type)) {
            // Personal chat
            String toId = (String) msg.get("toId");
            String fromId = (String) msg.get("fromId");
            String fromName = (String) msg.get("fromName");
            String content = (String) msg.get("content");

            Map<String, Object> chatMsg = Map.of(
                    "type", "chat",
                    "fromId", fromId,
                    "fromName", fromName,
                    "content", content
            );

            // Send only to recipient
            WebSocketSession recipientSession = sessions.get(toId);
            if (recipientSession != null && recipientSession.isOpen()) {
                recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMsg)));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = session.getId();
        sessions.remove(userId);
        userInfoMap.remove(userId);

        // Broadcast user_offline
        Map<String, Object> offlineMsg = Map.of(
                "type", "user_offline",
                "userId", userId
        );
        broadcastToAll(offlineMsg);

        broadcastUserList();
    }

    private void broadcastUserList() throws Exception {
        List<UserInfo> userList = new ArrayList<>(userInfoMap.values());
        Map<String, Object> userListMsg = Map.of(
                "type", "user_list",
                "users", userList
        );
        broadcastToAll(userListMsg);
    }

    private void broadcastToAll(Map<String, Object> msg) throws Exception {
        String json = objectMapper.writeValueAsString(msg);
        TextMessage textMessage = new TextMessage(json);
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(textMessage);
            }
        }
    }

    // UserInfo class
    public static class UserInfo {
        public String id;
        public String name;
        public int age;
        public String gender;

        public UserInfo() {}

        public UserInfo(String id, String name, int age, String gender) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
        }
    }
}
