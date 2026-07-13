package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.GameMap;
import de.thb.crazyhorseracing.entity.Game;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.GameMapProvider;
import de.thb.crazyhorseracing.repository.PlayerRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameManager {
    private final ThreadPoolTaskExecutor executor;
    private final TaskScheduler scheduler;
    private final List<Game> games;
    private final GameMapProvider maps;
    private final PlayerRepository playerRepository;

    public GameManager(GameMapProvider maps, ThreadPoolTaskExecutor executor, TaskScheduler scheduler, PlayerRepository playerRepository) {
        this.executor = executor;
        this.scheduler = scheduler;
        this.maps = maps;
        this.games = new ArrayList<>();
        this.playerRepository = playerRepository;
    }

    public Game getGame(Player player) {
        for (Game game : games) {
            if (game.hasPlayer(player)) {
                return game;
            }
        }
        return null;
    }

    public boolean playerHasGame(Player player) {
        return getGame(player) != null;
    }

    public Game getGame(int id) {
        for (Game game : games) {
            if (game.id == id) {
                return game;
            }
        }
        return null;
    }

    public boolean gameExists(int id) {
        return getGame(id) != null;
    }

    public void savePlayers(List<Player> players) {
        playerRepository.saveAll(players);
    }

    public void onGameDestroyed(Game game) {
        savePlayers(game.getPlayers());
        removeGame(game);
    }

    private Game createGame(int minPlayers, int maxPlayers, Player player, GameMap gameMap) {
        minPlayers = Math.min(minPlayers, gameMap.maxPlayers());
        maxPlayers = Math.min(maxPlayers, gameMap.maxPlayers());
        Game game = new Game(minPlayers, maxPlayers, player, gameMap, executor, scheduler, this::onGameDestroyed);
        games.add(game);
        return game;
    }

    public Game joinSuitableGame(Player player) {
        for (Game game : games) {
            if (game.canAddPlayer(player)) {
                game.addPlayer(player);
                return game;
            }
        }
        return null;
    }

    public Game getJoinOrCreateGame(Player player) {
        Game existingGame = getGame(player);
        if (existingGame != null) return existingGame;

        Game joinedGame = joinSuitableGame(player);
        if (joinedGame != null) return joinedGame;

        return createGame(2, 999, player, maps.getRandomMap());
    }

    public List<Game> getGames() {
        return new ArrayList<>(games);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }
}
