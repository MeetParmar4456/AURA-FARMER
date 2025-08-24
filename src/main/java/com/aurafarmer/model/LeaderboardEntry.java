package com.aurafarmer.model;

import java.sql.Timestamp;

// A simple data object to hold information for a single leaderboard entry.
public class LeaderboardEntry {
    public final String username;
    public final int auraBalance;
    public final Timestamp createdAt;

    public LeaderboardEntry(String username, int auraBalance, Timestamp createdAt) {
        this.username = username;
        this.auraBalance = auraBalance;
        this.createdAt = createdAt;
    }
}