// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/CrimeCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.model.UserInventoryItem;
import com.aurafarmer.model.Item;
import com.aurafarmer.service.GameService;
import java.util.Random;
import java.util.Scanner;
import java.util.List;

public class CrimeCommand extends Command {

    public CrimeCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "crime";
    }

    @Override
    public String getDescription() {
        return "👺 Commit a petty crime. Very risky, but potentially very rewarding.";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        // Fetch mask item definition to get its current price and max quantity
        Item maskDef = gameService.getShopItems().stream()
                .filter(item -> "Mask".equalsIgnoreCase(item.getName()))
                .findFirst().orElse(null);

        if (maskDef == null) {
            System.out.println("Error: Mask item definition not found in shop. Cannot proceed with crime.");
            return;
        }

        // Check for Mask in inventory and its uses
        UserInventoryItem maskItem = user.getInventory().values().stream()
                .filter(item -> "Mask".equalsIgnoreCase(item.getItem().getName()))
                .findFirst()
                .orElse(null);

        if (maskItem == null || maskItem.getQuantity() <= 0 || (maskItem.getItem().getUsesPerItem() != null && (maskItem.getUsesLeft() == null || maskItem.getUsesLeft() <= 0))) {
            System.out.println("You can't commit a crime without a mask or your mask has no uses left!");

            if (maskDef.getMaxQuantity() != null && (maskItem != null && maskItem.getQuantity() >= maskDef.getMaxQuantity())) {
                System.out.println("❌ You already have the maximum number of masks (" + maskDef.getMaxQuantity() + ").");
                return;
            }

            // Dynamically get the mask's buy price from the fetched item definition
            System.out.print("A mask costs " + maskDef.getBuyPrice() + " Auras. Do you want to buy one? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("y")) {
                if (user.getAuraBalance() >= maskDef.getBuyPrice()) {
                    gameService.updateUserBalance(user, -maskDef.getBuyPrice());
                    gameService.addItemToUser(user, maskDef.getId(), 1);
                    System.out.println("✅ You bought a mask and can now commit a crime!");
                } else {
                    System.out.println("❌ You don't have enough Auras to buy a mask.");
                }
            } else {
                System.out.println("You decided not to buy a mask. Crime command cancelled.");
            }
            return; // Exit after the check and potential purchase
        }

        // Simulate action time
        try {
            System.out.println("👺 Looking for an opportunity...");
            Thread.sleep( (new Random().nextInt(3) + 2) * 1000); // Wait 2 to 4 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Action was interrupted.");
            return;
        }

        Random random = new Random();
        int outcome = random.nextInt(100);

        if (outcome < 50) { // 50% chance to win
            int foundAuras = random.nextInt(35001); // Aura range: 0 to 35,000
            gameService.updateUserBalance(user, foundAuras);
            // --- MODIFICATION START ---
            if(user.isInArenaTurn()){
                System.out.println("✅ Success! You got away with " + foundAuras + " Auras.");
            } else {
                System.out.println("✅ Success! You got away with " + foundAuras + " Auras. Your new balance is " + user.getAuraBalance() + ".");
            }
            // --- MODIFICATION END ---
        } else { // 50% chance to lose
            int fine = random.nextInt(101) + 50;
            gameService.updateUserBalance(user, -fine);
            // --- MODIFICATION START ---
            if(user.isInArenaTurn()){
                System.out.println("👮 Busted! You were caught and had to pay a fine of " + fine + " Auras.");
            } else {
                System.out.println("👮 Busted! You were caught and had to pay a fine of " + fine + " Auras. Your new balance is " + user.getAuraBalance() + ".");
            }
            // --- MODIFICATION END ---
        }

        gameService.useTool(user, "Mask"); // Decrement mask uses after action
        if(!user.isInArenaTurn()) {
            gameService.setCooldown(user, getName(), 90);
        }
    }
}