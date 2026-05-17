package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.service.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final GameStateUpdaterWebSocket updater_handler;
    private final PlayerMoveHandlerWebsocket move_handler;
    private final LobbyManager lobbies;
    private final PlayerManager players;

    public WebSocketConfig(GameStateUpdaterWebSocket updater_handler, PlayerMoveHandlerWebsocket move_handler, LobbyManager lobbies, PlayerManager players) {
        this.updater_handler = updater_handler;
        this.move_handler = move_handler;
        this.lobbies = lobbies;
        this.players = players;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(updater_handler, "/ws/game/*/status").addInterceptors(new GameHandshakeInterceptor(lobbies, players)).setAllowedOrigins("*");
        registry.addHandler(move_handler, "/ws/game/*/move").addInterceptors(new GameHandshakeInterceptor(lobbies, players)).setAllowedOrigins("*");
    }
}