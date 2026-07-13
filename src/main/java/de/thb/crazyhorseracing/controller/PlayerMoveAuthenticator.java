package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.Game;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.service.GameManager;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class PlayerMoveAuthenticator implements HandshakeInterceptor {
    private final GameManager games;
    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        try {
            if (!(request instanceof ServletServerHttpRequest servletRequest)) return false;

            int gameId = extractGameId(request);
            Game game = games.getGame(gameId);
            if (game == null) return false;

            Player player = (Player) attributes.get("player");
            if (player == null || !game.hasPlayer(player)) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int extractGameId(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        // /ws/game/{id}/...
        String[] parts = path.split("/");
        return Integer.parseInt(parts[3]); // ["", "ws", "game", "{id}"]
    }

    public PlayerMoveAuthenticator(GameManager games) {
        this.games = games;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, Exception exception) {}
}
