// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/CoinFlipCommand.java
package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import com.aurafarmer.util.GambleUtils;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;

public class CoinFlipCommand extends Command {

    public CoinFlipCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "coinflip";
    }

    @Override
    public String getDescription() {
        return "🪙 Flip a coin. Double your bet or lose it all.";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int bet = GambleUtils.getBetAmount(scanner, user);
        if (bet == -1) return;

        System.out.print("Choose heads or tails (h/t): ");
        String choice = scanner.nextLine().toLowerCase();

        if (!choice.equals("h") && !choice.equals("t")) {
            System.out.println("❌ Invalid choice. Please enter 'h' or 't'.");
            return;
        }

        // --- MODIFICATION START ---
        try {
            System.out.print("Flipping...");
            for (int i = 0; i < 4; i++) {
                if (System.in.available() > 0) { // Check for input
                    System.out.println("\nAction cancelled.");
                    return;
                }
                Thread.sleep(1000);
                System.out.print(".");
            }
            System.out.println();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            System.out.println("\nAction interrupted. Please enter a command.");
            return;
        }
        // --- MODIFICATION END ---

        String result = new Random().nextBoolean() ? "h" : "t";
        String resultText = result.equals("h") ? "Heads" : "Tails";

        System.out.println("The coin spins... and it's **" + resultText + "**!");

        if (choice.equals(result)) {
            int winnings = bet;
            gameService.updateUserBalance(user, winnings);
            System.out.println("🎉 You won " + winnings + " Auras! Your new balance is " + user.getAuraBalance() + ".");
        } else {
            gameService.updateUserBalance(user, -bet);
            System.out.println("😭 You lost " + bet + " Auras. Your new balance is " + user.getAuraBalance() + ".");
        }

        gameService.setCooldown(user, getName(), 20); // 20-second cooldown
    }
}