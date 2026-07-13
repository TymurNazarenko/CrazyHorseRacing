package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.HorseTypeProvider;
import de.thb.crazyhorseracing.service.LobbyManager;
import de.thb.crazyhorseracing.service.PlayerManager;
import de.thb.crazyhorseracing.service.response.LoginResponse;
import de.thb.crazyhorseracing.service.response.ActionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
    private final LobbyManager lobbyManager;
    private final PlayerManager playerManager;
    private final HorseTypeProvider horseTypeProvider;

    public MainController(LobbyManager lobbyManager, PlayerManager playerManager, HorseTypeProvider horseTypeProvider) {
        this.lobbyManager = lobbyManager;
        this.playerManager = playerManager;
        this.horseTypeProvider = horseTypeProvider;
    }

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request, HttpServletResponse response) {
        Player player = playerManager.getOrCreatePlayer(request, response);
        model.addAttribute("player", player);
        model.addAttribute("horses", horseTypeProvider.getHorseTypes());

        boolean hasActiveGame = lobbyManager.playerHasLobby(player);
        model.addAttribute("hasActiveGame", hasActiveGame);
        if (hasActiveGame) {
            model.addAttribute("activeGameId", lobbyManager.getLobby(player).id);
        }

        return "home";
    }

    @GetMapping("/game/{id}")
    public String game(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response) {
        Player player = playerManager.getOrCreatePlayer(request, response);
        Lobby lobby = lobbyManager.getLobby(id);

        if (lobby == null) {
            return "redirect:/";
        } else if (!lobby.isPlayerAllowed(player)) {
            return "redirect:/";
        }

        model.addAttribute("player", player);
        model.addAttribute("levelImage", lobby.game.map.imagePath()); // populate the game (image)
        model.addAttribute("horseSizeMultiplier", lobby.game.map.horseSize());
        model.addAttribute("gameId", lobby.id);
        model.addAttribute("UUID", player.getUUID());

        return "game";
    }

    @PostMapping("/start_game")
    public String startGame(@RequestParam("selectedHorseType") long selectedHorseType, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Player player = playerManager.getOrCreatePlayer(request, response);
        HorseType horseType = horseTypeProvider.getHorseById(selectedHorseType);

        ActionResponse actionResponse = playerManager.setPlayerHorseType(player, horseType);
        if (actionResponse.success) {
            redirectAttributes.addFlashAttribute("success", actionResponse.message);
        } else {
            redirectAttributes.addFlashAttribute("error", actionResponse.message);
        }

        Lobby lobby = lobbyManager.getJoinOrCreateLobby(player);
        return "redirect:/game/" + lobby.id;
    }

    @GetMapping("/level_creator")
    public String level_creator() {
        return "level_creator";
    }

    @GetMapping("/horse_creator")
    public String horse_creator() { return "horse_creator"; }

    @GetMapping("/account")
    public String account(Model model, HttpServletRequest request, HttpServletResponse response) {
        Player player = playerManager.getOrCreatePlayer(request, response);
        model.addAttribute("player", player);
        model.addAttribute("nickname", player.getNickname());
        model.addAttribute("login", player.getLogin());
        model.addAttribute("password", player.getPasswordHash() != null ? "password" : "");
        return "account";
    }

    @PostMapping("/set-nickname")
    public String setNickname(@RequestParam("nickname") String nickname, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Player player = playerManager.getOrCreatePlayer(request, response);

        ActionResponse actionResponse = playerManager.setNickname(player, nickname);
        if (actionResponse.success) {
            redirectAttributes.addFlashAttribute("success", actionResponse.message);
        } else {
            redirectAttributes.addFlashAttribute("error", actionResponse.message);
        }

        return "redirect:/account";
    }

    @PostMapping("/set-login-password")
    public String setNickname(@RequestParam("login") String login, @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Player player = playerManager.getOrCreatePlayer(request, response);

        ActionResponse actionResponse = playerManager.setPlayerLoginPassword(player, login, password);
        if (actionResponse.success) {
            redirectAttributes.addFlashAttribute("success", actionResponse.message);
        } else {
            redirectAttributes.addFlashAttribute("error", actionResponse.message);
        }

        return "redirect:/account";
    }

    @PostMapping("/log-in")
    public String login(@RequestParam("login_other") String login, @RequestParam("password_other") String password, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        LoginResponse loginResponse = playerManager.login(login, password);
        if (loginResponse.success) {
            playerManager.setAuthCookie(response, loginResponse.getAuthCookie());
            redirectAttributes.addFlashAttribute("success", loginResponse.message);
        } else {
            redirectAttributes.addFlashAttribute("error", loginResponse.message);
        }
        return "redirect:/account";
    }
}