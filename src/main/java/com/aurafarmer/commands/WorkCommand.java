package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import com.aurafarmer.util.TypingMinigame;

public class WorkCommand extends Command {

    public WorkCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "work";
    }

    @Override
    public String getDescription() {
        return "💪 Work a shift at your current job by completing a challenge.";
    }

    @Override
    public void execute(User user) {
        if (user.getCurrentJob() == null) {
            System.out.println("You need a job to work! Visit the jobs list to get one.");
            return;
        }
        if (gameService.isCoolingDown(user, "work")) {
            return;
        }

        // --- NEW: Run the minigame to get a performance score ---
        double performanceMultiplier = TypingMinigame.run();

        // Pass the multiplier to the game service to calculate final earnings
        gameService.workShift(user, performanceMultiplier);
    }
}
