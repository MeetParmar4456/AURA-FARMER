// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/SlotsCommand.java
package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import com.aurafarmer.util.GambleUtils;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;

public class SlotsCommand extends Command {

    public SlotsCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "slots";
    }

    @Override
    public String getDescription() {
        return "🎰 Spin the slot machine for a chance to win 3x or 9x prize.";
    }

    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        int bet = GambleUtils.getBetAmount(scanner, user);
        if (bet == -1) return;

        // --- MODIFICATION START ---
        try {
            System.out.print("Spinning the slots...");
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

        String[] emojis = {"🍒", "🍋", "🍊", "🍇", "🍉", "💎"};
        Random random = new Random();

        String s1 = emojis[random.nextInt(emojis.length)];
        String s2 = emojis[random.nextInt(emojis.length)];
        String s3 = emojis[random.nextInt(emojis.length)];

        System.out.println("\n[ " + s1 + " | " + s2 + " | " + s3 + " ]");

        if (s1.equals(s2) && s2.equals(s3)) {
            int winnings;
            if (s1.equals("💎")) {
                winnings = bet * 10;
                System.out.println("💎💎💎 JACKPOT! 💎💎💎");
            } else {
                winnings = bet * 4;
                System.out.println("🎉 WINNER! 🎉");
            }
            gameService.updateUserBalance(user, winnings - bet);
            System.out.println("You won " + winnings + " Auras!");
        } else {
            gameService.updateUserBalance(user, -bet);
            System.out.println("😭 You lost " + bet + " Auras. Better luck next time!");
        }
        System.out.println("Your new balance is " + user.getAuraBalance() + ".");
    }
}