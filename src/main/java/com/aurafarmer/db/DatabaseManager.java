package com.aurafarmer.db;

import com.aurafarmer.model.*;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseManager {



    // server
    private static final String DB_HOST = "crossover.proxy.rlwy.net";
    private static final String DB_PORT = "30031";
    private static final String DB_NAME = "railway";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "HTgEdysmaxVbPJMakkTJlcbvxrdBAaGm"; // IMPORTANT: Replace with your secure password


    // local
//    private static final String DB_HOST = "localhost"; // Or "127.0.0.1"
//    private static final String DB_PORT = "3306"; // Default MySQL port
//    private static final String DB_NAME = "aurafarmer_local"; // Or whatever you named your local DB
//    private static final String DB_USER = "root"; // Your local MySQL username
//    private static final String DB_PASSWORD = ""; // Your local MySQL password (often empty or 'root')


    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String urlWithOptions = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true";
                connection = DriverManager.getConnection(urlWithOptions, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("Error: MySQL JDBC Driver not found!");
                e.printStackTrace();
            }
        }
        return connection;
    }
    // ... (The rest of the file remains unchanged, you can keep your existing code for the other methods)
    public List<String> getAllPlayerNames() {
        List<String> players = new ArrayList<>();
        String sql = "SELECT username FROM users ORDER BY username ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                players.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public List<LeaderboardEntry> getLeaderboardEntries() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        String sql = "SELECT username, aura_balance, created_at FROM users";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                entries.add(new LeaderboardEntry(
                        rs.getString("username"),
                        rs.getInt("aura_balance"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public User validateLogin(String username, String password) {
        User user = null;
        String sql = "{CALL get_user_by_username(?)}";
        String getHashSql = "SELECT SHA2(?, 256)";

        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             PreparedStatement hashStmt = conn.prepareStatement(getHashSql)) {

            cstmt.setString(1, username);
            ResultSet rs = cstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                int userId = rs.getInt("user_id");
                String dbUsername = rs.getString("username");
                int auraBalance = rs.getInt("aura_balance");
                int totalShifts = rs.getInt("total_shifts_worked");
                boolean passiveMode = rs.getBoolean("passive_mode");

                hashStmt.setString(1, password);
                ResultSet hashRs = hashStmt.executeQuery();
                String attemptHash = "";
                if (hashRs.next()) { attemptHash = hashRs.getString(1); }

                if (storedHash != null && storedHash.equals(attemptHash)) {
                    user = new User(userId, dbUsername, auraBalance, totalShifts, passiveMode);
                    loadUserInventory(user);
                    loadUserJob(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    private void loadUserJob(User user) {
        String sql = "SELECT j.job_id, j.job_name, j.salary_per_shift, j.required_shifts, j.cooldown_minutes, uj.shifts_worked " +
                "FROM user_jobs uj " +
                "JOIN jobs j ON uj.job_id = j.job_id " +
                "WHERE uj.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Job job = new Job(
                        rs.getInt("job_id"),
                        rs.getString("job_name"),
                        rs.getInt("salary_per_shift"),
                        rs.getInt("required_shifts"),
                        rs.getInt("cooldown_minutes")
                );
                UserJob userJob = new UserJob(job, rs.getInt("shifts_worked"));
                user.setCurrentJob(userJob);
            }
        } catch (SQLException e) {
            System.err.println("Error loading user job.");
            e.printStackTrace();
        }
    }

    public List<Job> getAvailableJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT job_id, job_name, salary_per_shift, required_shifts, cooldown_minutes FROM jobs";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                jobs.add(new Job(
                        rs.getInt("job_id"),
                        rs.getString("job_name"),
                        rs.getInt("salary_per_shift"),
                        rs.getInt("required_shifts"),
                        rs.getInt("cooldown_minutes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    public void incrementUserShifts(int userId) {
        String updateUserJobSql = "UPDATE user_jobs SET shifts_worked = shifts_worked + 1 WHERE user_id = ?";
        String updateUserSql = "UPDATE users SET total_shifts_worked = total_shifts_worked + 1 WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(updateUserJobSql)) {
                pstmt1.setInt(1, userId);
                pstmt1.executeUpdate();
            }
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateUserSql)) {
                pstmt2.setInt(1, userId);
                pstmt2.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            System.err.println("Transaction failed. Rolling back shift increment.");
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUserJob(int userId, int jobId) {
        String sql = "{CALL set_user_job(?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, userId);
            cstmt.setInt(2, jobId);
            cstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resignJob(int userId) {
        String sql = "DELETE FROM user_jobs WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUserInventory(User user) {
        String sql = "SELECT i.item_id, i.item_name, i.description, i.buy_price, i.sell_price, i.uses_per_item, i.max_quantity, ui.quantity, ui.uses_left " +
                "FROM user_inventory ui " +
                "JOIN items i ON ui.item_id = i.item_id " +
                "WHERE ui.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            Map<Integer, UserInventoryItem> inventory = user.getInventory();

            while(rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("item_name");
                String description = rs.getString("description");
                // Use getInt() for integer columns, check for SQL NULL
                Integer buyPrice = rs.getObject("buy_price", Integer.class);
                Integer sellPrice = rs.getObject("sell_price", Integer.class);
                Integer usesPerItem = rs.getObject("uses_per_item", Integer.class);
                Integer maxQuantity = rs.getObject("max_quantity", Integer.class);
                int quantity = rs.getInt("quantity");
                Integer usesLeft = rs.getObject("uses_left", Integer.class);

                Item item = new Item(itemId, itemName, description, buyPrice, sellPrice, usesPerItem, maxQuantity);
                UserInventoryItem inventoryItem = new UserInventoryItem(item, quantity, usesLeft);
                inventory.put(itemId, inventoryItem);
            }
        } catch (SQLException e) {
            System.err.println("Error loading user inventory.");
            e.printStackTrace();
        }
    }

    public List<Item> getShopItems() {
        List<Item> shopItems = new ArrayList<>();
        String sql = "SELECT item_id, item_name, description, buy_price, sell_price, uses_per_item, max_quantity FROM items WHERE buy_price IS NOT NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("item_name");
                String description = rs.getString("description");
                // Use getInt() for integer columns, check for SQL NULL
                Integer buyPrice = rs.getObject("buy_price", Integer.class);
                Integer sellPrice = rs.getObject("sell_price", Integer.class);
                Integer usesPerItem = rs.getObject("uses_per_item", Integer.class);
                Integer maxQuantity = rs.getObject("max_quantity", Integer.class);

                shopItems.add(new Item(itemId, itemName, description, buyPrice, sellPrice, usesPerItem, maxQuantity));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shop items.");
            e.printStackTrace();
        }
        return shopItems;
    }

    public Item getItemById(int itemId) {
        String sql = "SELECT item_id, item_name, description, buy_price, sell_price, uses_per_item, max_quantity FROM items WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String itemName = rs.getString("item_name");
                String description = rs.getString("description");
                // Use getInt() for integer columns, check for SQL NULL
                Integer buyPrice = rs.getObject("buy_price", Integer.class);
                Integer sellPrice = rs.getObject("sell_price", Integer.class);
                Integer usesPerItem = rs.getObject("uses_per_item", Integer.class);
                Integer maxQuantity = rs.getObject("max_quantity", Integer.class);
                return new Item(itemId, itemName, description, buyPrice, sellPrice, usesPerItem, maxQuantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeItemQuantity(int userId, int itemId, int quantityToRemove) {
        String updateSql = "UPDATE user_inventory SET quantity = quantity - ? WHERE user_id = ? AND item_id = ?";
        String deleteSql = "DELETE FROM user_inventory WHERE user_id = ? AND item_id = ?";

        try (Connection conn = getConnection()) {
            // First, update the quantity
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, quantityToRemove);
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, itemId);
                updateStmt.executeUpdate();
            }

            // Then, check if quantity is zero and delete if necessary
            try (PreparedStatement checkAndDeleteStmt = conn.prepareStatement("DELETE FROM user_inventory WHERE user_id = ? AND item_id = ? AND quantity <= 0")) {
                checkAndDeleteStmt.setInt(1, userId);
                checkAndDeleteStmt.setInt(2, itemId);
                checkAndDeleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error removing item quantity from user inventory.");
            e.printStackTrace();
        }
    }

    /**
     * Adds a specified quantity of an item to user inventory.
     * If the item is a tool, its uses_left is set upon initial insertion.
     * If the item already exists, only quantity is incremented.
     * @param userId The ID of the user.
     * @param itemId The ID of the item to add.
     * @param quantityChange The amount to add.
     * @param initialUsesForTool The initial uses if this is a new tool being added. Null for non-tools or existing items.
     */
    public void addItemToUserInventory(int userId, int itemId, int quantityChange, Integer initialUsesForTool) {
        String sql;
        // If initialUsesForTool is provided, it means we are adding a NEW tool instance
        // or replacing a broken one, so we insert/update uses_left.
        if (initialUsesForTool != null) {
            sql = "INSERT INTO user_inventory (user_id, item_id, quantity, uses_left) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity), uses_left = VALUES(uses_left)";
        } else {
            // For non-tools or existing items where uses_left shouldn't be touched, just update quantity.
            sql = "INSERT INTO user_inventory (user_id, item_id, quantity) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, quantityChange);

            if (initialUsesForTool != null) {
                pstmt.setInt(4, initialUsesForTool);
            }
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding item to user inventory in DB.");
            e.printStackTrace();
        }
    }

    /**
     * Updates the uses_left for a specific item in a user's inventory.
     * @param userId The ID of the user.
     * @param itemId The ID of the item whose uses to update.
     * @param newUsesLeft The new value for uses_left.
     */
    public void updateItemUsesLeft(int userId, int itemId, int newUsesLeft) {
        String sql = "UPDATE user_inventory SET uses_left = ? WHERE user_id = ? AND item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newUsesLeft);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating item uses left in DB.");
            e.printStackTrace();
        }
    }


    public boolean registerUser(String username, String password) {
        String sql = "{CALL register_user(?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, username);
            cstmt.setString(2, password);
            cstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Error: Username '" + username + "' is already taken.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void updateUserBalance(int userId, int newBalance) {
        String sql = "UPDATE users SET aura_balance = ? WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newBalance);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCooldown(int userId, String commandName, int durationSeconds) {
        String sql = "INSERT INTO command_cooldowns (user_id, command_name, expires_at) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE expires_at = VALUES(expires_at)";
        Timestamp expiresAt = Timestamp.from(Instant.now().plusSeconds(durationSeconds));
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, commandName);
            pstmt.setTimestamp(3, expiresAt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getCooldownSecondsLeft(int userId, String commandName) {
        String sql = "SELECT expires_at FROM command_cooldowns WHERE user_id = ? AND command_name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, commandName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Timestamp expiresAt = rs.getTimestamp("expires_at");
                long secondsLeft = (expiresAt.getTime() - System.currentTimeMillis()) / 1000;
                // Delete the row if the cooldown has expired
                if (secondsLeft <= 0) {
                    deleteCooldown(userId, commandName);
                    return 0;
                }
                return Math.max(0, secondsLeft);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteCooldown(int userId, String commandName) {
        String sql = "DELETE FROM command_cooldowns WHERE user_id = ? AND command_name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, commandName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean performRobbery(int robberId, int victimId, int amountStolen, int fine) {
        String updateRobberSql = "UPDATE users SET aura_balance = aura_balance + ? WHERE user_id = ?";
        String updateVictimSql = "UPDATE users SET aura_balance = aura_balance - ? WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(updateRobberSql)) {
                pstmt.setInt(1, amountStolen);
                pstmt.setInt(2, robberId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(updateVictimSql)) {
                pstmt.setInt(1, amountStolen);
                pstmt.setInt(2, victimId);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Robbery transaction failed. Rolling back.");
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public User getUserByUsername(String username) {
        String sql = "{CALL get_user_by_username(?)}";
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, username);
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getInt("aura_balance"),
                        rs.getInt("total_shifts_worked"),
                        rs.getBoolean("passive_mode")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, aura_balance, total_shifts_worked, passive_mode FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getInt("aura_balance"),
                        rs.getInt("total_shifts_worked"),
                        rs.getBoolean("passive_mode")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePassiveMode(int userId, boolean status) {
        String sql = "UPDATE users SET passive_mode = ? WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, status);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- NEW ARENA METHODS ---

    /**
     * Creates a new arena match in a PENDING state.
     * @param player1Id The ID of the player initiating the match.
     * @param challengeType The type of challenge (e.g., "AuraRush").
     * @param timeLimitMinutes The time limit for the challenge in minutes.
     * @return The match_id of the created match, or -1 if creation failed.
     */
    public int createArenaMatch(int player1Id, String challengeType, int timeLimitMinutes) {
        String sql = "INSERT INTO arena_matches (player1_id, status, challenge_type, time_limit_minutes, created_at, last_active_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, player1Id);
            pstmt.setString(2, "PENDING");
            pstmt.setString(3, challengeType);
            pstmt.setInt(4, timeLimitMinutes);
            Timestamp now = Timestamp.from(Instant.now());
            pstmt.setTimestamp(5, now);
            pstmt.setTimestamp(6, now);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating arena match: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Finds an active arena match for a given user, either as player1 or player2.
     * @param userId The ID of the user to find a match for.
     * @return An ArenaMatch object if found, null otherwise.
     */
    public ArenaMatch findArenaMatchForUser(int userId) {
        String sql = "SELECT am.*, u1.username AS player1_username, u2.username AS player2_username " +
                "FROM arena_matches am " +
                "JOIN users u1 ON am.player1_id = u1.user_id " +
                "LEFT JOIN users u2 ON am.player2_id = u2.user_id " +
                "WHERE (am.player1_id = ? OR am.player2_id = ?) AND am.status != 'COMPLETED' AND am.status != 'DISBANDED'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArenaMatch(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding arena match for user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // --- NEW METHOD ---
    public List<ArenaMatch> findCompletedArenaMatchesForUser(int userId) {
        List<ArenaMatch> completedMatches = new ArrayList<>();
        String sql = "SELECT am.*, u1.username AS player1_username, u2.username AS player2_username " +
                "FROM arena_matches am " +
                "JOIN users u1 ON am.player1_id = u1.user_id " +
                "LEFT JOIN users u2 ON am.player2_id = u2.user_id " +
                "WHERE (am.player1_id = ? OR am.player2_id = ?) AND am.status = 'COMPLETED'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    completedMatches.add(mapResultSetToArenaMatch(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding completed arena matches for user: " + e.getMessage());
            e.printStackTrace();
        }
        return completedMatches;
    }


    /**
     * Finds a pending arena match (where player2 is NULL).
     * @return A list of pending ArenaMatch objects.
     */
    public List<ArenaMatch> findPendingArenaMatches() {
        List<ArenaMatch> pendingMatches = new ArrayList<>();
        String sql = "SELECT am.*, u1.username AS player1_username, NULL AS player2_username " + // player2_username is null for pending
                "FROM arena_matches am " +
                "JOIN users u1 ON am.player1_id = u1.user_id " +
                "WHERE am.player2_id IS NULL AND am.status = 'PENDING'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pendingMatches.add(mapResultSetToArenaMatch(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding pending arena matches: " + e.getMessage());
            e.printStackTrace();
        }
        return pendingMatches;
    }

    /**
     * Joins a player to an existing pending arena match.
     * @param matchId The ID of the match to join.
     * @param player2Id The ID of the player joining.
     * @return true if successful, false otherwise.
     */
    public boolean joinArenaMatch(int matchId, int player2Id) {
        String sql = "UPDATE arena_matches SET player2_id = ?, status = 'PLAYER2_TURN', last_active_at = ? WHERE match_id = ? AND player2_id IS NULL AND status = 'PENDING'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, player2Id);
            pstmt.setTimestamp(2, Timestamp.from(Instant.now()));
            pstmt.setInt(3, matchId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error joining arena match: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates a player's score and the match status.
     * @param matchId The ID of the match.
     * @param playerId The ID of the player whose score is being updated.
     * @param score The score achieved by the player.
     * @param newStatus The new status of the match after this player's turn.
     * @return true if successful, false otherwise.
     */
    public boolean updateArenaPlayerScoreAndStatus(int matchId, int playerId, int score, String newStatus) {
        String sql;
        if (newStatus.equals("PLAYER1_TURN")) { // Player2 just played
            sql = "UPDATE arena_matches SET player2_score = ?, status = ?, last_active_at = ? WHERE match_id = ? AND player2_id = ?";
        } else if (newStatus.equals("COMPLETED")) { // Player1 just played
            sql = "UPDATE arena_matches SET player1_score = ?, status = ?, last_active_at = ? WHERE match_id = ? AND player1_id = ?";
        } else {
            return false; // Invalid status update for score
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, score);
            pstmt.setString(2, newStatus);
            pstmt.setTimestamp(3, Timestamp.from(Instant.now()));
            pstmt.setInt(4, matchId);
            pstmt.setInt(5, playerId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating arena player score and status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the winner of a completed match.
     * @param matchId The ID of the match.
     * @param winnerId The ID of the winning player.
     * @return true if successful, false otherwise.
     */
    public boolean updateArenaMatchWinner(int matchId, int winnerId) {
        String sql = "UPDATE arena_matches SET winner_id = ? WHERE match_id = ? AND status = 'COMPLETED'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, winnerId);
            pstmt.setInt(2, matchId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating arena match winner: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Disbands an arena match.
     * @param matchId The ID of the match to disband.
     * @return true if successful, false otherwise.
     */
    public boolean disbandArenaMatch(int matchId) {
        String sql = "UPDATE arena_matches SET status = 'DISBANDED', last_active_at = ? WHERE match_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.from(Instant.now()));
            pstmt.setInt(2, matchId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error disbanding arena match: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a match from the database (for cleanup after disband or completion).
     * @param matchId The ID of the match to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteArenaMatch(int matchId) {
        String sql = "DELETE FROM arena_matches WHERE match_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, matchId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting arena match: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the last_active_at timestamp for a given arena match.
     * @param matchId The ID of the match to update.
     * @param timestamp The new timestamp.
     * @return true if successful, false otherwise.
     */
    public boolean updateArenaMatchLastActive(int matchId, Timestamp timestamp) {
        String sql = "UPDATE arena_matches SET last_active_at = ? WHERE match_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, timestamp);
            pstmt.setInt(2, matchId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating arena match last active timestamp: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves an ArenaMatch object by its match ID.
     * @param matchId The ID of the match to find.
     * @return An ArenaMatch object if found, null otherwise.
     */
    public ArenaMatch findArenaMatchById(int matchId) {
        String sql = "SELECT am.*, u1.username AS player1_username, u2.username AS player2_username " +
                "FROM arena_matches am " +
                "JOIN users u1 ON am.player1_id = u1.user_id " +
                "LEFT JOIN users u2 ON am.player2_id = u2.user_id " +
                "WHERE am.match_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, matchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArenaMatch(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding arena match by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    // Helper method to map ResultSet to ArenaMatch object
    private ArenaMatch mapResultSetToArenaMatch(ResultSet rs) throws SQLException {
        Integer player2Id = rs.getObject("player2_id") != null ? rs.getInt("player2_id") : null;
        String player2Username = rs.getObject("player2_username") != null ? rs.getString("player2_username") : null;
        Integer player1Score = rs.getObject("player1_score") != null ? rs.getInt("player1_score") : null;
        Integer player2Score = rs.getObject("player2_score") != null ? rs.getInt("player2_score") : null;
        Integer winnerId = rs.getObject("winner_id") != null ? rs.getInt("winner_id") : null;

        return new ArenaMatch(
                rs.getInt("match_id"),
                rs.getInt("player1_id"),
                rs.getString("player1_username"),
                player2Id,
                player2Username,
                player1Score,
                player2Score,
                ArenaMatch.MatchStatus.valueOf(rs.getString("status")),
                rs.getString("challenge_type"),
                rs.getInt("time_limit_minutes"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("last_active_at"),
                winnerId
        );
    }
}