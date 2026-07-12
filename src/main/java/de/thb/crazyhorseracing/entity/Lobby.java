package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonValue;
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
import java.util.function.Consumer;

import static de.thb.crazyhorseracing.entity.Lobby.LobbyState.*;

@JsonIncludeProperties({"state"})
public class Lobby {
    public enum LobbyState {
        WAITING_FOR_PLAYERS("Waiting for players..."),
        READY_TO_PLAY("Ready to play!"),
        PLAYING("Playing"),
        GAME_OVER("Game over!"),
        TO_BE_DELETED("Deleting lobby...");

        private final String displayName;
        LobbyState(String displayName) {
            this.displayName = displayName;
        }

        @JsonValue
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    @Getter
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    @Getter
    private static final long gameBeginningTimerDelayMillis = 5000;
    @Getter
    private static final long gameEndingTimerDelayMillis = 3000;

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
    private LobbyState state = WAITING_FOR_PLAYERS;


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

    public final Consumer<Lobby> destroyCallback;

    public Lobby(int minPlayers, int maxPlayers, Player player, GameMap gameMap, ThreadPoolTaskExecutor executor, TaskScheduler scheduler, Consumer<Lobby> destroyCallback) {
        if (minPlayers <= 1 || maxPlayers <= 1) {
            throw new IllegalArgumentException("minPlayers and maxPlayers must be greater than 1");
        }
        if (maxPlayers < minPlayers) {
            throw new IllegalArgumentException("maxPlayers must be at least as large as minPlayers");
        }
        if (maxPlayers > gameMap.maxPlayers()) {
            throw new IllegalArgumentException("maxPlayers can't exceed the maxPlayers of the map!");
        }

        this.id = idCounter.incrementAndGet();
        this.executor = executor;
        this.scheduler = scheduler;
        this.destroyCallback = destroyCallback;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        players = new ArrayList<>();
        players.add(player);
        this.game = new Game(this, gameMap);
        game.addHorse(player);
    }

    public boolean canAddPlayer(Player player) {
        if (state != WAITING_FOR_PLAYERS && state != READY_TO_PLAY) return false;
        if (hasPlayer(player)) return false;
        if (players.size() >= maxPlayers) return false; // if this case occurs, there is an error in the logic
        if (player.getHorseType() == null) return false;
        return true;
    }

    public boolean addPlayer(Player player) { // returns whether the player was actually added
        if (!canAddPlayer(player)) return false;
        players.add(player);
        game.addHorse(player);

        if (state == WAITING_FOR_PLAYERS && players.size() >= minPlayers) {
            state = READY_TO_PLAY;
            startGameBeginningTimer();
        }

        return true;
    }

    public boolean canRemovePlayer(Player player) {
        if (state == LobbyState.PLAYING || state == LobbyState.GAME_OVER) return false;
        if (!hasPlayer(player)) return false;
        return true;
    }

    public boolean removePlayer(Player player) { // returns whether the player was actually removed
        if (!canRemovePlayer(player)) return false;
        players.remove(player);

        if (state == READY_TO_PLAY && players.size() < minPlayers) {
            state = WAITING_FOR_PLAYERS;
            stopGameBeginningTimer();
        }

        if (players.isEmpty()) {
            destroyGameAndLobby();
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
        if (state != READY_TO_PLAY) return;
        if (gameTaskFuture != null && !gameTaskFuture.isDone()) return;

        gameTask = new GameTask(game);
        gameTaskFuture = executor.submit(gameTask);
        game.setGameTaskHandler(gameTaskFuture);
    }

    private synchronized void destroyGameAndLobby() {
        for (Player player : players) {
            player.addGame();
        }

        Player winner = game.getWinner();
        if (winner != null) winner.addWin();

        state = TO_BE_DELETED;
        destroyCallback.accept(this);
    }

    public synchronized void startGameEndingTimer() {
        if (state != GAME_OVER) return;
        scheduler.schedule(
            () -> destroyGameAndLobby(),
            new Date(System.currentTimeMillis() + gameEndingTimerDelayMillis)
        );
    }

    public synchronized void startGameBeginningTimer() {
        if (state != READY_TO_PLAY) return;
        stopGameBeginningTimer();

        gameTimerTaskFuture = scheduler.schedule(
            () -> startGame(executor),
            new Date(System.currentTimeMillis() + gameBeginningTimerDelayMillis)
        );
    }

    public synchronized void stopGameBeginningTimer() {
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
