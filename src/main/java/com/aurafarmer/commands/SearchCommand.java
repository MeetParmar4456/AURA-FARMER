// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/SearchCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SearchCommand extends Command {

    public SearchCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "🔍 Search one of three locations to find a hidden stash of Auras.";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        // Define all possible locations
        List<String> locations = Arrays.asList("a dusty attic", "an old barn", "a forgotten shed");

        // Randomly select the winning location for this turn
        String winningLocation = locations.get(new Random().nextInt(locations.size()));

        // Shuffle the list for display so the order is always random
        Collections.shuffle(locations);

        System.out.println("\nYou've found a few interesting places to search:");
        System.out.println("1. " + locations.get(0));
        System.out.println("2. " + locations.get(1));
        System.out.println("3. " + locations.get(2));
        System.out.print("Which location do you want to search? (1-3): ");

        Scanner scanner = new Scanner(System.in);
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > 3) {
                System.out.println("❓ Invalid choice. You got distracted and went home.");
                return;
            }

            String chosenLocation = locations.get(choice - 1);

            // Simulate action time
            try {
                System.out.println("🔍 Searching " + chosenLocation + "...");
                Thread.sleep( (new Random().nextInt(3) + 3) * 1000); // Wait 3 to 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Action was interrupted.");
                return;
            }

            // Check if the choice was correct
            if (chosenLocation.equals(winningLocation)) {
                Random random = new Random();
                int foundAuras = random.nextInt(25001); // Aura range: 0 to 25,000
                gameService.updateUserBalance(user, foundAuras);

                int itemOutcome = random.nextInt(100); // 0-99 for item chance

                if (itemOutcome < 40) { // 40% chance for Shiny Rock
                    gameService.addItemToUser(user, 4, 1); // Add 1 Shiny Rock (assuming ID 4)
                    System.out.println("✅ Jackpot! You found a hidden stash of " + foundAuras + " Auras and a Shiny Rock!");
                } else if (itemOutcome < 60) { // 20% chance for Rusty Key
                    gameService.addItemToUser(user, 1, 1); // Add 1 Rusty Key (assuming ID 1)
                    System.out.println("✅ Jackpot! You found a hidden stash of " + foundAuras + " Auras and a Rusty Key!");
                } else { // 40% chance for only Auras
                    System.out.println("✅ Jackpot! You found a hidden stash of " + foundAuras + " Auras!");
                }

            } else {
                System.out.println("🤷 You searched thoroughly but found nothing of value.");
            }

            // --- MODIFICATION START ---
            if(!user.isInArenaTurn()) {
                System.out.println("Your new balance is " + user.getAuraBalance() + ".");
                gameService.setCooldown(user, getName(), 45); // Set cooldown after the action
            }
            // --- MODIFICATION END ---

        } catch (NumberFormatException e) {
            System.out.println("❓ That's not a number. You got distracted and went home.");
        }
    }
}