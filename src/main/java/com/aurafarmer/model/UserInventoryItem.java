// File: aurafarmer_final/src/main/java/com/aurafarmer/model/UserInventoryItem.java

package com.aurafarmer.model;

public class UserInventoryItem {
    private Item item;
    private int quantity;
    private Integer usesLeft; // NEW: How many uses are left for this specific item instance

    // Updated constructor
    public UserInventoryItem(Item item, int quantity, Integer usesLeft) {
        this.item = item;
        this.quantity = quantity;
        this.usesLeft = usesLeft;
    }

    // Original constructor (keep for backward compatibility if needed elsewhere)
    public UserInventoryItem(Item item, int quantity) {
        this(item, quantity, null); // Default usesLeft to null
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public Integer getUsesLeft() { // NEW Getter
        return usesLeft;
    }

    public void setUsesLeft(Integer usesLeft) { // NEW Setter
        this.usesLeft = usesLeft;
    }
}