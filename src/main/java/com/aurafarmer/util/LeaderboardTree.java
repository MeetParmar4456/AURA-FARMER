package com.aurafarmer.util;

import com.aurafarmer.model.LeaderboardEntry;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardTree {

    // Inner class representing a single node in the tree
    private static class Node {
        LeaderboardEntry entry;
        Node left;
        Node right;

        Node(LeaderboardEntry entry) {
            this.entry = entry;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;

    public LeaderboardTree() {
        this.root = null;
    }

    // Public method to start the insertion process
    public void insert(LeaderboardEntry entry) {
        root = insertRecursive(root, entry);
    }

    // Recursive helper method for insertion
    private Node insertRecursive(Node current, LeaderboardEntry entry) {
        if (current == null) {
            return new Node(entry);
        }

        // --- Sorting Logic ---
        // 1. Higher aura balance goes to the right (for descending order)
        if (entry.auraBalance > current.entry.auraBalance) {
            current.right = insertRecursive(current.right, entry);
        }
        // 2. Lower aura balance goes to the left
        else if (entry.auraBalance < current.entry.auraBalance) {
            current.left = insertRecursive(current.left, entry);
        }
        // 3. Tie-breaker: If balances are equal, compare creation date
        else {
            // Earlier date (older account) is "greater" and goes to the right
            if (entry.createdAt.before(current.entry.createdAt)) {
                current.right = insertRecursive(current.right, entry);
            } else {
                current.left = insertRecursive(current.left, entry);
            }
        }
        return current;
    }

    // Public method to get the sorted list of entries
    public List<LeaderboardEntry> getSortedEntries() {
        List<LeaderboardEntry> sortedList = new ArrayList<>();
        // Perform a reverse in-order traversal to get descending order
        traverseReverseInOrder(root, sortedList);
        return sortedList;
    }

    // Recursive helper for traversal
    private void traverseReverseInOrder(Node node, List<LeaderboardEntry> list) {
        if (node != null) {
            traverseReverseInOrder(node.right, list); // Visit right subtree first
            list.add(node.entry);                     // Visit node
            traverseReverseInOrder(node.left, list);  // Visit left subtree
        }
    }
}