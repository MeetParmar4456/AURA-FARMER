// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/StartArenaTurnCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

public class StartArenaTurnCommand extends Command {
    public StartArenaTurnCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "start_turn";
    }

    @Override
    public String getDescription() {
        return "▶️ Start your turn in an active challenge.";
    }

    @Override
    public void execute(User user) {
        // This command now calls the logic in GameService
        gameService.startArenaTurn(user);
    }
}