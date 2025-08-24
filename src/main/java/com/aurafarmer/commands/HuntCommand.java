// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/HuntCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.model.UserInventoryItem;
import com.aurafarmer.service.GameService;
import java.util.Random;
import java.util.Scanner;
import com.aurafarmer.model.Item;
import java.util.List;

public class HuntCommand extends Command {

    public HuntCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "hunt";
    }

    @Override
    public String getDescription() {
        return "🏹 Go hunting in the wild. High risk, high reward.";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        // Fetch rifle item definition to get its current price and max quantity
        Item rifleDef = gameService.getShopItems().stream()
                .filter(item -> "Rifle".equalsIgnoreCase(item.getName()))
                .findFirst().orElse(null);

        if (rifleDef == null) {
            System.out.println("Error: Rifle item definition not found in shop. Cannot proceed with hunt.");
            return;
        }

        // Check for Rifle in inventory and its uses
        UserInventoryItem rifleItem = user.getInventory().values().stream()
                .filter(item -> "Rifle".equalsIgnoreCase(item.getItem().getName()))
                .findFirst()
                .orElse(null);

        if (rifleItem == null || rifleItem.getQuantity() <= 0 || (rifleItem.getItem().getUsesPerItem() != null && (rifleItem.getUsesLeft() == null || rifleItem.getUsesLeft() <= 0))) {
            System.out.println("You can't hunt without a rifle or your rifle has no uses left!");

            if (rifleDef.getMaxQuantity() != null && (rifleItem != null && rifleItem.getQuantity() >= rifleDef.getMaxQuantity())) {
                System.out.println("❌ You already have the maximum number of rifles (" + rifleDef.getMaxQuantity() + ").");
                return;
            }

            // Dynamically get the rifle's buy price from the fetched item definition
            System.out.print("A rifle costs " + rifleDef.getBuyPrice() + " Auras. Do you want to buy one? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("y")) {
                if (user.getAuraBalance() >= rifleDef.getBuyPrice()) {
                    gameService.updateUserBalance(user, -rifleDef.getBuyPrice());
                    gameService.addItemToUser(user, rifleDef.getId(), 1);
                    System.out.println("✅ You bought a rifle and can now hunt!");
                } else {
                    System.out.println("❌ You don't have enough Auras to buy a rifle.");
                }
            } else {
                System.out.println("You decided not to buy a rifle. Hunt command cancelled.");
            }
            return; // Exit after the check and potential purchase
        }

        // Simulate action time
        try {
            System.out.println("🏹 Venturing into the wild to hunt...");
            Thread.sleep( (new Random().nextInt(3) + 3) * 1000); // Wait 4 to 7 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Action was interrupted.");
            return;
        }

        Random random = new Random();
        int outcome = random.nextInt(100);

        if (outcome < 40) { // 40% chance of success (Aura + potential item)
            int foundAuras = random.nextInt(30001); // Aura range: 0 to 30,000
            gameService.updateUserBalance(user, foundAuras);

            int itemOutcome = random.nextInt(100); // 0-99 for item chance
            if (itemOutcome < 30) { // 30% chance for Rabbit Foot
                gameService.addItemToUser(user, 2, 1); // Add 1 Rabbit Foot (assuming ID 2)
                System.out.println("✅ A successful hunt! You return with valuables worth " + foundAuras + " Auras and a lucky Rabbit Foot.");
            } else if (itemOutcome < 50) { // 20% chance for Strange Fossil
                gameService.addItemToUser(user, 3, 1); // Add 1 Strange Fossil (assuming ID 3)
                System.out.println("✅ A successful hunt! You return with valuables worth " + foundAuras + " Auras and a Strange Fossil.");
            } else { // 50% chance for only Auras
                System.out.println("✅ A successful hunt! You return with valuables worth " + foundAuras + " Auras.");
            }

        } else if (outcome < 70) { // 30% chance to find nothing
            System.out.println("🤷 You hunt for hours but return with nothing.");
        } else { // 30% chance to lose Auras
            int lostAuras = random.nextInt(31) + 20;
            gameService.updateUserBalance(user, -lostAuras);
            // --- MODIFICATION START ---
            if(user.isInArenaTurn()){
                System.out.println("❌ Oh no! You were injured during the hunt and had to pay " + lostAuras + " Auras for supplies.");
            } else {
                System.out.println("❌ Oh no! You were injured during the hunt and had to pay " + lostAuras + " Auras for supplies. Your new balance is " + user.getAuraBalance() + ".");
            }
            // --- MODIFICATION END ---
        }

        // --- MODIFICATION START ---
        if(!user.isInArenaTurn()) {
            System.out.println("Your new balance is " + user.getAuraBalance() + ".");
            gameService.setCooldown(user, getName(), 60);
        }
        gameService.useTool(user, "Rifle"); // Decrement rifle uses after action
        // --- MODIFICATION END ---
    }
}