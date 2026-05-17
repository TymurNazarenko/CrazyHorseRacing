package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.MoveType;
import de.thb.crazyhorseracing.entity.Player;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;

@Component
public class PlayerMoveHandlerWebsocket extends TextWebSocketHandler {
    private final LobbyManager lobbies;
    private final PlayerManager players;

    public  PlayerMoveHandlerWebsocket(LobbyManager lobbies, PlayerManager players) {
        this.lobbies = lobbies;
        this.players = players;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String jsessionId = (String) session.getAttributes().get("JSESSIONID");
        Optional<Player> pl = players.getPlayer(jsessionId);
        if (pl.isEmpty()) return;
        Player player = pl.get();

        Optional<Lobby> lb = lobbies.getLobby(pl.get());
        if (lb.isEmpty()) return;
        Lobby lobby = lb.get();

        String payload = message.getPayload();
        try {
            MoveType moveType = MoveType.valueOf(payload);
            lobby.getGame().doPlayerMove(player, moveType); // TODO
            System.out.println("Received move: " + moveType); // todo remove
        } catch (IllegalArgumentException e) {
            // TODO
            return;
        }
    }
}
