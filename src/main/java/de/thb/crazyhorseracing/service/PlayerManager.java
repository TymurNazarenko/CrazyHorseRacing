package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.PlayerRepository;
import de.thb.crazyhorseracing.service.response.LoginResponse;
import de.thb.crazyhorseracing.service.response.ActionResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class PlayerManager {
    private final PlayerRepository playerRepository;
    private List<Player> players;
    private final PasswordEncoder passwordHasher;

    public PlayerManager(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.players = new ArrayList<>();
        this.passwordHasher = new BCryptPasswordEncoder();
    }

    @PostConstruct
    private void init() {
        players = (List<Player>) playerRepository.findAll();
    }

    public String getAuthCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        Cookie AuthCookie = Arrays.stream(cookies).filter(cookie -> "AuthCookie".equals(cookie.getName())).findFirst().orElse(null);
        if (AuthCookie == null) return null;
        return AuthCookie.getValue();
    }

    public void setAuthCookie(HttpServletResponse response, String AuthCookie) {
        Cookie cookie = new Cookie("AuthCookie", AuthCookie);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
    }

    public String getOrCreateAuthCookie(HttpServletRequest request, HttpServletResponse response) {
        String AuthCookie = getAuthCookie(request);
        if (AuthCookie != null) { return AuthCookie; }

        AuthCookie = UUID.randomUUID().toString();
        setAuthCookie(response, AuthCookie);
        return AuthCookie;
    }

    private Player createPlayer(String AuthCookie, HorseType horseType) {
        Player player = new Player(AuthCookie, horseType);
        players.add(player);
        playerRepository.save(player);
        return getPlayer(AuthCookie);
    }

    private Player createPlayer(String AuthCookie) {
        return createPlayer(AuthCookie, null);
    }

    public Player getPlayer(String AuthCookie) {
        return players.stream().filter(player -> player.getAuthCookie().equals(AuthCookie)).findFirst().orElse(null);
    }

    public Player getPlayer(HttpServletRequest request) {
        return getPlayer(getAuthCookie(request));
    }

    public Player getOrCreatePlayer(HttpServletRequest request, HttpServletResponse response) {
        String AuthCookie = getOrCreateAuthCookie(request, response);
        Player player =  getPlayer(AuthCookie);
        if (player == null) player = createPlayer(AuthCookie);
        return player;
    }

    public Player getPlayerByLogin(String login) {
        return players.stream().filter(player -> login.equals(player.getLogin())).findFirst().orElse(null);
    }

    public boolean isLoginAvailable(Player player, String login) {
        Player playerWithLogin = getPlayerByLogin(login);
        return (playerWithLogin == null) || (playerWithLogin.equals(player));
    }

    public ActionResponse setNickname(Player player, String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return new ActionResponse(false, "Nickname can't be empty");
        } else if (nickname.length() > 30) {
            return new ActionResponse(false, "Nickname too long");
        }

        player.setNickname(nickname);
        playerRepository.save(player);
        return new ActionResponse(true, "Nickname has been set");
    }

    public ActionResponse setPlayerLoginPassword(Player player, String login, String password) {
        if (login.isEmpty() || password.isEmpty()) return new ActionResponse(false,"Login and password can't be empty");
        if (!isLoginAvailable(player, login)) return new ActionResponse(false, "Login is already taken");

        String passwordHash = passwordHasher.encode(password);
        player.setPasswordHash(passwordHash);
        player.setLogin(login);
        playerRepository.save(player);
        return new ActionResponse(true, "Login and password successfully set");
    }

    public ActionResponse setPlayerHorseType(Player player, HorseType horseType) {
        if (horseType == null) {
            return new ActionResponse(false, "Invalid horse selected");
        }

        player.setHorseType(horseType);
        return new ActionResponse(true, "Horse type selected");
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

        return new LoginResponse(true, "Successfully logged in", player.getAuthCookie());
    }
}
