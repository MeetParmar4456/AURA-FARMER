// File: aurafarmer_final/src/main/java/com/aurafarmer/menus/ArenaMenu.java

package com.aurafarmer.menus;

import com.aurafarmer.commands.*;
import com.aurafarmer.model.ArenaMatch;
import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ArenaMenu {
    private final User currentUser;
    private final GameService gameService;
    private final List<Command> commands = new ArrayList<>();

    public ArenaMenu(User user, GameService gameService) {
        this.currentUser = user;
        this.gameService = gameService;
        initializeCommands();
    }

    private void initializeCommands() {
        commands.add(new CreateChallengeCommand(gameService));
        commands.add(new BrowseChallengesCommand(gameService));
        commands.add(new ViewMyMatchCommand(gameService));
        commands.add(new StartArenaTurnCommand(gameService));
        commands.add(new ViewResultsCommand(gameService)); // New command to view results
        commands.add(new DisbandChallengeCommand(gameService));
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n--- ⚔️ The Battle Arena ---");
            System.out.println("Your balance: " + currentUser.getAuraBalance() + " Auras 💰");
            System.out.println("--------------------------");

            // Removed the automatic display of results here

            System.out.println("Current active match for " + currentUser.getUsername() + ":");
            gameService.displayUserArenaMatchStatus(currentUser); // Display current match status

            System.out.println("\n--- Arena Actions ---");
            for (int i = 0; i < commands.size(); i++) {
                Command cmd = commands.get(i);
                System.out.printf("%d. %-20s : %s\n", (i + 1), cmd.getName(), cmd.getDescription());
            }
            System.out.println("--------------------------");
            System.out.print("Enter a command number (or 0 to go back): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) {
                    inMenu = false;
                    continue;
                }
                if (choice > 0 && choice <= commands.size()) {
                    commands.get(choice - 1).execute(currentUser);
                    clearInputBuffer();
                } else {
                    System.out.println("❓ Invalid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❓ Please enter a valid number.");
            }
        }
    }

    private void clearInputBuffer() {
        try {
            while (System.in.available() > 0) { System.in.read(); }
        } catch (IOException e) { /* Ignore */ }
    }
}