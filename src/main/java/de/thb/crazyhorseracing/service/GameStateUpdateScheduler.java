package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.Lobby;
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

    @Scheduled(fixedRate = 500)
    public void pushUpdates() {
        for (Lobby lobby : lobbies.getLobbies()) {
            handler.broadcast(lobby.getId(), jsonMapper.writeValueAsString(lobby.getGame().getHorses())); // TODO add a way to distinguish players from one another from the clientside
        }
    }
}
