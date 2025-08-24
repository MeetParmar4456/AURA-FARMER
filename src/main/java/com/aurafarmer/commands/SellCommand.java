package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.model.UserInventoryItem;
import com.aurafarmer.service.GameService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SellCommand extends Command {

    public SellCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "sell";
    }

    @Override
    public String getDescription() {
        return "💸 Sell items from your inventory for Auras.";
    }

    @Override
    public void execute(User user) {
        if (user.getInventory().isEmpty()) {
            System.out.println("Your inventory is empty. Nothing to sell!");
            return;
        }

        List<UserInventoryItem> sellableItems = new ArrayList<>(user.getInventory().values());

        System.out.println("\n--- 💸 Your Inventory (Sell) ---");
        for (int i = 0; i < sellableItems.size(); i++) {
            UserInventoryItem invItem = sellableItems.get(i);
            Integer sellPrice = invItem.getItem().getSellPrice();
            String price = (sellPrice != null) ? sellPrice + " Auras" : "Not sellable";
            System.out.printf("%d. %s (x%d) - %s\n", (i + 1), invItem.getItem().getName(), invItem.getQuantity(), price);
        }
        System.out.println("---------------------------------");
        System.out.print("Enter the number of the item you want to sell (or 0 to cancel): ");

        Scanner scanner = new Scanner(System.in);
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                System.out.println("Cancelled selling.");
                return;
            }

            if (choice > 0 && choice <= sellableItems.size()) {
                UserInventoryItem selectedInvItem = sellableItems.get(choice - 1);
                int currentQuantity = selectedInvItem.getQuantity();

                System.out.print("You have " + currentQuantity + ". How many do you want to sell? ");
                int desiredQuantity = Integer.parseInt(scanner.nextLine());
                int quantityToSell = desiredQuantity;

                if (desiredQuantity <= 0) {
                    System.out.println("❌ You must sell a positive number of items.");
                    return;
                }

                // --- NEW: Smart selling logic ---
                if (desiredQuantity > currentQuantity) {
                    System.out.println("You don't have that many. You only have " + currentQuantity + ".");
                    System.out.print("Do you want to sell all " + currentQuantity + " instead? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        quantityToSell = currentQuantity;
                    } else {
                        System.out.println("Sale cancelled.");
                        return;
                    }
                }

                gameService.sellItem(user, selectedInvItem, quantityToSell);

            } else {
                System.out.println("❓ Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❓ Please enter a valid number.");
        }
    }
}
