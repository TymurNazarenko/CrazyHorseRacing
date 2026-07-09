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
    private final PlayerManager players;

    public PlayerMoveHandlerWS(LobbyManager lobbies, PlayerManager players) {
        this.lobbies = lobbies;
        this.players = players;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        String jsessionId = (String) session.getAttributes().get("JSESSIONID");
        Player player = players.getPlayer(jsessionId);
        if (player == null) return;

        Lobby lobby = lobbies.getLobby(player);
        if (lobby == null) return;

        String payload = message.getPayload();
        try {
            MoveType moveType = MoveType.valueOf(payload);
            lobby.getGame().doPlayerMove(player, moveType);
        } catch (IllegalArgumentException e) {
            // TODO
            return;
        }
    }
}
