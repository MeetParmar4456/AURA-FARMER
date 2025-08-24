// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/CreateChallengeCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

import java.util.Scanner;

public class CreateChallengeCommand extends Command {
    public CreateChallengeCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "create_challenge";
    }

    @Override
    public String getDescription() {
        return "⚔️ Create a new arena challenge.";
    }

    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        String challengeType = "AuraRush"; // For now, we'll hardcode the challenge type
        int timeLimitMinutes;

        // Prompt user for challenge details
        System.out.println("\n--- Creating a new challenge ---");

        while (true) {
            System.out.print("Enter the time limit in minutes (10-15): ");
            try {
                timeLimitMinutes = Integer.parseInt(scanner.nextLine());
                if (timeLimitMinutes >= 10 && timeLimitMinutes <= 15) {
                    break; // Exit the loop if the input is valid
                } else {
                    System.out.println("❌ Time limit must be between 10 and 15 minutes.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }

        // Call GameService to handle the logic
        gameService.createArenaChallenge(user, challengeType, timeLimitMinutes);
    }
}