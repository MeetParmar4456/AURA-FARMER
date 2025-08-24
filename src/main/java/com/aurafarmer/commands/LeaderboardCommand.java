package com.aurafarmer.commands;

import com.aurafarmer.model.LeaderboardEntry;
import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import com.aurafarmer.util.LeaderboardTree;

import java.util.List;

public class LeaderboardCommand extends Command {

    public LeaderboardCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return "🏆 View the top players by Aura balance.";
    }

    @Override
    public void execute(User user) {
        List<LeaderboardEntry> entries = gameService.getLeaderboardEntries();

        // Build the tree
        LeaderboardTree leaderboard = new LeaderboardTree();
        for (LeaderboardEntry entry : entries) {
            leaderboard.insert(entry);
        }

        // Get the sorted list from the tree
        List<LeaderboardEntry> sortedEntries = leaderboard.getSortedEntries();

        System.out.println("\n--- 🏆 Global Leaderboard ---");
        int rank = 1;
        for (LeaderboardEntry entry : sortedEntries) {
            System.out.printf("%d. %-20s - %d Auras\n", rank++, entry.username, entry.auraBalance);
            if (rank > 10) { // Only show top 10
                break;
            }
        }
        System.out.println("-----------------------------");
    }
}
