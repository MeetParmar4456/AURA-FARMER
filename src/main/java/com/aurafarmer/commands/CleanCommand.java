// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/CleanCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.Random;

public class CleanCommand extends Command {

    public CleanCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public String getDescription() {
        return "🧹 Clean up an area for a small reward.";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        Random random = new Random();

        // Simulate action time
        try {
            System.out.println("🧹 Cleaning diligently...");
            Thread.sleep( (random.nextInt(3) + 2) * 1000); // Wait 2 to 4 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Action was interrupted.");
            return;
        }

        int totalAuras = random.nextInt(35001); // Aura range: 0 to 35,000
        String message = "✅ You earned " + totalAuras + " Auras for your hard work!";

        gameService.updateUserBalance(user, totalAuras);
        System.out.println(message);

        // --- MODIFICATION START ---
        if(!user.isInArenaTurn()) {
            System.out.println("Your new balance is " + user.getAuraBalance() + ".");
            gameService.setCooldown(user, getName(), 45);
        }
        // --- MODIFICATION END ---
    }
}