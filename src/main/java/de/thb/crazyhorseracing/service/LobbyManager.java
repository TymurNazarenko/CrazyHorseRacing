package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.GameMap;
import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.GameMapListLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class LobbyManager {
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private TaskScheduler scheduler;
    private List<Lobby> lobbies;
    private final GameMapListLoader maps;

    public LobbyManager(GameMapListLoader maps) {
        this.maps = maps;
    }

    @PostConstruct
    public void init() {
        lobbies = new ArrayList<>();
    }

    public Optional<Lobby> getLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.hasPlayer(player)) {
                return Optional.of(lobby);
            }
        }
        return Optional.empty();
    }

    public boolean playerHasLobby(Player player) {
        return getLobby(player).isPresent();
    }

    public Optional<Lobby> getLobby(int id) {
        for (Lobby lobby : lobbies) {
            if (lobby.getId() == id) {
                return Optional.of(lobby);
            }
        }
        return Optional.empty();
    }

    public boolean lobbyExists(int id) {
        return getLobby(id).isPresent();
    }

    private Lobby createLobby(int minPlayers, int maxPlayers, Player player, GameMap gameMap) {
        Lobby lobby = new Lobby(minPlayers, maxPlayers, player, gameMap, executor, scheduler);
        lobbies.add(lobby);
        return lobby;
    }

    public Optional<Lobby> findSuitableLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.canAddPlayer(player)) {
                lobby.addPlayer(player);
                return Optional.of(lobby);
            }
        }
        return Optional.empty();
    }

    public Lobby getJoinOrCreateLobby(Player player) {
        Optional<Lobby> existingLobby = getLobby(player);
        if (existingLobby.isPresent()) return existingLobby.get();

        Optional<Lobby> joinedLobby = findSuitableLobby(player);
        if (joinedLobby.isPresent()) return joinedLobby.get();

        return createLobby(2, 2, player, maps.getMapById(1).get()); // TODO no hardcoding
    }

    public List<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }
}
