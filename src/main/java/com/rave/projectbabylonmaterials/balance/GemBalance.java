package com.rave.projectbabylonmaterials.balance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;

/**
 * Datapack-driven per-rarity effect values for a single {@link com.rave.projectbabylonmaterials.gem.GemType}.
 * The five values map to the rarity tiers and feed the bonus calculation in the gem effect handler.
 */
public record GemBalance(double common, double uncommon, double rare, double epic, double legendary) {
    public static final Codec<GemBalance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("common").forGetter(GemBalance::common),
            Codec.DOUBLE.fieldOf("uncommon").forGetter(GemBalance::uncommon),
            Codec.DOUBLE.fieldOf("rare").forGetter(GemBalance::rare),
            Codec.DOUBLE.fieldOf("epic").forGetter(GemBalance::epic),
            Codec.DOUBLE.fieldOf("legendary").forGetter(GemBalance::legendary)
    ).apply(instance, GemBalance::new));

    public double value(ItemRarityTier rarity) {
        return switch (rarity) {
            case COMMON -> common;
            case UNCOMMON -> uncommon;
            case RARE -> rare;
            case EPIC -> epic;
            case LEGENDARY -> legendary;
        };
    }
}
