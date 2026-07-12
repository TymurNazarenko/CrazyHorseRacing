package de.thb.crazyhorseracing.service;

import com.mysql.cj.log.Log;
import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.PlayerRepository;
import de.thb.crazyhorseracing.service.object.LoginResponse;
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
        return players.stream().filter(player -> login.equals(player.getLogin())).findFirst().orElse(null);
    }

    public boolean isLoginAvailable(Player player, String login) {
        Player playerWithLogin = getPlayerByLogin(login);
        return (playerWithLogin == null) || (playerWithLogin.equals(player));
    }

    // Returns error message or nothing
    public String setNickname(Player player, String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return "Nickname can't be empty";
        } else if (nickname.length() > 100) {
            return "Nickname too long";
        }

        player.setNickname(nickname);
        return "";
    }

    // Return error message or nothing
    public String setPlayerLoginPassword(Player player, String login, String password) {
        if (login.isEmpty() || password.isEmpty()) return "Login and password can't be empty";
        if (!isLoginAvailable(player, login)) return "Login is already taken";

        String passwordHash = passwordHasher.encode(password);
        player.setPasswordHash(passwordHash);
        player.setLogin(login);
        return "";
    }

    // Return error message or nothing
    public String setPlayerHorseType(Player player, HorseType horseType) {
        if (horseType == null) {
            return "Invalid horse selected";
        }

        player.setHorseType(horseType);
        return "";
    }

    public boolean doesPasswordMatch(Player player, String password) {
        String storedHash = player.getPasswordHash();
        return passwordHasher.matches(password, storedHash);
    }

    public LoginResponse login(String login, String password) {
        if (login == null || login.isEmpty()) {
            return new LoginResponse(false, null, "Login can't be empty", null);
        } else if (password == null || password.isEmpty()) {
            return new LoginResponse(false, null, "Password can't be empty", null);
        }

        Player player = getPlayerByLogin(login);
        if  (player == null) {
            return new LoginResponse(false, null, "Player not found", null);
        }

        if (!doesPasswordMatch(player, password)) {
            return new LoginResponse(false, null, "Wrong password", null);
        }

        return new LoginResponse(true, player.getJid(), null, "Successfully logged in");
    }
}
