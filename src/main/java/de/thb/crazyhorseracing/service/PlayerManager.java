package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Player;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PlayerManager {
    private List<Player> players;

    @PostConstruct
    public void init() {
        players = new ArrayList<>();
    }

    public Optional<Player> getPlayer(String id) {
        for (Player player : players) {
            if (player.secretId().equals(id)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    public boolean playerExists(String id) {
        return getPlayer(id).isPresent();
    }

    private Player createPlayer(String id, HorseType horseType) {
        Player player = new Player(id, horseType);
        players.add(player);
        return player;
    }

    private Player createPlayer(String id) {
        Player player = new Player(id);
        players.add(player);
        return player;
    }

    public Player getOrCreatePlayer(String id, HorseType horseType) {
        return getPlayer(id).orElseGet(() -> createPlayer(id, horseType));
    }

    public Player getOrCreatePlayer(String id) {
        return getPlayer(id).orElseGet(() -> createPlayer(id));
    }
}
