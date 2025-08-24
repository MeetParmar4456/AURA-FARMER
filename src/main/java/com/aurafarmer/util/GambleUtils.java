package com.aurafarmer.util;

import com.aurafarmer.model.User;
import java.util.Scanner;

// A utility class to handle common gambling logic.
public class GambleUtils {

    /**
     * Prompts the user for a bet amount and validates it, including an "all-in" feature.
     * @param scanner The Scanner object to read user input.
     * @param user The user who is betting.
     * @return The validated bet amount, or -1 if the bet is invalid or cancelled.
     */
    public static int getBetAmount(Scanner scanner, User user) {
        // --- FIX STARTS HERE ---
        // Add a check to prevent betting if the user has no Auras.
        if (user.getAuraBalance() <= 0) {
            System.out.println("❌ You have no Auras to bet!");
            return -1;
        }
        // --- FIX ENDS HERE ---

        System.out.print("How many Auras do you want to bet? (Your balance: " + user.getAuraBalance() + "): ");
        try {
            int bet = Integer.parseInt(scanner.nextLine());

            if (bet <= 0) {
                System.out.println("❌ You must bet a positive amount.");
                return -1;
            }

            if (bet > user.getAuraBalance()) {
                System.out.println("You don't have that many Auras!");
                System.out.print("Do you want to bet everything you have (" + user.getAuraBalance() + " Auras)? (y/n): ");
                String choice = scanner.nextLine().toLowerCase();
                if (choice.equals("y")) {
                    return user.getAuraBalance(); // Bet all-in
                } else {
                    System.out.println("Bet cancelled.");
                    return -1; // Cancel the bet
                }
            }
            return bet;
        } catch (NumberFormatException e) {
            System.out.println("❓ That's not a valid number.");
            return -1;
        }
    }
}