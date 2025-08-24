// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/DigCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.model.UserInventoryItem;
import com.aurafarmer.service.GameService;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import com.aurafarmer.model.Item;

public class DigCommand extends Command {

    public DigCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "dig";
    }

    @Override
    public String getDescription() {
        return "⛏️ Dig in the dirt for a chance to find some Auras.";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        // Fetch shovel item definition to get its current price and max quantity
        Item shovelDef = gameService.getShopItems().stream()
                .filter(item -> "Shovel".equalsIgnoreCase(item.getName()))
                .findFirst().orElse(null);

        if (shovelDef == null) {
            System.out.println("Error: Shovel item definition not found in shop. Cannot proceed with dig.");
            return;
        }

        // Check for Shovel in inventory and its uses
        UserInventoryItem shovelItem = user.getInventory().values().stream()
                .filter(item -> "Shovel".equalsIgnoreCase(item.getItem().getName()))
                .findFirst()
                .orElse(null);

        // This block handles the case where the user has no shovel or the shovel is broken
        if (shovelItem == null || shovelItem.getQuantity() <= 0 || (shovelItem.getItem().getUsesPerItem() != null && (shovelItem.getUsesLeft() == null || shovelItem.getUsesLeft() <= 0))) {
            System.out.println("You can't dig without a shovel or your shovel has no uses left!");

            if (shovelDef.getMaxQuantity() != null && (shovelItem != null && shovelItem.getQuantity() >= shovelDef.getMaxQuantity())) {
                System.out.println("❌ You already have the maximum number of shovels (" + shovelDef.getMaxQuantity() + ").");
                return;
            }

            // Dynamically get the shovel's buy price from the fetched item definition
            System.out.print("A shovel costs " + shovelDef.getBuyPrice() + " Auras. Do you want to buy one? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("y")) {
                if (user.getAuraBalance() >= shovelDef.getBuyPrice()) {
                    gameService.updateUserBalance(user, -shovelDef.getBuyPrice());
                    gameService.addItemToUser(user, shovelDef.getId(), 1);
                    System.out.println("✅ You bought a shovel and can now dig!");
                    // After buying, we must re-fetch the shovel item to get its updated state with uses.
                    shovelItem = user.getInventory().values().stream()
                            .filter(item -> "Shovel".equalsIgnoreCase(item.getItem().getName()))
                            .findFirst()
                            .orElse(null); // This will now find the new, usable shovel
                } else {
                    System.out.println("❌ You don't have enough Auras to buy a shovel.");
                    return; // Exit if they can't afford it
                }
            } else {
                System.out.println("You decided not to buy a shovel. Dig command cancelled.");
                return; // Exit if they don't want to buy
            }
        }

        // --- Digging Logic ---
        // This part of the code will now be executed if the user already had a shovel OR just bought one.

        Random random = new Random();

        // Simulate action time
        try {
            int waitTime = random.nextInt(5) + 5; // Wait 5 to 9 seconds
            System.out.print("⛏️ Digging...");
            for (int i = 0; i < waitTime; i++) {
                Thread.sleep(1000);
                System.out.print(".");
            }
            System.out.println(); // New line after dots
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Action was interrupted.");
            return;
        }

        int outcome = random.nextInt(100);

        if (outcome < 60) {
            int foundAuras = random.nextInt(20001); // Aura range: 0 to 20,000
            gameService.updateUserBalance(user, foundAuras);
            // --- MODIFICATION START ---
            if(user.isInArenaTurn()){
                System.out.println("✅ You dig in the dirt and find " + foundAuras + " Auras!");
            } else {
                System.out.println("✅ You dig in the dirt and find " + foundAuras + " Auras! Your new balance is " + user.getAuraBalance() + ".");
            }
            // --- MODIFICATION END ---
        } else {
            System.out.println("🤷 You dig for a while but find nothing but dirt.");
        }

        // Decrement shovel uses after successful action
        gameService.useTool(user, "Shovel");
        // Only set cooldown if NOT in arena turn
        if (!user.isInArenaTurn()) {
            gameService.setCooldown(user, getName(), 30);
        }
    }

}