package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.repository.HorseTypeProvider;
import de.thb.crazyhorseracing.service.LobbyManager;
import de.thb.crazyhorseracing.service.PlayerManager;
import jakarta.servlet.http.HttpSession;
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
    public String home(Model model, HttpSession session) {
        Player player = playerManager.getOrCreatePlayer(session.getId());
        model.addAttribute("player", player);
        model.addAttribute("horses", horseTypeProvider.getHorseTypes());
        return "home";
    }

    @GetMapping("/game/{id}")
    public String game(@PathVariable int id, Model model, HttpSession session) {
        Player player = playerManager.getOrCreatePlayer(session.getId());
        Lobby lobby = lobbyManager.getLobby(id);

        if (lobby == null) {
            return "redirect:/";
        } else if (!lobby.isPlayerAllowed(player)) {
            return "redirect:/";
        }

        model.addAttribute("player", player);
        model.addAttribute("levelImage", lobby.getGame().map.imagePath()); // populate the game (image)
        model.addAttribute("horseSizeMultiplier", lobby.getGame().map.horseSize());
        model.addAttribute("gameId", lobby.getId());
        model.addAttribute("UUID", player.getUUID());

        return "game";
    }

    @PostMapping("/start_game")
    public String startGame(@RequestParam("selectedHorseType") long selectedHorseType, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Player player = playerManager.getOrCreatePlayer(session.getId());

        HorseType horseType = horseTypeProvider.getHorseById(selectedHorseType);
        String error = playerManager.setPlayerHorseType(player, horseType);
        if (error != null && !error.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/";
        }

        Lobby lobby = lobbyManager.getJoinOrCreateLobby(player);
        return "redirect:/game/" + lobby.getId();
    }

    @GetMapping("/level_creator")
    public String level_creator(Model model) {
        return "level_creator";
    }

    @GetMapping("/account")
    public String account(Model model, HttpSession session) {
        Player player = playerManager.getOrCreatePlayer(session.getId());
        model.addAttribute("player", player);
        model.addAttribute("nickname", player.getNickname());
        model.addAttribute("login", player.getLogin());
        model.addAttribute("password", player.getPasswordHash() != null ? "password" : "");
        return "account";
    }

    @PostMapping("/set-nickname")
    public String setNickname(@RequestParam("nickname") String nickname, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Player player = playerManager.getOrCreatePlayer(session.getId());
        String error = playerManager.setNickname(player, nickname);
        if (error != null && !error.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Nickname set successfully");
        }
        return "redirect:/account";
    }

    @PostMapping("/set-login-password")
    public String setNickname(@RequestParam("login") String login, @RequestParam("password") String password, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Player player = playerManager.getOrCreatePlayer(session.getId());
        String error = playerManager.setPlayerLoginPassword(player, login, password);
        if (error != null && !error.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Login and password set successfully");
        }
        return "redirect:/account";
    }
}