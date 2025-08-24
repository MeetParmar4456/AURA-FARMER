// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/DisbandChallengeCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

public class DisbandChallengeCommand extends Command {
    public DisbandChallengeCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "disband_challenge";
    }

    @Override
    public String getDescription() {
        return "🗑️ Disband an inactive or completed challenge.";
    }

    @Override
    public void execute(User user) {
        // This command calls the disband logic in GameService
        gameService.disbandArenaChallenge(user);
    }
}