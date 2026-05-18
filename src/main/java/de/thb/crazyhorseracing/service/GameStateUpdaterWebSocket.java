package de.thb.crazyhorseracing.service;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameStateUpdaterWebSocket extends TextWebSocketHandler {
    // gameId → sessions
    private final Map<Integer, Set<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        Integer gameId = extractGameId(session);

        session.getAttributes().put("gameId", gameId);

        Set<WebSocketSession> sessions = gameSessions.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet());
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        Integer gameId = (Integer) session.getAttributes().get("gameId");

        Set<WebSocketSession> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    public void broadcast(Integer gameId, String message) {
        Set<WebSocketSession> sessions = gameSessions.get(gameId);

        if (sessions == null) return;

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception ignored) {}
            }
        }
    }

    private Integer extractGameId(WebSocketSession session) {
        String path = session.getUri().getPath();
        // /ws/game/{id}/...
        String[] parts = path.split("/");
        return Integer.parseInt(parts[3]); // ["", "ws", "game", "{id}"]
    }
}