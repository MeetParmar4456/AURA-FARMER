// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/BegCommand.java
package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.Random;

public class BegCommand extends Command {

    public BegCommand(GameService gameService) {
        super(gameService);
    }


    public String getName() {
        return "beg";
    }

   public String getDescription() {
        return "🙏 Beg from strangers for some spare Auras.";
    }

    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        Random random = new Random();

        // Simulate action time
        try {
            System.out.println("🙏 Approaching a stranger...");
            Thread.sleep( (random.nextInt(2) + 2) * 1000); // Wait 1 to 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Action was interrupted.");
            return;
        }

        int outcome = random.nextInt(100); // 0-99

        if (outcome < 60) { // 60% chance to get some Auras
            int receivedAuras = random.nextInt(20001); // Aura range: 0 to 20,000
            gameService.updateUserBalance(user, receivedAuras);
            System.out.println("✅ A kind stranger gave you " + receivedAuras + " Auras!");
        } else if (outcome < 90) { // 30% chance to get nothing
            System.out.println("🤷 The stranger ignored you.");
        } else { // 10% chance to lose Auras (e.g., fine for loitering)
            int lostAuras = random.nextInt(11) + 5; // Original fine range: 5-15 Auras
            gameService.updateUserBalance(user, -lostAuras);
            System.out.println("👮 You were shooed away and fined " + lostAuras + " Auras for loitering.");
        }

        // --- MODIFICATION START ---
        if(!user.isInArenaTurn()) {
            System.out.println("Your new balance is " + user.getAuraBalance() + ".");
            gameService.setCooldown(user, getName(), 35);
        }
        // --- MODIFICATION END ---
    }
}