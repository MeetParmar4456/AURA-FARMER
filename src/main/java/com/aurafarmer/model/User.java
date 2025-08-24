// File: aurafarmer_final/src/main/java/com/aurafarmer/model/User.java

package com.aurafarmer.model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class User {
    private int userId;
    private String username;
    private String passwordHash; // Not loaded into User object for security, but exists in DB
    private int auraBalance;
    private int totalShiftsWorked;
    private boolean passiveMode;
    private Timestamp createdAt;
    private Map<Integer, UserInventoryItem> inventory;
    private UserJob currentJob;


    private boolean inArenaTurn;
    private int arenaInitialBalance;
    private long arenaTurnStartTime; // To track remaining time
    private int arenaMatchId; // To link user to their active match during turn
    private int arenaScore;

    public User(int userId, String username, int auraBalance, int totalShiftsWorked, boolean passiveMode) {
        this.userId = userId;
        this.username = username;
        this.auraBalance = auraBalance;
        this.totalShiftsWorked = totalShiftsWorked;
        this.passiveMode = passiveMode;
        this.inventory = new HashMap<>(); // Initialize inventory

        this.inArenaTurn = false;
        this.arenaInitialBalance = 0;
        this.arenaTurnStartTime = 0;
        this.arenaMatchId = -1;
        this.arenaScore = 0;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getAuraBalance() { return auraBalance; }
    public int getTotalShiftsWorked() { return totalShiftsWorked; }
    public boolean isPassiveMode() { return passiveMode; }
    public Map<Integer, UserInventoryItem> getInventory() { return inventory; }
    public UserJob getCurrentJob() { return currentJob; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setAuraBalance(int auraBalance) { this.auraBalance = auraBalance; }
    public void setTotalShiftsWorked(int totalShiftsWorked) { this.totalShiftsWorked = totalShiftsWorked; }
    public void setPassiveMode(boolean passiveMode) { this.passiveMode = passiveMode; }
    public void setCurrentJob(UserJob currentJob) { this.currentJob = currentJob; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }


    public boolean isInArenaTurn() { return inArenaTurn; }
    public void setInArenaTurn(boolean inArenaTurn) { this.inArenaTurn = inArenaTurn; }
    public int getArenaInitialBalance() { return arenaInitialBalance; }
    public void setArenaInitialBalance(int arenaInitialBalance) { this.arenaInitialBalance = arenaInitialBalance; }
    public long getArenaTurnStartTime() { return arenaTurnStartTime; }
    public void setArenaTurnStartTime(long arenaTurnStartTime) { this.arenaTurnStartTime = arenaTurnStartTime; }
    public int getArenaMatchId() { return arenaMatchId; }
    public void setArenaMatchId(int arenaMatchId) { this.arenaMatchId = arenaMatchId; }


    public int getArenaScore() { return arenaScore; }
    public void setArenaScore(int arenaScore) { this.arenaScore = arenaScore; }
    public void addArenaScore(int amount) { this.arenaScore += amount; }
}