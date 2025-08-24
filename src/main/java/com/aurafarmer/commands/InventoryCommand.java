package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.model.UserInventoryItem;
import com.aurafarmer.service.GameService;
import java.util.Map;

public class InventoryCommand extends Command {

    public InventoryCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "inventory";
    }

    @Override
    public String getDescription() {
        return "🎒 Check the items you are carrying.";
    }

    @Override
    public void execute(User user) {
        System.out.println("\n--- 🎒 Your Inventory ---");
        Map<Integer, UserInventoryItem> inventory = user.getInventory();

        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            for (UserInventoryItem invItem : inventory.values()) {
                System.out.printf("- %s (x%d)\n", invItem.getItem().getName(), invItem.getQuantity());
            }
        }
        System.out.println("-------------------------");
    }
}
