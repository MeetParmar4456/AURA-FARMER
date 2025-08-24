// File: aurafarmer_final/src/main/java/com/aurafarmer/service/GameService.java

package com.aurafarmer.service;

import com.aurafarmer.db.DatabaseManager;
import com.aurafarmer.model.*;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameService {
    private final DatabaseManager dbManager;

    public GameService() {
        this.dbManager = new DatabaseManager();
    }

    public List<LeaderboardEntry> getLeaderboardEntries() {
        return dbManager.getLeaderboardEntries();
    }

    public List<String> getAllPlayerNames() {
        return dbManager.getAllPlayerNames();
    }


    public void robPlayer(User robber, String targetUsername) {
        if (isCoolingDown(robber, "rob")) {
            return;
        }

        if (robber.isPassiveMode()) {
            System.out.println("You cannot rob others while in passive mode. Turn it off first with the 'passive' command.");
            return;
        }

        if (robber.getUsername().equalsIgnoreCase(targetUsername)) {
            System.out.println("You can't rob yourself!");
            return;
        }
        if (robber.getAuraBalance() < 500) {
            System.out.println("You need at least 500 Auras to attempt a robbery.");
            return;
        }

        User victim = dbManager.getUserByUsername(targetUsername);
        if (victim == null) {
            System.out.println("Player '" + targetUsername + "' not found.");
            return;
        }
        if (victim.isPassiveMode()) {
            System.out.println(victim.getUsername() + " is in passive mode and cannot be robbed.");
            return;
        }
        if (victim.getAuraBalance() < 1000) {
            System.out.println(victim.getUsername() + " doesn't have enough Auras to be worth robbing.");
            return;
        }

        Random random = new Random();
        boolean success = random.nextInt(100) < 40;

        if (success) {
            int amountStolen = (int) (victim.getAuraBalance() * (random.nextDouble() * 0.25 + 0.05));
            boolean transactionSuccess = dbManager.performRobbery(robber.getUserId(), victim.getUserId(), amountStolen, 0);
            if (transactionSuccess) {
                robber.setAuraBalance(robber.getAuraBalance() + amountStolen);
                System.out.println("✅ You stole " + amountStolen + " Auras from " + victim.getUsername() + ".");
                System.out.println("A message was sent to " + victim.getUsername() + ": They stole " + amountStolen + " Auras from you.");
            }
        } else {
            int fine = (int) (robber.getAuraBalance() * 0.15);
            updateUserBalance(robber, -fine);
            System.out.println("👮 Busted! You were caught and fined " + fine + " Auras.");
        }

        setCooldown(robber, "rob", 10 * 60);
    }

    public void workShift(User user, double performanceMultiplier) {
        UserJob currentJob = user.getCurrentJob();
        if (currentJob == null) {
            return;
        }

        int baseSalary = currentJob.getJob().getSalary();
        int finalEarnings = (int) (baseSalary * performanceMultiplier);

        updateUserBalance(user, finalEarnings);
        dbManager.incrementUserShifts(user.getUserId());
        currentJob.incrementShifts();
        user.setTotalShiftsWorked(user.getTotalShiftsWorked() + 1);

        System.out.println("You completed your shift as a " + currentJob.getJob().getName() + " and earned " + finalEarnings + " Auras.");
        System.out.println("Your new balance is: " + user.getAuraBalance());

        if (!user.isInArenaTurn()) {
            int cooldownSeconds = currentJob.getJob().getCooldownMinutes() * 60;
            setCooldown(user, "work", cooldownSeconds);
        }
    }

    public List<Job> getAvailableJobs() {
        return dbManager.getAvailableJobs();
    }

    public void changeJob(User user, Job newJob) {
        if (user.getCurrentJob() != null) {
            System.out.println("❌ You already have a job! You must resign first with the 'resign' command.");
            return;
        }

        if (isCoolingDown(user, "job_application")) {
            System.out.println("You recently resigned. You must wait before applying for a new job.");
            return;
        }
        if (user.getTotalShiftsWorked() < newJob.getRequiredShifts()) {
            System.out.println("❌ You are not experienced enough for this job. You need " + newJob.getRequiredShifts() + " total shifts, but you only have " + user.getTotalShiftsWorked() + ".");
            return;
        }

        dbManager.setUserJob(user.getUserId(), newJob.getId());
        user.setCurrentJob(new UserJob(newJob, 0));
        System.out.println("💼 You are now a " + newJob.getName() + "!");
    }

    public void resignJob(User user) {
        if (user.getCurrentJob() == null) {
            System.out.println("You don't have a job to resign from.");
            return;
        }
        dbManager.resignJob(user.getUserId());
        user.setCurrentJob(null);

        dbManager.deleteCooldown(user.getUserId(), "work");

        setCooldown(user, "job_application", 6 * 60 * 60);
        System.out.println("You have resigned from your job. You cannot apply for a new one for 6 hours.");
    }

    public List<Item> getShopItems() {
        return dbManager.getShopItems();
    }

    public void sellItem(User user, UserInventoryItem invItem, int quantityToSell) {
        Item item = invItem.getItem();
        if (item.getSellPrice() == null) {
            System.out.println("❌ This item cannot be sold.");
            return;
        }
        if (invItem.getQuantity() < quantityToSell) {
            System.out.println("❌ You don't have that many " + item.getName() + "s to sell.");
            return;
        }

        int earnings = item.getSellPrice() * quantityToSell;

        updateUserBalance(user, earnings);
        dbManager.removeItemQuantity(user.getUserId(), item.getId(), quantityToSell);

        invItem.addQuantity(-quantityToSell);
        if (invItem.getQuantity() <= 0) {
            user.getInventory().remove(item.getId());
        }

        System.out.println("✅ You sold " + quantityToSell + " " + item.getName() + " for " + earnings + " Auras.");
        System.out.println("Your new balance is: " + user.getAuraBalance());
    }

    public void addItemToUser(User user, int itemId, int quantity) {
        Item itemToAdd = dbManager.getItemById(itemId);
        if (itemToAdd == null) {
            System.out.println("Error: Item with ID " + itemId + " not found.");
            return;
        }

        UserInventoryItem existingItem = user.getInventory().get(itemId);

        if (itemToAdd.getMaxQuantity() != null) {
            int currentQuantity = (existingItem != null) ? existingItem.getQuantity() : 0;
            if (currentQuantity + quantity > itemToAdd.getMaxQuantity()) {
                System.out.println("❌ You cannot carry more than " + itemToAdd.getMaxQuantity() + " " + itemToAdd.getName() + "s.");
                return;
            }
        }

        Integer initialUsesForDB = null;
        if (itemToAdd.getUsesPerItem() != null) {
            if (existingItem == null || existingItem.getUsesLeft() == null || existingItem.getUsesLeft() <= 0) {
                initialUsesForDB = itemToAdd.getUsesPerItem();
            }
        }

        dbManager.addItemToUserInventory(user.getUserId(), itemId, quantity, initialUsesForDB);

        if (existingItem != null) {
            existingItem.addQuantity(quantity);
            if (itemToAdd.getUsesPerItem() != null && (existingItem.getUsesLeft() == null || existingItem.getUsesLeft() <= 0)) {
                existingItem.setUsesLeft(itemToAdd.getUsesPerItem());
            }
        } else {
            user.getInventory().put(itemId, new UserInventoryItem(itemToAdd, quantity, initialUsesForDB));
        }
        System.out.println("🎁 You received " + quantity + "x " + itemToAdd.getName() + "!");
    }

    public boolean useTool(User user, String toolName) {
        UserInventoryItem toolItem = user.getInventory().values().stream()
                .filter(item -> toolName.equalsIgnoreCase(item.getItem().getName()))
                .findFirst()
                .orElse(null);

        if (toolItem == null || toolItem.getQuantity() <= 0) {
            System.out.println("Error: You don't have a " + toolName + " to use.");
            return false;
        }
        if (toolItem.getItem().getUsesPerItem() == null) {
            System.out.println("Error: " + toolName + " is not a tool with limited uses.");
            return false;
        }
        if (toolItem.getUsesLeft() == null || toolItem.getUsesLeft() <= 0) {
            System.out.println("Error: Your " + toolName + " has no uses left. You need to buy a new one.");
            return false;
        }

        int newUses = toolItem.getUsesLeft() - 1;
        toolItem.setUsesLeft(newUses);
        dbManager.updateItemUsesLeft(user.getUserId(), toolItem.getItem().getId(), newUses);
        System.out.println("Your " + toolItem.getItem().getName() + " now has " + newUses + " uses left.");

        if (newUses <= 0) {
            System.out.println("Your " + toolItem.getItem().getName() + " broke! One has been removed from your inventory.");
            toolItem.addQuantity(-1);
            dbManager.removeItemQuantity(user.getUserId(), toolItem.getItem().getId(), 1);

            if (toolItem.getQuantity() <= 0) {
                user.getInventory().remove(toolItem.getItem().getId());
            }
        }
        return true;
    }


    public boolean isCoolingDown(User user, String commandName) {
        long secondsLeft = dbManager.getCooldownSecondsLeft(user.getUserId(), commandName);
        if (secondsLeft > 0) {
            long hours = secondsLeft / 3600;
            long minutes = (secondsLeft % 3600) / 60;
            long seconds = secondsLeft % 60;
            System.out.printf("❌ This is on cooldown! Please wait %d hours, %d minutes, and %d seconds.\n", hours, minutes, seconds);
            return true;
        }
        return false;
    }

    public void setCooldown(User user, String commandName, int durationSeconds) {
        dbManager.setCooldown(user.getUserId(), commandName, durationSeconds);
    }

    public void setPassiveMode(User user, boolean status) {
        user.setPassiveMode(status);
        dbManager.updatePassiveMode(user.getUserId(), status);
    }

    // --- ARENA SERVICE METHODS ---

    public ArenaMatch getUserArenaMatch(User user) {
        return dbManager.findArenaMatchForUser(user.getUserId());
    }

    // --- NEW METHOD ---
    public List<ArenaResultEntry> getCompletedMatches(User user) {
        List<ArenaMatch> completedMatches = dbManager.findCompletedArenaMatchesForUser(user.getUserId());
        return completedMatches.stream().map(match -> {
            String opponentUsername = match.getOpponentUsername(user.getUserId());
            int yourScore = (match.getPlayer1Id() == user.getUserId()) ? match.getPlayer1Score() : match.getPlayer2Score();
            int opponentScore = (match.getPlayer1Id() == user.getUserId()) ? match.getPlayer2Score() : match.getPlayer1Score();
            String result = "Tie";
            if (match.getWinnerId() != null) {
                result = (match.getWinnerId() == user.getUserId()) ? "Win" : "Loss";
            }
            return new ArenaResultEntry(opponentUsername, yourScore, opponentScore, result, match.getLastActiveAt());
        }).collect(Collectors.toList());
    }

    public void createArenaChallenge(User user, String challengeType, int timeLimitMinutes) {
        if (dbManager.findArenaMatchForUser(user.getUserId()) != null) {
            System.out.println("❌ You are already in an active arena match. Use 'view_my_match' to see its status.");
            return;
        }

        int matchId = dbManager.createArenaMatch(user.getUserId(), challengeType, timeLimitMinutes);
        if (matchId != -1) {
            System.out.println("✅ Arena challenge created (Match ID: " + matchId + "). Waiting for another player to join.");
            System.out.println("You can now wait or do other activities. Come back to 'arena' to check status.");
        } else {
            System.out.println("❌ Failed to create arena challenge.");
        }
    }

    public void browsePendingChallenges(User user) {
        if (dbManager.findArenaMatchForUser(user.getUserId()) != null) {
            System.out.println("❌ You are already in an active arena match. Use 'view_my_match' to see its status.");
            return;
        }

        List<ArenaMatch> pendingMatches = dbManager.findPendingArenaMatches();
        if (pendingMatches.isEmpty()) {
            System.out.println("🤷 No pending arena challenges found. You can create one!");
            return;
        }

        System.out.println("\n--- Pending Arena Challenges ---");
        for (ArenaMatch match : pendingMatches) {
            if (match.getPlayer1Id() == user.getUserId()) {
                continue;
            }
            System.out.printf("Match ID: %d | Creator: %s | Type: %s | Time Limit: %d mins\n",
                    match.getMatchId(), match.getPlayer1Username(), match.getChallengeType(), match.getTimeLimitMinutes());
        }
        System.out.println("---------------------------------");
        System.out.print("Enter the Match ID you want to join (or 0 to cancel): ");

        Scanner scanner = new Scanner(System.in);
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                System.out.println("Cancelled browsing challenges.");
                return;
            }

            ArenaMatch selectedMatch = null;
            for (ArenaMatch match : pendingMatches) {
                if (match.getMatchId() == choice && match.getPlayer1Id() != user.getUserId()) {
                    selectedMatch = match;
                    break;
                }
            }

            if (selectedMatch != null) {
                if (dbManager.joinArenaMatch(selectedMatch.getMatchId(), user.getUserId())) {
                    System.out.println("✅ You have joined Match ID: " + selectedMatch.getMatchId() + " against " + selectedMatch.getPlayer1Username() + "!");
                    System.out.println("It's your turn to play. Use 'start_turn' to begin.");
                } else {
                    System.out.println("❌ Failed to join match. It might have been taken or disbanded.");
                }
            } else {
                System.out.println("❓ Invalid Match ID or you cannot join your own challenge.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❓ Please enter a valid number.");
        }
    }

    public void displayUserArenaMatchStatus(User user) {
        ArenaMatch match = dbManager.findArenaMatchForUser(user.getUserId());

        if (match == null) {
            System.out.println("🤷 You are not currently in an active arena match.");
            return;
        }

        System.out.println("\n--- Your Arena Match (ID: " + match.getMatchId() + ") ---");
        System.out.println("Challenge Type: " + match.getChallengeType() + " | Time Limit: " + match.getTimeLimitMinutes() + " mins");
        System.out.println("Status: " + match.getStatus());

        String player1Status = (match.getPlayer1Score() != null) ? match.getPlayer1Score() + " Auras" : "Not played";
        String player2Status = (match.getPlayer2Score() != null) ? match.getPlayer2Score() + " Auras" : "Not played";

        System.out.println("Player 1 (" + match.getPlayer1Username() + "): " + player1Status);
        if (match.getPlayer2Id() != null) {
            System.out.println("Player 2 (" + match.getPlayer2Username() + "): " + player2Status);
        } else {
            System.out.println("Player 2: Waiting for opponent to join...");
        }

        if (match.getStatus() == ArenaMatch.MatchStatus.PENDING) {
            System.out.println("Waiting for an opponent to join your challenge.");
        } else if (match.getStatus() == ArenaMatch.MatchStatus.PLAYER2_TURN && match.getPlayer2Id() == user.getUserId()) {
            System.out.println("It's your turn! Use 'start_turn' to play.");
        } else if (match.getStatus() == ArenaMatch.MatchStatus.PLAYER1_TURN && match.getPlayer1Id() == user.getUserId()) {
            System.out.println("It's your turn! Use 'start_turn' to play.");
        } else if (match.getStatus() == ArenaMatch.MatchStatus.DISBANDED) {
            System.out.println("This match was disbanded.");
            System.out.println("Use 'disband_challenge' to clear this match from your view.");
        }

        long inactiveDurationSeconds = Duration.between(match.getLastActiveAt().toInstant(), Instant.now()).getSeconds();
        long disbandThresholdSeconds = 10 * 60;

        if ((match.getStatus() == ArenaMatch.MatchStatus.PENDING && inactiveDurationSeconds >= disbandThresholdSeconds) ||
                (match.getStatus() == ArenaMatch.MatchStatus.PLAYER1_TURN && match.getPlayer2Id() == user.getUserId() && inactiveDurationSeconds >= disbandThresholdSeconds) ||
                (match.getStatus() == ArenaMatch.MatchStatus.PLAYER2_TURN && match.getPlayer1Id() == user.getUserId() && inactiveDurationSeconds >= disbandThresholdSeconds)) {
            System.out.println("\n⚠️ This match has been inactive for " + (inactiveDurationSeconds / 60) + " minutes. You can 'disband_challenge'.");
        }
    }

    public void updateUserBalance(User user, int amountToAdd) {
        // Divert funds to arena score if in a turn
        if (user.isInArenaTurn()) {
            user.addArenaScore(amountToAdd);
            // We do NOT update the main balance or database here yet.
            return;
        }

        int newBalance = user.getAuraBalance() + amountToAdd;

        if (newBalance < 0) {
            newBalance = 0;
        }

        user.setAuraBalance(newBalance);
        dbManager.updateUserBalance(user.getUserId(), newBalance);
    }

    public void startArenaTurn(User user) {
        ArenaMatch match = dbManager.findArenaMatchForUser(user.getUserId());

        if (match == null || match.getStatus() == ArenaMatch.MatchStatus.COMPLETED || match.getStatus() == ArenaMatch.MatchStatus.DISBANDED || match.getStatus() == ArenaMatch.MatchStatus.PENDING) {
            System.out.println("❌ You are not in an active challenge that requires your turn. Use 'arena' to check status.");
            return;
        }

        boolean isPlayer1 = (match.getPlayer1Id() == user.getUserId());
        boolean isPlayer2 = (match.getPlayer2Id() != null && match.getPlayer2Id() == user.getUserId());

        if ((isPlayer1 && match.getStatus() != ArenaMatch.MatchStatus.PLAYER1_TURN) ||
                (isPlayer2 && match.getStatus() != ArenaMatch.MatchStatus.PLAYER2_TURN)) {
            System.out.println("❌ It's not your turn yet. Current status: " + match.getStatus());
            return;
        }

        if (user.isInArenaTurn()) {
            System.out.println("❌ You are already in an arena turn!");
            return;
        }

        user.setInArenaTurn(true);
        user.setArenaScore(0);
        user.setArenaTurnStartTime(System.currentTimeMillis());
        user.setArenaMatchId(match.getMatchId());

        System.out.println("\n--- Your Arena Turn Started (Match ID: " + match.getMatchId() + ") ---");
        System.out.println("Challenge Type: " + match.getChallengeType() + " | Time Limit: " + match.getTimeLimitMinutes() + " minutes");
        System.out.println("Earn as many Auras as possible using ANY earning command!");
        System.out.println("Your turn will automatically end after " + match.getTimeLimitMinutes() + " minutes.");
        System.out.println("You can type 'quit_turn' at any time to end your turn early.");
        System.out.println("Good luck!");

        dbManager.updateArenaMatchLastActive(match.getMatchId(), Timestamp.from(Instant.now()));
    }

    public void endArenaTurn(User user, boolean forcedEnd) {
        if (!user.isInArenaTurn()) {
            System.out.println("Error: Not currently in an arena turn to end.");
            return;
        }

        ArenaMatch match = dbManager.findArenaMatchForUser(user.getUserId());
        if (match == null || match.getMatchId() != user.getArenaMatchId()) {
            System.out.println("Error: Arena match state mismatch. Cannot end turn.");
            user.setInArenaTurn(false);
            user.setArenaScore(0);
            user.setArenaTurnStartTime(0);
            user.setArenaMatchId(-1);
            return;
        }

        int score = user.getArenaScore();

        System.out.println("\n--- Your Arena Turn Ended (Match ID: " + match.getMatchId() + ") ---");
        System.out.println("You gained " + score + " Auras during this turn.");
        if (forcedEnd) {
            System.out.println("Time's up!");
        }

        user.setInArenaTurn(false);

        boolean isPlayer1 = (match.getPlayer1Id() == user.getUserId());
        String newStatus;

        if (isPlayer1) { // Player 1 is the second to play, so the match completes.
            newStatus = "COMPLETED";
            dbManager.updateArenaPlayerScoreAndStatus(match.getMatchId(), user.getUserId(), score, newStatus);
            System.out.println("Both players have completed their turns. Results are ready!");

            ArenaMatch completedMatch = dbManager.findArenaMatchById(match.getMatchId());
            if (completedMatch != null && completedMatch.getPlayer1Score() != null && completedMatch.getPlayer2Score() != null) {

                // --- MODIFICATION START: Winner-takes-all and loser-loses-all reward logic ---
                int p1_score = completedMatch.getPlayer1Score();
                int p2_score = completedMatch.getPlayer2Score();
                int p1_id = completedMatch.getPlayer1Id();
                int p2_id = completedMatch.getPlayer2Id();

                User player1 = dbManager.getUserById(p1_id);
                User player2 = dbManager.getUserById(p2_id);

                if (p1_score > p2_score) {
                    dbManager.updateArenaMatchWinner(match.getMatchId(), p1_id);
                    // Add winnings to winner's current balance
                    int winnerNewBalance = player1.getAuraBalance() + p1_score;
                    dbManager.updateUserBalance(p1_id, winnerNewBalance);

                    // Reset loser's balance to 0 in DB
                    dbManager.updateUserBalance(p2_id, 0);

                    // Update the current user's object in memory for correct display
                    if(user.getUserId() == p1_id) { user.setAuraBalance(winnerNewBalance); }
                    if(user.getUserId() == p2_id) { user.setAuraBalance(0); }

                    System.out.println("🎉 " + player1.getUsername() + " wins and collects " + p1_score + " Auras!");
                    System.out.println("☠️ " + player2.getUsername() + " has lost all their Auras!");

                } else if (p2_score > p1_score) {
                    dbManager.updateArenaMatchWinner(match.getMatchId(), p2_id);
                    // Add winnings to winner's current balance
                    int winnerNewBalance = player2.getAuraBalance() + p2_score;
                    dbManager.updateUserBalance(p2_id, winnerNewBalance);

                    // Reset loser's balance to 0 in DB
                    dbManager.updateUserBalance(p1_id, 0);

                    // Update the current user's object in memory for correct display
                    if(user.getUserId() == p2_id) { user.setAuraBalance(winnerNewBalance); }
                    if(user.getUserId() == p1_id) { user.setAuraBalance(0); }

                    System.out.println("🎉 " + player2.getUsername() + " wins and collects " + p2_score + " Auras!");
                    System.out.println("☠️ " + player1.getUsername() + " has lost all their Auras!");

                } else {
                    // TIE: Both keep earnings, so we update their main balances with their scores
                    int p1NewBalance = player1.getAuraBalance() + p1_score;
                    int p2NewBalance = player2.getAuraBalance() + p2_score;
                    dbManager.updateUserBalance(p1_id, p1NewBalance);
                    dbManager.updateUserBalance(p2_id, p2NewBalance);

                    // Update the current user's object in memory for correct display
                    if(user.getUserId() == p1_id) { user.setAuraBalance(p1NewBalance); }
                    if(user.getUserId() == p2_id) { user.setAuraBalance(p2NewBalance); }

                    System.out.println("🤝 It's a TIE! Both players keep their earnings.");
                }
                // --- MODIFICATION END ---
            }
        } else { // Player 2 is the first to play.
            newStatus = "PLAYER1_TURN";
            dbManager.updateArenaPlayerScoreAndStatus(match.getMatchId(), user.getUserId(), score, newStatus);
            System.out.println("Waiting for " + match.getPlayer1Username() + " to complete their turn.");
        }

        user.setArenaScore(0);
        user.setArenaTurnStartTime(0);
        user.setArenaMatchId(-1);
    }

    public void disbandArenaChallenge(User user) {
        Scanner scanner = new Scanner(System.in);
        ArenaMatch match = dbManager.findArenaMatchForUser(user.getUserId());

        if (match == null) {
            System.out.println("❌ You are not in an active arena match to disband.");
            return;
        }

        long inactiveDurationSeconds = Duration.between(match.getLastActiveAt().toInstant(), Instant.now()).getSeconds();
        long disbandThresholdSeconds = 10 * 60;

        boolean canForceDisband = false;
        if (match.getStatus() == ArenaMatch.MatchStatus.PENDING && inactiveDurationSeconds >= disbandThresholdSeconds) {
            canForceDisband = true;
        } else if (match.getStatus() == ArenaMatch.MatchStatus.PLAYER1_TURN && match.getPlayer2Id() == user.getUserId() && inactiveDurationSeconds >= disbandThresholdSeconds) {
            canForceDisband = true;
        } else if (match.getStatus() == ArenaMatch.MatchStatus.PLAYER2_TURN && match.getPlayer1Id() == user.getUserId() && inactiveDurationSeconds >= disbandThresholdSeconds) {
            canForceDisband = true;
        }

        if (!canForceDisband) {
            System.out.println("❌ This match cannot be disbanded yet. It is either active or has not met the inactivity threshold (10 minutes).");
            System.out.println("Current status: " + match.getStatus() + ". Inactive for: " + (inactiveDurationSeconds / 60) + " minutes.");
            return;
        }

        System.out.print("Are you sure you want to disband Match ID " + match.getMatchId() + "? (y/n): ");
        String confirmation = scanner.nextLine().toLowerCase();

        if (confirmation.equals("y")) {
            if (dbManager.disbandArenaMatch(match.getMatchId())) {
                System.out.println("✅ Match ID " + match.getMatchId() + " has been disbanded.");
                dbManager.deleteArenaMatch(match.getMatchId());
            } else {
                System.out.println("❌ Failed to disband match.");
            }
        } else {
            System.out.println("Disbanding cancelled.");
        }
    }
}