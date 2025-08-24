// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/PassiveCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

public class PassiveCommand extends Command {

    public PassiveCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "passive";
    }

    @Override
    public String getDescription() {
        return "🛡️ Toggle passive mode on or off. Prevents you from being robbed.";
    }

    @Override
    public void execute(User user) {
        if (user.isPassiveMode()) {
            gameService.setPassiveMode(user, false);
            System.out.println("✅ Passive mode is now OFF. You can be robbed, but you can also rob others.");
        } else {
            gameService.setPassiveMode(user, true);
            System.out.println("✅ Passive mode is now ON. You cannot be robbed, but you also cannot rob others.");
        }
    }
}