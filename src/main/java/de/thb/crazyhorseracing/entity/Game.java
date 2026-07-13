package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import de.thb.crazyhorseracing.service.RandomService;
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

import static de.thb.crazyhorseracing.entity.Game.GameState.*;

@JsonIncludeProperties({"state", "horses", "winner"})
public class Game {
    public enum GameState {
        WAITING_FOR_PLAYERS("Waiting for players..."),
        READY_TO_PLAY("Ready to play!"),
        PLAYING("Playing"),
        GAME_OVER("Game over!"),
        TO_BE_DELETED("Deleting game...");

        private final String displayName;
        GameState(String displayName) {
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

    public static final AtomicInteger idCounter = new AtomicInteger(0);
    public static final long gameBeginningTimerDelayMillis = 5000;
    public static final long gameEndingTimerDelayMillis = 3000;

    public final int id;
    public final int minPlayers;
    public final int maxPlayers;

    @Getter
    @Setter
    private GameState state = WAITING_FOR_PLAYERS;

    public final ThreadPoolTaskExecutor executor;
    public final TaskScheduler scheduler;

    @Getter
    private ScheduledFuture<?> gameTimerTaskFuture;
    @Getter
    private GameTask gameTask;
    @Getter
    private Future<?> gameTaskFuture;

    public final Consumer<Game> destroyCallback;

    public final List<Horse> horses;
    public final GameMap map;

    @Getter
    private Player winner = null;

    public Game(int minPlayers, int maxPlayers, Player player, GameMap gameMap, ThreadPoolTaskExecutor executor, TaskScheduler scheduler, Consumer<Game> destroyCallback) {
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
        this.horses = new ArrayList<>();
        this.map = gameMap;

        addPlayer(player);
    }

    public Horse getHorseOfPlayer(Player player) { return horses.stream().filter(h -> h.player.equals(player)).findFirst().orElse(null); }
    public boolean hasPlayer(Player player) {
        return getHorseOfPlayer(player) != null;
    }
    public boolean isReady() {
        return horses.size() >= minPlayers;
    }

    public boolean isEmpty() {
        return horses.isEmpty();
    }
    public boolean isPlayerAllowed(Player player) {
        return hasPlayer(player);
    }
    public List<Player> getPlayers() { return horses.stream().map(horse -> (horse.player)).toList(); }

    public boolean canAddPlayer(Player player) {
        if (state != WAITING_FOR_PLAYERS && state != READY_TO_PLAY) return false;
        if (hasPlayer(player)) return false;
        if (horses.size() >= maxPlayers) return false;
        if (player.getHorseType() == null) return false;
        if (getAvailableSpawnpoints().isEmpty()) return false;
        return true;
    }
    public boolean canRemovePlayer(Player player) {
        if (state == GameState.PLAYING || state == GameState.GAME_OVER) return false;
        if (!hasPlayer(player)) return false;
        return true;
    }
    public boolean isSpawnpointAvailable(Vec spawnpoint) {
        for (Horse horse : horses) {
            if (horse.getPos().isNear(spawnpoint)) {
                return false;
            }
        }
        return true;
    }
    public List<Vec> getAvailableSpawnpoints() {
        List<Vec> allSpawnpoints = map.horseSpawnpoints();
        List<Vec> availableSpawnpoints = new ArrayList<>();
        for (Vec spawnpoint : allSpawnpoints) {
            if (isSpawnpointAvailable(spawnpoint)) {
                availableSpawnpoints.add(spawnpoint);
            }
        }
        return availableSpawnpoints;
    }

    public boolean addPlayer(Player player) { // returns whether the player was actually added
        if (!canAddPlayer(player)) return false;
        List<Vec> spawnpoints = getAvailableSpawnpoints();
        Vec chosenSpawnpoint = spawnpoints.get(RandomService.nextInt(spawnpoints.size()));
        horses.add(new Horse(player.getHorseType(), player, chosenSpawnpoint, map.horseSize()));

        if (state == WAITING_FOR_PLAYERS && horses.size() >= minPlayers) {
            state = READY_TO_PLAY;
            startGameBeginningTimer();
        }

        return true;
    }

    public boolean removePlayer(Player player) { // returns whether the player was actually removed
        if (!canRemovePlayer(player)) return false;
        horses.remove(getHorseOfPlayer(player));

        if (state == READY_TO_PLAY && horses.size() < minPlayers) {
            state = WAITING_FOR_PLAYERS;
            stopGameBeginningTimer();
        }

        if (horses.isEmpty()) {
            destroy();
        }

        return true;
    }



    private synchronized void startGame(ThreadPoolTaskExecutor executor) {
        if (state != READY_TO_PLAY) return;
        if (gameTaskFuture != null && !gameTaskFuture.isDone()) return;

        gameTask = new GameTask(this);
        gameTaskFuture = executor.submit(gameTask);
    }

    private synchronized void destroy() {
        for (Horse horse : horses) {
            horse.player.addGame();
        }
        if (winner != null) winner.addWin();

        state = TO_BE_DELETED;
        destroyCallback.accept(this);
    }

    public synchronized void startGameEndingTimer() {
        if (state != GAME_OVER) return;
        scheduler.schedule(
            () -> destroy(),
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

    public void processGameBeginning() {
        // Give all horses random initial velocities
        double randomVelocity = map.initialRandomVelocity();
        for (Horse horse : horses) {
            // Pick a random point on the unit circle
            double x = RandomService.nextDouble();
            double y = Math.sqrt(1-x*x);
            x = RandomService.nextBoolean() ? x : -x; // Randomly invert x
            y = RandomService.nextBoolean() ? y : -y; // Randomly invert y

            horse.setVelocity(new Vec(randomVelocity*x, randomVelocity*y));
        }
        state = PLAYING;
    }



    public void win(Horse winnerHorse) {
        winner = winnerHorse.player;
        winnerHorse.setPos(map.carrot().hitbox().getAlgebraicCenter());
        for (Horse horse : horses) {
            horse.setVelocity(new Vec(0,0));
        }

        state = GAME_OVER;
        gameTaskFuture.cancel(true); // stops the game from processing
        startGameEndingTimer();
    }

    public void processStep(double dt_seconds) {
        dt_seconds = Math.clamp(dt_seconds, 0.000001, 0.25);

        // Apply velocities to horses
        for (Horse horse : horses) {
            horse.setPos(horse.getPos().applyVelocity(horse.getVelocity(), dt_seconds));
        }

        // Check if any horse intersects the carrots
        Hitbox carrotHitbox = map.carrot().hitbox();
        for (Horse horse : horses) {
            List<Vec> intersections = Hitbox.getIntersections(horse.getAbsoluteHitbox(), carrotHitbox);
            if (intersections.isEmpty()) continue;
            win(horse);
            return;
        }

        // Collision reflections
        for (Horse horse : horses) {
            for (Wall wall : map.walls()) {
                horse.reflectIfColliding(wall);
            }

            for (Horse horse2 : horses) {
                if (horse == horse2) continue;
                horse.reflectIfColliding(horse2);
            }
        }
    }

    // Returns whether the move was actually carried out
    public boolean doPlayerMove(Player player, MoveType moveType) {
        if (state != PLAYING) return false;
        Horse horse = getHorseOfPlayer(player);
        if (horse == null) return false;
        return horse.Move(moveType);
    }
}
