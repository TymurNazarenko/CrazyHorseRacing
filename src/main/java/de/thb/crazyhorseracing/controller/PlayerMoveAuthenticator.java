package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.service.LobbyManager;
import de.thb.crazyhorseracing.service.PlayerManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

public class PlayerMoveAuthenticator implements HandshakeInterceptor {
    private final LobbyManager lobbies;
    private final PlayerManager players;
    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        try {
            if (!(request instanceof ServletServerHttpRequest servletRequest)) return false;

            int gameId = extractGameId(request);
            Lobby lobby = lobbies.getLobby(gameId);
            if (lobby == null) return false;

            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            HttpSession session = httpRequest.getSession(false);
            if (session == null) return false;

            String jsessionId = session.getId();
            Player player = players.getPlayer(jsessionId);
            if (player == null || !lobby.hasPlayer(player)) return false;

            attributes.put("JSESSIONID", jsessionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public PlayerMoveAuthenticator(LobbyManager lobbies, PlayerManager players) {
        this.lobbies = lobbies;
        this.players = players;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        // optional
    }
    private int extractGameId(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        // /ws/game/{id}/...
        String[] parts = path.split("/");
        return Integer.parseInt(parts[3]); // ["", "ws", "game", "{id}"]
    }
}
