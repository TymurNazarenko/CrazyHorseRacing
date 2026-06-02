package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static de.thb.crazyhorseracing.entity.LobbyState.*;

public class Lobby {
    @Getter
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    @Getter
    private static final long timerDelayMillis = 5000;
    @Getter
    private final int id;
    @Getter
    private final int minPlayers;
    @Getter
    private final int maxPlayers;
    @Getter
    private final List<Player> players;
    @Getter
    private final Game game;
    @Getter
    @Setter
    private LobbyState lobbyState = WAITING_FOR_PLAYERS;


    @Getter
    private final ThreadPoolTaskExecutor executor;
    @Getter
    private final TaskScheduler scheduler;

    @Getter
    private ScheduledFuture<?> gameTimerTaskFuture;
    @Getter
    private GameTask gameTask;
    @Getter
    private Future<?> gameTaskFuture;

    public Lobby(int minPlayers, int maxPlayers, Player player, GameMap gameMap, ThreadPoolTaskExecutor executor, TaskScheduler scheduler) {
        if (minPlayers <= 1 || maxPlayers <= 1) {
            throw new IllegalArgumentException("minPlayers and maxPlayers must be greater than 1");
        }
        if (maxPlayers < minPlayers) {
            throw new IllegalArgumentException("maxPlayers must be at least as large as minPlayers");
        }
        if (maxPlayers >= gameMap.maxPlayers()) {
            throw new IllegalArgumentException("maxPlayers can't exceed the maxPlayers of the map!");
        }

        this.id = idCounter.incrementAndGet();
        this.executor = executor;
        this.scheduler = scheduler;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        players = new ArrayList<>();
        players.add(player);
        this.game = new Game(this, gameMap);
        game.addHorse(player);
    }

    public Lobby(int playerNum, Player player, GameMap gameMap, ThreadPoolTaskExecutor executor, TaskScheduler scheduler) {
        this(playerNum, playerNum, player, gameMap, executor, scheduler);
    }

    public boolean canAddPlayer(Player player) {
        if (lobbyState == PLAYING || lobbyState == GAME_OVER) return false;
        if (hasPlayer(player)) return false;
        if (players.size() >= maxPlayers) return false; // if this case occurs, there is an error in the logic
        if (player.getHorseType() == null) return false;
        return true;
    }

    public boolean addPlayer(Player player) { // returns whether the player was actually added
        if (!canAddPlayer(player)) return false;
        players.add(player);
        game.addHorse(player);

        if (lobbyState == WAITING_FOR_PLAYERS && players.size() >= minPlayers) {
            lobbyState = READY_TO_PLAY;
            startGameTimer();
        }

        return true;
    }

    public boolean canRemovePlayer(Player player) {
        if (lobbyState == PLAYING || lobbyState == GAME_OVER) return false;
        if (!hasPlayer(player)) return false;
        return true;
    }

    public boolean removePlayer(Player player) { // returns whether the player was actually removed
        if (!canRemovePlayer(player)) return false;
        players.remove(player);

        if (lobbyState == READY_TO_PLAY && players.size() < minPlayers) {
            lobbyState = WAITING_FOR_PLAYERS;
            stopGameTimer();
        }

        return true;
    }

    public boolean isReady() {
        return players.size() >= minPlayers;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    private synchronized void startGame(ThreadPoolTaskExecutor executor) {
        if (gameTaskFuture != null && !gameTaskFuture.isDone()) return;

        gameTask = new GameTask(game);
        gameTaskFuture = executor.submit(gameTask);
        game.setGameTaskHandler(gameTaskFuture);
    }

    public synchronized void startGameTimer() {
        if (lobbyState != READY_TO_PLAY) return;
        stopGameTimer();

        gameTimerTaskFuture = scheduler.schedule(
                () -> startGame(executor),
                new Date(System.currentTimeMillis() + timerDelayMillis)
        );
    }

    public synchronized void stopGameTimer() {
        if (gameTimerTaskFuture == null) return;
        gameTimerTaskFuture.cancel(false);
        gameTimerTaskFuture = null;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isPlayerAllowed(Player player) {
        return hasPlayer(player);
    }
}
