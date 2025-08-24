// File: aurafarmer_final/src/main/java/com/aurafarmer/model/ArenaMatch.java

package com.aurafarmer.model;

import java.sql.Timestamp;

public class ArenaMatch {

    public enum MatchStatus {
        PENDING,        // Match created, waiting for Player 2 to join
        PLAYER1_TURN,   // Player 1 has joined/created, Player 2 has played, waiting for Player 1 to play
        PLAYER2_TURN,   // Player 1 has created, Player 2 has joined, Player 2 needs to play
        COMPLETED,      // Both players have played, results are ready
        DISBANDED       // Match was disbanded due to inactivity or explicit action
    }

    private int matchId;
    private int player1Id;
    private String player1Username;
    private Integer player2Id; // Use Integer for nullable foreign key
    private String player2Username; // Can be null
    private Integer player1Score; // Use Integer for nullable score
    private Integer player2Score; // Use Integer for nullable score
    private MatchStatus status;
    private String challengeType;
    private int timeLimitMinutes;
    private Timestamp createdAt;
    private Timestamp lastActiveAt;
    private Integer winnerId; // Use Integer for nullable foreign key

    public ArenaMatch(int matchId, int player1Id, String player1Username, Integer player2Id, String player2Username,
                      Integer player1Score, Integer player2Score, MatchStatus status, String challengeType,
                      int timeLimitMinutes, Timestamp createdAt, Timestamp lastActiveAt, Integer winnerId) {
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player1Username = player1Username;
        this.player2Id = player2Id;
        this.player2Username = player2Username;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.status = status;
        this.challengeType = challengeType;
        this.timeLimitMinutes = timeLimitMinutes;
        this.createdAt = createdAt;
        this.lastActiveAt = lastActiveAt;
        this.winnerId = winnerId;
    }

    // Getters
    public int getMatchId() { return matchId; }
    public int getPlayer1Id() { return player1Id; }
    public String getPlayer1Username() { return player1Username; }
    public Integer getPlayer2Id() { return player2Id; }
    public String getPlayer2Username() { return player2Username; }
    public Integer getPlayer1Score() { return player1Score; }
    public Integer getPlayer2Score() { return player2Score; }
    public MatchStatus getStatus() { return status; }
    public String getChallengeType() { return challengeType; }
    public int getTimeLimitMinutes() { return timeLimitMinutes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getLastActiveAt() { return lastActiveAt; }
    public Integer getWinnerId() { return winnerId; }

    // Setters (for updating state in memory before DB sync if needed, or just for convenience)
    public void setPlayer2Id(Integer player2Id) { this.player2Id = player2Id; }
    public void setPlayer2Username(String player2Username) { this.player2Username = player2Username; }
    public void setPlayer1Score(Integer player1Score) { this.player1Score = player1Score; }
    public void setPlayer2Score(Integer player2Score) { this.player2Score = player2Score; }
    public void setStatus(MatchStatus status) { this.status = status; }
    public void setLastActiveAt(Timestamp lastActiveAt) { this.lastActiveAt = lastActiveAt; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }

    // Helper to check if a user is part of this match
    public boolean isUserInMatch(int userId) {
        return this.player1Id == userId || (this.player2Id != null && this.player2Id == userId);
    }

    // Helper to get the opponent's username
    public String getOpponentUsername(int userId) {
        if (this.player1Id == userId && this.player2Username != null) {
            return this.player2Username;
        } else if (this.player2Id != null && this.player2Id == userId && this.player1Username != null) {
            return this.player1Username;
        }
        return "N/A";
    }

    // Helper to get the opponent's ID
    public Integer getOpponentId(int userId) {
        if (this.player1Id == userId && this.player2Id != null) {
            return this.player2Id;
        } else if (this.player2Id != null && this.player2Id == userId) {
            return this.player1Id;
        }
        return null;
    }
}