package com.aurafarmer.model;

import java.sql.Timestamp;

// A simple data object to hold information for a single arena result entry.
public class ArenaResultEntry {
    public final String opponentUsername;
    public final int yourScore;
    public final int opponentScore;
    public final String result; // "Win", "Loss", or "Tie"
    public final Timestamp matchDate;

    public ArenaResultEntry(String opponentUsername, int yourScore, int opponentScore, String result, Timestamp matchDate) {
        this.opponentUsername = opponentUsername;
        this.yourScore = yourScore;
        this.opponentScore = opponentScore;
        this.result = result;
        this.matchDate = matchDate;
    }
}