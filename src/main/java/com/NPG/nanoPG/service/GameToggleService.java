package com.NPG.nanoPG.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameToggleService {

    private final Map<String, Boolean> gameStates = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        gameStates.put("lightsOut", true);
        gameStates.put("wordHunt", true);
        gameStates.put("chat", true);
        gameStates.put("flames", true);
    }

    public Map<String, Boolean> getAll() {
        return gameStates;
    }

    public Boolean updateState(String key, Boolean enabled) {
        if (gameStates.containsKey(key)) {
            gameStates.put(key, enabled);
            return true;
        }
        return false;
    }

    public Map<String, Boolean> getAllAndChange() {

        gameStates.put("chat",!gameStates.get("chat"));
        gameStates.put("flames",!gameStates.get("chat"));

        return gameStates;
    }
}

