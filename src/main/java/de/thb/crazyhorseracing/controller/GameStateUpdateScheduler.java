package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.Game;
import de.thb.crazyhorseracing.service.GameManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class GameStateUpdateScheduler {
    private final GameStateUpdateWS handler;
    private final GameManager games;
    private final ObjectMapper jsonMapper;

    public GameStateUpdateScheduler(GameStateUpdateWS handler, GameManager games) {
        this.handler = handler;
        this.games = games;
        this.jsonMapper = new ObjectMapper();
    }

    @Scheduled(fixedRate = 10) // every 0.01 seconds (unrealistic, but much better clientside for now)
    public void pushUpdates() {
        for (Game game : games.getGames()) {
            handler.broadcast(game.id, jsonMapper.writeValueAsString(game));
        }
    }
}
