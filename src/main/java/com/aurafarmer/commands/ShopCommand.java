// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/ShopCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.Item;
import com.aurafarmer.model.User;
import com.aurafarmer.model.UserInventoryItem;
import com.aurafarmer.service.GameService;

import java.util.List;
import java.util.Scanner;

public class ShopCommand extends Command {

    public ShopCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public String getDescription() {
        return "🛒 Buy items from the shop.";
    }

    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        List<Item> shopItems = gameService.getShopItems();

        if (shopItems.isEmpty()) {
            System.out.println("The shop is currently empty.");
            return;
        }

        System.out.println("\n--- 🛒 Welcome to the Shop ---");
        System.out.println("Your balance: " + user.getAuraBalance() + " Auras 💰");
        System.out.println("-----------------------------");

        for (int i = 0; i < shopItems.size(); i++) {
            Item item = shopItems.get(i);
            String quantityLimit = "";
            if (item.getMaxQuantity() != null) {
                UserInventoryItem existingItem = user.getInventory().get(item.getId());
                int currentQuantity = (existingItem != null) ? existingItem.getQuantity() : 0;
                quantityLimit = " (You have: " + currentQuantity + "/" + item.getMaxQuantity() + ")";
            }
            System.out.printf("%d. %s - %d Auras%s\n", (i + 1), item.getName(), item.getBuyPrice(), quantityLimit);
        }
        System.out.println("-----------------------------");
        System.out.print("Enter the number of the item you want to buy (or 0 to cancel): ");

        try {
            int itemChoice = Integer.parseInt(scanner.nextLine());
            if (itemChoice == 0) {
                System.out.println("You left the shop.");
                return;
            }

            if (itemChoice > 0 && itemChoice <= shopItems.size()) {
                Item selectedItem = shopItems.get(itemChoice - 1);

                System.out.print("How many " + selectedItem.getName() + "s do you want to buy? ");
                int quantityToBuy = Integer.parseInt(scanner.nextLine());

                if (quantityToBuy <= 0) {
                    System.out.println("❌ You must buy at least one item.");
                    return;
                }

                // --- MODIFIED LOGIC START ---
                if (selectedItem.getMaxQuantity() != null) {
                    UserInventoryItem existingItem = user.getInventory().get(selectedItem.getId());
                    int currentQuantity = (existingItem != null) ? existingItem.getQuantity() : 0;
                    int remainingCapacity = selectedItem.getMaxQuantity() - currentQuantity;

                    if (remainingCapacity <= 0) {
                        System.out.println("❌ You already have the maximum number of " + selectedItem.getName() + "s (" + selectedItem.getMaxQuantity() + ").");
                        return;
                    }

                    if (quantityToBuy > remainingCapacity) {
                        System.out.println("⚠️ You can only carry " + remainingCapacity + " more " + selectedItem.getName() + "s.");
                        System.out.print("Do you want to buy " + remainingCapacity + " " + selectedItem.getName() + "s instead? (y/n): ");
                        String confirmation = scanner.nextLine().toLowerCase();

                        if (confirmation.equals("y")) {
                            quantityToBuy = remainingCapacity; // Adjust quantity to buy to max capacity
                        } else {
                            System.out.println("Purchase cancelled.");
                            return; // User chose not to buy the limited quantity
                        }
                    }
                }
                // --- MODIFIED LOGIC END ---

                long totalCost = (long) quantityToBuy * selectedItem.getBuyPrice();

                if (user.getAuraBalance() < totalCost) {
                    System.out.println("❌ You don't have enough Auras to buy " + quantityToBuy + " " + selectedItem.getName() + "s.");
                    return;
                }

                gameService.updateUserBalance(user, (int) -totalCost);
                gameService.addItemToUser(user, selectedItem.getId(), quantityToBuy);

                System.out.println("✅ You bought " + quantityToBuy + "x " + selectedItem.getName() + " for " + totalCost + " Auras!");
                System.out.println("Your new balance is: " + user.getAuraBalance());

            } else {
                System.out.println("❓ Invalid item number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❓ Please enter a valid number.");
        }
    }
}