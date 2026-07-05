package com.rave.projectbabylonmaterials.balance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.Optional;

/**
 * Datapack-driven balance values for a single {@link com.rave.projectbabylonmaterials.rarity.ItemRarityTier}.
 *
 * <p>Each field describes the tier this entry is named after ("T"):
 * <ul>
 *     <li>slot rolls ({@code rollWeight}, enchant/gem slot base + bonus chance) are applied when an item becomes T;</li>
 *     <li>{@code material} / {@code reforgeMaterialCount} / {@code requiredDust} / {@code requiredXp} are the cost to upgrade INTO T;</li>
 *     <li>{@code successChance} / {@code maxUpgradeAttempts} apply when upgrading or reforging FROM T;</li>
 *     <li>{@code socketXp} / {@code extractionXp} apply to a gem whose own rarity is T.</li>
 * </ul>
 *
 * <p>Datapack registry entries are full-object replacements (not field merges), the same as vanilla
 * datapack registries: to change a value, copy the shipped default file for that tier and edit it. Fields
 * left out of a present entry take the neutral {@code optionalFieldOf} defaults below (mostly {@code 0}),
 * <em>not</em> the original hardcoded values. Only when an entire tier entry is absent do the gem/station
 * helpers fall back to their hardcoded values.
 */
public record RarityBalance(
        int rollWeight,
        int baseEnchantSlots,
        int enchantBonusChance,
        int baseGemSlots,
        int gemSlotBonusChance,
        Optional<Item> material,
        int reforgeMaterialCount,
        int requiredDust,
        int requiredXp,
        int successChance,
        int socketXp,
        int extractionXp,
        int maxUpgradeAttempts
) {
    public static final Codec<RarityBalance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("roll_weight", 0).forGetter(RarityBalance::rollWeight),
            Codec.INT.optionalFieldOf("base_enchant_slots", 0).forGetter(RarityBalance::baseEnchantSlots),
            Codec.INT.optionalFieldOf("enchant_bonus_chance", 0).forGetter(RarityBalance::enchantBonusChance),
            Codec.INT.optionalFieldOf("base_gem_slots", 0).forGetter(RarityBalance::baseGemSlots),
            Codec.INT.optionalFieldOf("gem_slot_bonus_chance", 0).forGetter(RarityBalance::gemSlotBonusChance),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("material").forGetter(RarityBalance::material),
            Codec.INT.optionalFieldOf("reforge_material_count", 2).forGetter(RarityBalance::reforgeMaterialCount),
            Codec.INT.optionalFieldOf("required_dust", 0).forGetter(RarityBalance::requiredDust),
            Codec.INT.optionalFieldOf("required_xp", 0).forGetter(RarityBalance::requiredXp),
            Codec.INT.optionalFieldOf("success_chance", 0).forGetter(RarityBalance::successChance),
            Codec.INT.optionalFieldOf("socket_xp", 0).forGetter(RarityBalance::socketXp),
            Codec.INT.optionalFieldOf("extraction_xp", 0).forGetter(RarityBalance::extractionXp),
            Codec.INT.optionalFieldOf("max_upgrade_attempts", 0).forGetter(RarityBalance::maxUpgradeAttempts)
    ).apply(instance, RarityBalance::new));
}
