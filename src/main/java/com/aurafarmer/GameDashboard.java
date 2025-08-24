// File: aurafarmer_final/src/main/java/com/aurafarmer/GameDashboard.java

package com.aurafarmer;

import com.aurafarmer.commands.*;
import com.aurafarmer.menus.ArenaMenu;
import com.aurafarmer.menus.GambleMenu;
import com.aurafarmer.menus.MarketplaceMenu;
import com.aurafarmer.menus.SocialHubMenu;
import com.aurafarmer.menus.WorkMenu;
import com.aurafarmer.model.ArenaMatch;
import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class GameDashboard {

    private final User currentUser;
    private final GameService gameService;
    private final List<Command> passiveCommands = new ArrayList<>();
    private boolean inGame = true; // Class-level flag to control the main loop

    // A set of command names allowed during an arena turn for quick lookup
    private final Set<String> arenaTurnCommands = new HashSet<>(Arrays.asList(
            "dig", "search", "hunt", "crime", "fish", "beg", "clean", "work", "quit_turn"
    ));


    public GameDashboard(User user) {
        this.currentUser = user;
        this.gameService = new GameService();
        initializeCommands();
    }

    private void initializeCommands() {
        passiveCommands.add(new ProfileCommand(gameService));
        passiveCommands.add(new InventoryCommand(gameService));
        passiveCommands.add(new DigCommand(gameService));
        passiveCommands.add(new SearchCommand(gameService));
        passiveCommands.add(new HuntCommand(gameService));
        passiveCommands.add(new CrimeCommand(gameService));
        passiveCommands.add(new PassiveCommand(gameService));
        passiveCommands.add(new FishingCommand(gameService));
        passiveCommands.add(new BegCommand(gameService));
        passiveCommands.add(new CleanCommand(gameService));
        passiveCommands.add(new QuitArenaTurnCommand(gameService));
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (inGame) {
            try {
                if (currentUser.isInArenaTurn()) {
                    handleArenaTurn(scanner);
                } else {
                    handleNormalTurn(scanner);
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage() + ". Returning to the main menu.");
                // e.printStackTrace(); // Uncomment for detailed debugging
            }
        }
        System.out.println("Logging out... Returning to the main menu.");
    }

    private void handleNormalTurn(Scanner scanner) {
        displayMainMenu();
        System.out.print("\nEnter a command number (or 0 to log out): ");
        String inputLine = scanner.nextLine();

        try {
            int choice = Integer.parseInt(inputLine);
            if (choice == 0) {
                this.inGame = false; // Set flag to exit the main loop
                return;
            }

            if (choice > 0 && choice <= passiveCommands.size()) {
                passiveCommands.get(choice - 1).execute(currentUser);
            } else {
                int menuChoice = choice - passiveCommands.size();
                switch (menuChoice) {
                    case 1: new WorkMenu(currentUser, gameService).start(); break;
                    case 2: new GambleMenu(currentUser, gameService).start(); break;
                    case 3: new MarketplaceMenu(currentUser, gameService).start(); break;
                    case 4: new SocialHubMenu(currentUser, gameService).start(); break;
                    case 5: new ArenaMenu(currentUser, gameService).start(); break;
                    default: System.out.println("❓ Invalid number. Please choose a number from the menu.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❓ Please enter a valid number.");
        }
    }

    private void handleArenaTurn(Scanner scanner) {
        ArenaMatch currentMatch = gameService.getUserArenaMatch(currentUser);
        if (currentMatch == null) {
            System.out.println("Your arena match seems to have ended. Returning to the main hub.");
            currentUser.setInArenaTurn(false);
            return;
        }

        long elapsedMillis = System.currentTimeMillis() - currentUser.getArenaTurnStartTime();
        long timeLimitMillis = currentMatch.getTimeLimitMinutes() * 60 * 1000L;

        if (elapsedMillis >= timeLimitMillis) {
            System.out.println("\n--- ⌛ Time's up! Arena Turn Ended Automatically ---");
            gameService.endArenaTurn(currentUser, true); // Forced end
            return;
        }

        displayArenaTurnMenu(timeLimitMillis - elapsedMillis);
        System.out.print("Enter command: ");
        String inputLine = scanner.nextLine().trim().toLowerCase();

        Command matchedCommand = findCommandByName(inputLine);

        if (arenaTurnCommands.contains(inputLine)) {
            if (matchedCommand != null) {
                matchedCommand.execute(currentUser);
            }
        } else {
            System.out.println("❌ You can only use the displayed earning commands or 'quit_turn' during your arena turn.");
        }
    }

    private Command findCommandByName(String name) {
        for (Command cmd : passiveCommands) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        return null;
    }

    private void displayMainMenu() {
        System.out.println("\n========================================");
        System.out.println("👑 Main Hub | " + currentUser.getUsername());
        System.out.println("💰 Balance: " + currentUser.getAuraBalance() + " Auras");
        System.out.println("========================================");

        System.out.println("\n--- Actions ---");
        for (int i = 0; i < passiveCommands.size(); i++) {
            Command cmd = passiveCommands.get(i);
            System.out.printf("%d. %-11s : %s\n", (i + 1), cmd.getName(), cmd.getDescription());
        }

        System.out.println("\n--- Menus ---");
        int menuNumber = passiveCommands.size() + 1;
        System.out.printf("%d. %-11s : %s\n", menuNumber++, "Careers", "💼 Find a job and work shifts.");
        System.out.printf("%d. %-11s : %s\n", menuNumber++, "Gamble", "🎲 Visit the casino to test your luck.");
        System.out.printf("%d. %-11s : %s\n", menuNumber++, "Marketplace", "🛒 Buy and sell items.");
        System.out.printf("%d. %-11s : %s\n", menuNumber++, "Social Hub", "🤝 Interact with other players.");
        System.out.printf("%d. %-11s : %s\n", menuNumber++, "Arena", "⚔️ Compete against other players!");
        System.out.println("----------------------------------------");
    }

    private void displayArenaTurnMenu(long timeLeftMillis) {
        long timeLeftSeconds = timeLeftMillis / 1000;
        System.out.println("\n--- ARENA TURN ACTIVE ---");
        // --- MODIFICATION: Display Arena Score instead of main balance ---
        System.out.println("Time Left: " + timeLeftSeconds + " seconds | Auras Earned This Turn: " + currentUser.getArenaScore());
        System.out.println("--- Available Commands ---");
        for (Command cmd : passiveCommands) {
            if (arenaTurnCommands.contains(cmd.getName())) {
                System.out.printf("- %-11s : %s\n", cmd.getName(), cmd.getDescription());
            }
        }
        System.out.println("--------------------------");
    }

    // This method is no longer needed in the main loop but can be useful elsewhere.
    private void clearInputBuffer() {
        try {
            while (System.in.available() > 0) { System.in.read(); }
        } catch (IOException e) { /*ignore*/ }
    }
}