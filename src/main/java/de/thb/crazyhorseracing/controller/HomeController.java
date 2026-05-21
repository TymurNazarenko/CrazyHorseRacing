package de.thb.crazyhorseracing.controller;

import de.thb.crazyhorseracing.entity.HorseType;
import de.thb.crazyhorseracing.entity.Lobby;
import de.thb.crazyhorseracing.entity.Player;
import de.thb.crazyhorseracing.service.HorseListLoader;
import de.thb.crazyhorseracing.service.LobbyManager;
import de.thb.crazyhorseracing.service.PlayerManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class HomeController {
    private final HorseListLoader horseListLoader;

    private final LobbyManager lobbyManager;
    private final PlayerManager playerManager;

    public HomeController(HorseListLoader horseListLoader, LobbyManager lobbyManager, PlayerManager playerManager) {
        this.horseListLoader = horseListLoader;
        this.lobbyManager = lobbyManager;
        this.playerManager = playerManager;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("horses", horseListLoader.copyHorses());
        return "home";
    }

    @GetMapping("/game/{id}")
    public String game(@PathVariable int id, Model model, HttpSession session) {
        Optional<Lobby> optionalLobby = lobbyManager.getLobby(id);
        if (optionalLobby.isEmpty()) {
            return "redirect:/";
        }

        Lobby lobby = optionalLobby.get();
        Player player = playerManager.getOrCreatePlayer(session.getId());

        if (!lobby.hasPlayer(player)) {
            return "redirect:/";
        }

        model.addAttribute("levelImage", lobby.getGame().getMap().imagePath()); // populate the game (image)
        model.addAttribute("walls", lobby.getGame().getMap().walls()); // populate the game (wall hitboxes)
        model.addAttribute("carrot", lobby.getGame().getMap().carrot()); // populate the game (carrot)
        model.addAttribute("gameId", lobby.getId());

        return "game";
    }

    @PostMapping("/")
    public String startGame(@RequestParam("selectedHorseType") long selectedHorseType, Model model, HttpSession session) {
        Player player = playerManager.getOrCreatePlayer(session.getId());

        Optional<HorseType> horseOptional = horseListLoader.getHorseById(selectedHorseType);
        if (horseOptional.isEmpty()) { // User selected a horse that doesn't exist
            model.addAttribute("error", "Invalid horse selected");
            return home(model);
        }
        HorseType horseType = horseOptional.get();
        player.setHorseType(horseType);

        Lobby lobby = lobbyManager.getJoinOrCreateLobby(player);
        return "redirect:/game/" + lobby.getId();
    }

    @GetMapping("/level_creator")
    public String level_creator(Model model) {
        return "level_creator";
    }
}