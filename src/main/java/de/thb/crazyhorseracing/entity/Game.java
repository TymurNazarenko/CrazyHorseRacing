package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static de.thb.crazyhorseracing.entity.LobbyState.PLAYING;

public class Game {
    private Lobby lobby; // needed to communicate game state back to the lobby
    private List<Horse> horses;
    @Getter
    private GameMap map;
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

    public List<Vec> getAvailableSpawnpoints() {
        List<Vec> allSpawnpoints = map.getSpawnpoints();
        List<Vec> availableSpawnpoints = new ArrayList<>();
        for (Vec spawnpoint : allSpawnpoints) {
            if (isSpawnpointAvailable(spawnpoint)) {
                availableSpawnpoints.add(spawnpoint);
            }
        }
        return availableSpawnpoints;
    }

    public boolean addHorse(Player player) { // Returns whether horse was actually added
        if (horses.size() >= map.getMaxPlayers()) return false;

        List<Vec> spawnpoints = getAvailableSpawnpoints();
        if  (spawnpoints.isEmpty()) return false;

        Vec chosenSpawnpoint = spawnpoints.get(0); // TODO choose randomly
        HorseType horseType = player.getHorseType().get();
        horses.add(new Horse(horseType, player, chosenSpawnpoint));

        return true;
    }

    public void processStep(double dt_seconds) {
        // TODO clamp dt_nano to reasonable values
        // TODO apply velocities to horses
        // TODO gather collision data
        // TODO apply collision reflections
        // TODO check if any horse intersect the carrots
        // To stop game: gameTaskHandler.cancel(true);
    }

    public Optional<Horse> getHorse(Player player) {
        for  (Horse horse : horses) {
            if (horse.getPlayer().equals(player)) {
                return Optional.of(horse);
            }
        }
        return Optional.empty();
    }

    public List<Horse> getHorses() {
        return horses;
    }

    public boolean doPlayerMove(Player player, MoveType moveType) { // returns whether the move was actually carried out
        if (lobby.getLobbyState() != PLAYING) return false;
        Optional<Horse> hs = getHorse(player);
        if (hs.isEmpty()) return false;
        Horse horse = hs.get();
        return horse.Move(moveType);
    }
}
