// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/FishingCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.model.UserInventoryItem;
import com.aurafarmer.service.GameService;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import com.aurafarmer.model.Item;

public class FishingCommand extends Command {

    public FishingCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "fish";
    }

    @Override
    public String getDescription() {
        return "🎣 Go fishing for Auras and rare catches.";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        // Fetch fishing rod item definition to get its current price and max quantity
        Item fishingRodDef = gameService.getShopItems().stream()
                .filter(item -> "Fishing Rod".equalsIgnoreCase(item.getName()))
                .findFirst().orElse(null);

        if (fishingRodDef == null) {
            System.out.println("Error: Fishing Rod item definition not found in shop. Cannot proceed with fishing.");
            return;
        }

        // Check for Fishing Rod in inventory and its uses
        UserInventoryItem fishingRodItem = user.getInventory().values().stream()
                .filter(item -> "Fishing Rod".equalsIgnoreCase(item.getItem().getName()))
                .findFirst()
                .orElse(null);

        if (fishingRodItem == null || fishingRodItem.getQuantity() <= 0 || (fishingRodItem.getItem().getUsesPerItem() != null && (fishingRodItem.getUsesLeft() == null || fishingRodItem.getUsesLeft() <= 0))) {
            System.out.println("You can't go fishing without a fishing rod or your fishing rod has no uses left!");

            if (fishingRodDef.getMaxQuantity() != null && (fishingRodItem != null && fishingRodItem.getQuantity() >= fishingRodDef.getMaxQuantity())) {
                System.out.println("❌ You already have the maximum number of fishing rods (" + fishingRodDef.getMaxQuantity() + ").");
                return;
            }

            // Dynamically get the fishing rod's buy price from the fetched item definition
            System.out.print("A Fishing Rod costs " + fishingRodDef.getBuyPrice() + " Auras. Do you want to buy one? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("y")) {
                if (user.getAuraBalance() >= fishingRodDef.getBuyPrice()) {
                    gameService.updateUserBalance(user, -fishingRodDef.getBuyPrice());
                    gameService.addItemToUser(user, fishingRodDef.getId(), 1);
                    System.out.println("✅ You bought a fishing rod and can now go fishing!");
                } else {
                    System.out.println("❌ You don't have enough Auras to buy a fishing rod.");
                }
            } else {
                System.out.println("You decided not to buy a fishing rod. Fishing command cancelled.");
            }
            return; // Exit after the check and potential purchase
        }

        Random random = new Random();
        int foundAuras = random.nextInt(30001); // Aura range: 0 to 30,000

        // Simulate action time
        try {
            System.out.println("🎣 Casting your line...");
            Thread.sleep( (random.nextInt(4) + 3) * 1000); // Wait 3 to 6 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Action was interrupted.");
            return;
        }

        int outcome = random.nextInt(100); // 0-99

        if (outcome < 50) { // 50% chance to catch Common Fish
            gameService.updateUserBalance(user, foundAuras);
            gameService.addItemToUser(user, 9, 1); // Add 1 Common Fish (assuming ID 9)
            System.out.println("✅ You caught a Common Fish and found " + foundAuras + " Auras!");
        } else if (outcome < 80) { // 30% chance to catch Rare Fish
            gameService.updateUserBalance(user, foundAuras);
            gameService.addItemToUser(user, 10, 1); // Add 1 Rare Fish (assuming ID 10)
            System.out.println("🎉 You caught a Rare Fish and found " + foundAuras + " Auras!");
        } else if (outcome < 95) { // 15% chance to catch Legendary Fish
            gameService.updateUserBalance(user, foundAuras);
            gameService.addItemToUser(user, 11, 1); // Add 1 Legendary Fish (assuming ID 11)
            System.out.println("👑 You caught a LEGENDARY FISH and found " + foundAuras + " Auras!");
        } else { // 5% chance to catch nothing
            System.out.println("🤷 Your line came up empty. Better luck next time!");
        }

        // --- MODIFICATION START ---
        if(!user.isInArenaTurn()) {
            System.out.println("Your new balance is " + user.getAuraBalance() + ".");
            gameService.setCooldown(user, getName(), 60);
        } else {
            gameService.useTool(user, "Fishing Rod"); // Decrement fishing rod uses after action
        }
        // --- MODIFICATION END ---
    }
}