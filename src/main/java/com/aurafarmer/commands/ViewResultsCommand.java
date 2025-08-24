package com.aurafarmer.commands;

import com.aurafarmer.model.ArenaResultEntry;
import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.List;

public class ViewResultsCommand extends Command {
    public ViewResultsCommand(GameService gameService) { super(gameService); }
    @Override public String getName() { return "view_results"; }
    @Override public String getDescription() { return "🏆 View the results of your past arena matches."; }
    @Override public void execute(User user) {
        System.out.println("Viewing your match results...");
        List<ArenaResultEntry> results = gameService.getCompletedMatches(user);
        if (results.isEmpty()) {
            System.out.println("You have no completed matches.");
            return;
        }
        System.out.println("\n--- 🏆 Your Match History ---");
        for (ArenaResultEntry result : results) {
            System.out.printf("Opponent: %s | Score: %d - %d | Result: %s | Date: %s\n",
                    result.opponentUsername, result.yourScore, result.opponentScore, result.result, result.matchDate);
        }
        System.out.println("-----------------------------");
    }
}