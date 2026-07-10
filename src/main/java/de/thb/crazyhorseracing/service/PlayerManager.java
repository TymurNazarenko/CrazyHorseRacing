package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.PlayerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerManager {
    private final PlayerRepository playerRepository;
    private List<Player> players;
    private final PasswordEncoder passwordHasher;

    public PlayerManager(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.players = new ArrayList<>();
        passwordHasher = new BCryptPasswordEncoder();
    }

    @PostConstruct
    private void init() {
        players = (List<Player>) playerRepository.findAll();
    }

    public Player getPlayerByJID(String jid) {
        return players.stream().filter(player -> player.getJid().equals(jid)).findFirst().orElse(null);
    }

    private Player createPlayer(String jid, HorseType horseType) {
        Player player = new Player(jid, horseType);
        players.add(player);
        playerRepository.save(player);
        return getPlayerByJID(jid);
    }

    private Player createPlayer(String jid) {
        return createPlayer(jid, null);
    }

    public Player getOrCreatePlayer(String jid, HorseType horseType) {
        Player player = getPlayerByJID(jid);
        if (player == null) player = createPlayer(jid, horseType);
        return player;
    }

    public Player getOrCreatePlayer(String jid) {
        return getOrCreatePlayer(jid, null);
    }

    public Player getPlayerByLogin(String login) {
        return players.stream().filter(player -> player.getLogin().equals(login)).findFirst().orElse(null);
    }

    public boolean isLoginAvailable(String login) {
        return getPlayerByLogin(login) == null;
    }

    // Return whether successful
    public boolean setPlayerLoginPassword(Player player, String login, String password) {
        if (!isLoginAvailable(login)) return false;
        String passwordHash =  passwordHasher.encode(password);
        player.setPasswordHash(passwordHash);
        player.setLogin(login);
        return true;
    }

    public boolean doesPasswordMatch(Player player, String password) {
        String passwordHash =  passwordHasher.encode(password);
        return passwordHash.equals(player.getPasswordHash());
    }
}
