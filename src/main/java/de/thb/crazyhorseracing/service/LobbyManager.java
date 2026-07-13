package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.GameMap;
import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.GameMapProvider;
import de.thb.crazyhorseracing.repository.PlayerRepository;
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
    private final PlayerRepository playerRepository;

    public LobbyManager(GameMapProvider maps, ThreadPoolTaskExecutor executor, TaskScheduler scheduler, PlayerRepository playerRepository) {
        this.executor = executor;
        this.scheduler = scheduler;
        this.maps = maps;
        this.lobbies = new ArrayList<>();
        this.playerRepository = playerRepository;
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
            if (lobby.id == id) {
                return lobby;
            }
        }
        return null;
    }

    public boolean lobbyExists(int id) {
        return getLobby(id) != null;
    }

    public void savePlayers(List<Player> players) {
        playerRepository.saveAll(players);
    }

    public void onLobbyDestroyed(Lobby lobby) {
        savePlayers(lobby.players);
        removeLobby(lobby);
    }

    private Lobby createLobby(int minPlayers, int maxPlayers, Player player, GameMap gameMap) {
        minPlayers = Math.min(minPlayers, gameMap.maxPlayers());
        maxPlayers = Math.min(maxPlayers, gameMap.maxPlayers());
        Lobby lobby = new Lobby(minPlayers, maxPlayers, player, gameMap, executor, scheduler, this::onLobbyDestroyed);
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

        return createLobby(2, 999, player, maps.getRandomMap());
    }

    public List<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }

    public void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }
}
