package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.MoveType;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.service.LobbyManager;
import de.thb.crazyhorseracing.service.PlayerManager;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class PlayerMoveHandlerWS extends TextWebSocketHandler {
    private final LobbyManager lobbies;

    public PlayerMoveHandlerWS(LobbyManager lobbies) {
        this.lobbies = lobbies;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        Player player = (Player) session.getAttributes().get("player");
        if (player == null) return;

        Lobby lobby = lobbies.getLobby(player);
        if (lobby == null) return;

        String payload = message.getPayload();
        try {
            MoveType moveType = MoveType.valueOf(payload);
            lobby.game.doPlayerMove(player, moveType);
        } catch (IllegalArgumentException e) {
            return;
        }
    }
}
