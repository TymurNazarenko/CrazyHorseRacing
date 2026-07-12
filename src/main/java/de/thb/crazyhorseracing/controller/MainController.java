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

    @PostMapping("/")
    public String startGame(@RequestParam("selectedHorseType") long selectedHorseType, Model model, HttpSession session) {
        Player player = playerManager.getOrCreatePlayer(session.getId());

        HorseType horseType = horseTypeProvider.getHorseById(selectedHorseType);
        if (horseType == null) { // User selected a horse that doesn't exist
            model.addAttribute("error", "Invalid horse selected");
            return home(model, session);
        }
        player.setHorseType(horseType);

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
        // TODO
        return "account";
    }
}