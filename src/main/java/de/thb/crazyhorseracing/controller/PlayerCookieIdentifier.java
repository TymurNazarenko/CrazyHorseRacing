package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.service.PlayerManager;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class PlayerCookieIdentifier implements HandshakeInterceptor {
    private final PlayerManager players;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        try {
            if (!(request instanceof ServletServerHttpRequest servletRequest)) return false;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            Player player = players.getPlayer(httpServletRequest);
            attributes.put("player", player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public PlayerCookieIdentifier(PlayerManager players) {
        this.players = players;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {}
}
