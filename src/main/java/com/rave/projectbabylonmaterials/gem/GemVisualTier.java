package com.rave.projectbabylonmaterials.gem;

import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;

public enum GemVisualTier {
    BROKEN(0.0F),
    NORMAL(0.1F),
    SHINY(0.2F);

    private final float modelPredicate;

    GemVisualTier(float modelPredicate) {
        this.modelPredicate = modelPredicate;
    }

    public float getModelPredicate() {
        return modelPredicate;
    }

    public static GemVisualTier fromRarity(ItemRarityTier rarity) {
        return switch (rarity) {
            case COMMON, UNCOMMON -> BROKEN;
            case RARE, EPIC -> NORMAL;
            case LEGENDARY -> SHINY;
        };
    }
}