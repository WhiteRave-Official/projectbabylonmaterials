package com.rave.projectbabylonmaterials.rarity;

import net.minecraft.ChatFormatting;

public enum ItemRarityTier {
    COMMON("common", ChatFormatting.WHITE, 45, 0, 0),
    UNCOMMON("uncommon", ChatFormatting.GREEN, 30, 1, 0),
    RARE("rare", ChatFormatting.BLUE, 15, 1, 0),
    EPIC("epic", ChatFormatting.DARK_PURPLE, 7, 2, 1),
    LEGENDARY("legendary", ChatFormatting.GOLD, 3, 3, 2);

    private final String id;
    private final ChatFormatting color;
    private final int rollWeight;
    private final int baseEnchantSlots;
    private final int baseGemSlots;

    ItemRarityTier(String id, ChatFormatting color, int rollWeight, int baseEnchantSlots, int baseGemSlots) {
        this.id = id;
        this.color = color;
        this.rollWeight = rollWeight;
        this.baseEnchantSlots = baseEnchantSlots;
        this.baseGemSlots = baseGemSlots;
    }

    public String getId() {
        return id;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public int getRollWeight() {
        return rollWeight;
    }

    public int getBaseEnchantSlots() {
        return baseEnchantSlots;
    }

    public int getBaseGemSlots() {
        return baseGemSlots;
    }

    public static ItemRarityTier byId(String id) {
        for (ItemRarityTier tier : values()) {
            if (tier.id.equals(id)) {
                return tier;
            }
        }
        return COMMON;
    }
}