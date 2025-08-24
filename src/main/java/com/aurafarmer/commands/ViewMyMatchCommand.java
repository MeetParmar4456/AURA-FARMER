
package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

public class ViewMyMatchCommand extends Command {
    public ViewMyMatchCommand(GameService gameService) { super(gameService); }
    @Override public String getName() { return "view_my_match"; }
    @Override public String getDescription() { return "👁️ View status of your current arena match."; }
    @Override public void execute(User user) {
        System.out.println("Viewing your match status...");
        gameService.displayUserArenaMatchStatus(user); // Reuse existing display logic
    }
}
