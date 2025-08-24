package com.aurafarmer.util;

import java.util.Random;
import java.util.Scanner;

// A utility class to run a skill-based typing minigame.
public class TypingMinigame {

    // --- NEW: Simple sentences for the minigame ---
    private static final String[] SENTENCES = {
            "the quick brown fox jumps over the lazy dog",
            "the farmer needs to harvest the crops soon",
            "a good tractor is essential for the farm",
            "we should sell our items at the marketplace",
            "the sun is shining on the beautiful sunflower field"
    };
    private static final int TIME_LIMIT_SECONDS = 25; // <-- UPDATED

    /**
     * Runs the typing minigame and returns a performance multiplier.
     * @return A double representing the salary multiplier (1.0 for success, 0.25 for failure).
     */
    public static double run() {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // --- NEW: Sentence-based game logic ---
        String targetSentence = SENTENCES[random.nextInt(SENTENCES.length)];

        System.out.println("\n--- Typing Challenge ---");
        System.out.println("Type the following sentence exactly as you see it.");
        System.out.println("You have " + TIME_LIMIT_SECONDS + " seconds. Go!");
        System.out.println("-------------------------");
        System.out.println(targetSentence);
        System.out.print("> ");

        long startTime = System.currentTimeMillis();
        String userInput = scanner.nextLine();
        long endTime = System.currentTimeMillis();

        long timeTakenSeconds = (endTime - startTime) / 1000;

        // --- NEW: Performance Evaluation ---
        if (userInput.equals(targetSentence) && timeTakenSeconds <= TIME_LIMIT_SECONDS) {
            System.out.println("\n✅ Perfect! You completed the task in " + timeTakenSeconds + " seconds.");
            System.out.println("You earned your full salary.");
            return 1.0; // 100% salary
        } else {
            if (timeTakenSeconds > TIME_LIMIT_SECONDS) {
                System.out.println("\n❌ Out of time! You took too long.");
            } else {
                System.out.println("\n❌ Incorrect! You made a typing mistake.");
            }
            System.out.println("Your pay has been severely docked.");
            return 0.25; // 25% penalty
        }
    }
}
