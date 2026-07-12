package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.PlayerRepository;
import de.thb.crazyhorseracing.service.object.LoginResponse;
import de.thb.crazyhorseracing.service.object.Response;
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

    public Response setNickname(Player player, String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return new Response(false, "Nickname can't be empty");
        } else if (nickname.length() > 30) {
            return new Response(false, "Nickname too long");
        }

        player.setNickname(nickname);
        return new Response(true, "Nickname has been set");
    }

    public Response setPlayerLoginPassword(Player player, String login, String password) {
        if (login.isEmpty() || password.isEmpty()) return new Response(false,"Login and password can't be empty");
        if (!isLoginAvailable(player, login)) return new Response(false, "Login is already taken");

        String passwordHash = passwordHasher.encode(password);
        player.setPasswordHash(passwordHash);
        player.setLogin(login);
        return new Response(true, "Login and password successfully set");
    }

    public Response setPlayerHorseType(Player player, HorseType horseType) {
        if (horseType == null) {
            return new Response(false, "Invalid horse selected");
        }

        player.setHorseType(horseType);
        return new Response(true, "Horse type selected");
    }

    public boolean doesPasswordMatch(Player player, String password) {
        String storedHash = player.getPasswordHash();
        return passwordHasher.matches(password, storedHash);
    }

    public LoginResponse login(String login, String password) {
        if (login == null || login.isEmpty()) {
            return new LoginResponse(false, "Login can't be empty");
        } else if (password == null || password.isEmpty()) {
            return new LoginResponse(false, "Password can't be empty");
        }

        Player player = getPlayerByLogin(login);
        if  (player == null) {
            return new LoginResponse(false, "Player not found");
        }

        if (!doesPasswordMatch(player, password)) {
            return new LoginResponse(false, "Wrong password");
        }

        return new LoginResponse(true, "Successfully logged in", player.getJid());
    }
}
