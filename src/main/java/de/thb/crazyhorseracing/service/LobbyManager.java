package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.GameMap;
import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.GameMapProvider;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LobbyManager {
    private final ThreadPoolTaskExecutor executor;
    private final TaskScheduler scheduler;
    private final List<Lobby> lobbies;
    private final GameMapProvider maps;

    public LobbyManager(GameMapProvider maps, ThreadPoolTaskExecutor executor, TaskScheduler scheduler) {
        this.executor = executor;
        this.scheduler = scheduler;
        this.maps = maps;
        this.lobbies = new ArrayList<>();
    }

    public Lobby getLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.hasPlayer(player)) {
                return lobby;
            }
        }
        return null;
    }

    public boolean playerHasLobby(Player player) {
        return getLobby(player) != null;
    }

    public Lobby getLobby(int id) {
        for (Lobby lobby : lobbies) {
            if (lobby.getId() == id) {
                return lobby;
            }
        }
        return null;
    }

    public boolean lobbyExists(int id) {
        return getLobby(id) != null;
    }

    private Lobby createLobby(int minPlayers, int maxPlayers, Player player, GameMap gameMap) {
        Lobby lobby = new Lobby(minPlayers, maxPlayers, player, gameMap, executor, scheduler);
        lobbies.add(lobby);
        return lobby;
    }

    public Lobby joinSuitableLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.canAddPlayer(player)) {
                lobby.addPlayer(player);
                return lobby;
            }
        }
        return null;
    }

    public Lobby getJoinOrCreateLobby(Player player) {
        Lobby existingLobby = getLobby(player);
        if (existingLobby != null) return existingLobby;

        Lobby joinedLobby = joinSuitableLobby(player);
        if (joinedLobby != null) return joinedLobby;

        return createLobby(2, 2, player, maps.getMapById(1)); // TODO no hardcoding
    }

    public List<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }
}
