// File: aurafarmer_final/src/main/java/com/aurafarmer/model/Item.java

package com.aurafarmer.model;

public class Item {
    private int id;
    private String name;
    private String description;
    private Integer buyPrice; // Nullable
    private Integer sellPrice; // Nullable
    private Integer usesPerItem; // NEW: How many uses a new tool has
    private Integer maxQuantity; // NEW: Max quantity a user can hold

    // Updated constructor
    public Item(int id, String name, String description, Integer buyPrice, Integer sellPrice, Integer usesPerItem, Integer maxQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.usesPerItem = usesPerItem;
        this.maxQuantity = maxQuantity;
    }

    // Original constructor (keep for backward compatibility if needed elsewhere, but new one is primary)
    public Item(int id, String name, String description, Integer buyPrice, Integer sellPrice) {
        this(id, name, description, buyPrice, sellPrice, null, null); // Default to null for new fields
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getBuyPrice() { return buyPrice; }
    public Integer getSellPrice() { return sellPrice; }
    public Integer getUsesPerItem() { return usesPerItem; } // NEW Getter
    public Integer getMaxQuantity() { return maxQuantity; } // NEW Getter

    // Setters (if needed, but typically items are immutable once loaded)
    public void setUsesPerItem(Integer usesPerItem) { this.usesPerItem = usesPerItem; } // NEW Setter
    public void setMaxQuantity(Integer maxQuantity) { this.maxQuantity = maxQuantity; } // NEW Setter
}