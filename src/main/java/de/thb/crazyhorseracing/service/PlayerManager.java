package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.PlayerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerManager {

    @Autowired
    private PlayerRepository playerRepository;

    private List<Player> players = new ArrayList<>();

    @PostConstruct
    private void init() {
        players = (List<Player>) playerRepository.findAll();
    }

    public Player getPlayer(String jid) {
        return players.stream().filter(player -> player.getJid().equals(jid)).findFirst().orElse(null);
    }

    public boolean playerExists(String jid) {
        return getPlayer(jid) != null;
    }

    private Player createPlayer(String jid, HorseType horseType) {
        Player player = new Player(jid, horseType);
        players.add(player);
        playerRepository.save(player);
        return getPlayer(jid);
    }

    private Player createPlayer(String jid) {
        return createPlayer(jid, null);
    }

    public Player getOrCreatePlayer(String jid, HorseType horseType) {
        Player player = getPlayer(jid);
        if (player == null) player = createPlayer(jid, horseType);
        return player;
    }

    public Player getOrCreatePlayer(String jid) {
        return getOrCreatePlayer(jid, null);
    }
}
