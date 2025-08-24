// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/QuitArenaTurnCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

public class QuitArenaTurnCommand extends Command {

    public QuitArenaTurnCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "quit_turn";
    }

    @Override
    public String getDescription() {
        return "🛑 End your current arena turn early.";
    }

    @Override
    public void execute(User user) {
        if (user.isInArenaTurn()) {
            gameService.endArenaTurn(user, false); // False for not forced end
        } else {
            System.out.println("❌ You are not currently in an arena turn to quit.");
        }
    }
}