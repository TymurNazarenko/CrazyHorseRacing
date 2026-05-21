package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.service.LobbyManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class GameStateUpdateScheduler {
    private final GameStateUpdaterWebSocket handler;
    private final LobbyManager lobbies;
    private final ObjectMapper jsonMapper;

    public GameStateUpdateScheduler(GameStateUpdaterWebSocket handler, LobbyManager lobbies) {
        this.handler = handler;
        this.lobbies = lobbies;
        this.jsonMapper = new ObjectMapper();
    }

    @Scheduled(fixedRate = 200)
    public void pushUpdates() {
        for (Lobby lobby : lobbies.getLobbies()) {
            handler.broadcast(lobby.getId(), jsonMapper.writeValueAsString(lobby.getGame()));
        }
    }
}
