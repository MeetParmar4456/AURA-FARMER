// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/BrowseChallengesCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.ArenaMatch;
import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

import java.util.List;
import java.util.Scanner;

public class BrowseChallengesCommand extends Command {
    public BrowseChallengesCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "browse_challenges";
    }

    @Override
    public String getDescription() {
        return "🔍 Browse challenges created by other players.";
    }

    @Override
    public void execute(User user) {
        // This command now calls the logic in GameService
        gameService.browsePendingChallenges(user);
    }
}