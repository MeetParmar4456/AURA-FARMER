package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.List;

public class PlayersCommand extends Command {

    public PlayersCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "players";
    }

    @Override
    public String getDescription() {
        return "📜 View a list of all players in the game.";
    }

    @Override
    public void execute(User user) {
        List<String> playerNames = gameService.getAllPlayerNames();
        System.out.println("\n--- 📜 All Players ---");
        for (String name : playerNames) {
            System.out.println("- " + name);
        }
        System.out.println("----------------------");
    }
}
