package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.Game;
import de.thb.crazyhorseracing.entity.MoveType;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.service.GameManager;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class PlayerMoveHandlerWS extends TextWebSocketHandler {
    private final GameManager games;

    public PlayerMoveHandlerWS(GameManager games) {
        this.games = games;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        Player player = (Player) session.getAttributes().get("player");
        if (player == null) return;

        Game game = games.getGame(player);
        if (game == null) return;

        String payload = message.getPayload();
        try {
            MoveType moveType = MoveType.valueOf(payload);
            game.doPlayerMove(player, moveType);
        } catch (IllegalArgumentException e) {
            return;
        }
    }
}
