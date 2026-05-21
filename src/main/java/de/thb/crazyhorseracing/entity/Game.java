package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static de.thb.crazyhorseracing.entity.LobbyState.GAME_OVER;
import static de.thb.crazyhorseracing.entity.LobbyState.PLAYING;

public class Game {
    @JsonIgnore
    @Getter
    private final Lobby lobby; // needed to communicate game state back to the lobby
    @Getter
    private final List<Horse> horses;
    @JsonIgnore
    @Getter
    private final GameMap map;

    @Getter
    private Player winner = null;

    @JsonIgnore
    @Getter
    @Setter
    private Future<?> gameTaskHandler;

    public Game(Lobby lobby, GameMap map) {
        this.lobby = lobby;
        this.map = map;
        this.horses = new ArrayList<>();
    }

    public boolean isSpawnpointAvailable(Vec spawnpoint) {
        for (Horse horse : horses) {
            if (horse.getVec().isNear(spawnpoint)) {
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
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

    public boolean addHorse(Player player) { // Returns whether horse was actually added
        if (horses.size() >= map.maxPlayers()) return false;

        List<Vec> spawnpoints = getAvailableSpawnpoints();
        if  (spawnpoints.isEmpty()) return false;

        Vec chosenSpawnpoint = spawnpoints.get(0); // TODO choose randomly
        HorseType horseType = player.getHorseType().get();
        horses.add(new Horse(horseType, player, chosenSpawnpoint));

        return true;
    }

    public void processStep(double dt_seconds) {
        // TODO clamp dt_seconds to reasonable values

        // Apply velocities to horses
        for (Horse horse : horses) {
            Vec vec = horse.getVec();
            vec.applyVelocity(horse.getVelocity(), dt_seconds);
        }

        // Check if any horse intersects the carrots
        Hitbox carrotHitbox = map.carrot().hitbox();
        for (Horse horse : horses) {
            List<Vec> intersections = horse.getAbsoluteHitbox().getIntersections(carrotHitbox);
            if (intersections.size() <= 0) continue;
            winner = horse.getPlayer();
            lobby.setLobbyState(GAME_OVER);
            gameTaskHandler.cancel(true); // stops the game from processing
            return;
        }

        // TODO gather horse-horse and horse-wall collision data
        // TODO apply collision reflections
    }

    public Optional<Horse> getHorse(Player player) {
        for  (Horse horse : horses) {
            if (horse.getPlayer().equals(player)) {
                return Optional.of(horse);
            }
        }
        return Optional.empty();
    }

    public boolean doPlayerMove(Player player, MoveType moveType) { // returns whether the move was actually carried out
        if (lobby.getLobbyState() != PLAYING) return false;
        Optional<Horse> hs = getHorse(player);
        if (hs.isEmpty()) return false;
        Horse horse = hs.get();
        return horse.Move(moveType);
    }
}
