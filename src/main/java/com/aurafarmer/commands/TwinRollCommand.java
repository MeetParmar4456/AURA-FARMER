// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/TwinRollCommand.java
package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import com.aurafarmer.util.GambleUtils;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;

public class TwinRollCommand extends Command {

    public TwinRollCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "twinroll";
    }

    @Override
    public String getDescription() {
        return "🎲 Roll two dice. Get twins for a 1.5x prize, or snake eyes for 3x!";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int bet = GambleUtils.getBetAmount(scanner, user);
        if (bet == -1) return;

        // --- MODIFICATION START ---
        try {
            System.out.print("Rolling the dice...");
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

        Random random = new Random();
        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;

        String[] diceEmojis = {"1", "2", "3", "4", "5", "6"};
        System.out.println("The dice are rolling... You got: " + diceEmojis[dice1 - 1] + " and " + diceEmojis[dice2 - 1]);

        if (dice1 == 1 && dice2 == 1) {
            int winnings = bet * 2;
            gameService.updateUserBalance(user, winnings);
            System.out.println("🐍 SNAKE EYES! You win a 2x prize of " + winnings + " Auras!");
        } else if (dice1 == dice2) {
            int winnings = (int) (bet * 0.5);
            gameService.updateUserBalance(user, winnings);
            System.out.println("✨ TWINS! You win a 1.5x prize of " + winnings + " Auras!");
        } else {
            gameService.updateUserBalance(user, -bet);
            System.out.println("😭 No twins... You lost " + bet + " Auras.");
        }
        System.out.println("Your new balance is " + user.getAuraBalance() + ".");

        gameService.setCooldown(user, getName(), 25); // 25-second cooldown
    }
}