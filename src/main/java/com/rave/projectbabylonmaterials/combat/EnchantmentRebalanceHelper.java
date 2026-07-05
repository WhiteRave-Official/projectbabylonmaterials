package com.rave.projectbabylonmaterials.combat;

import com.rave.projectbabylonmaterials.init.PBMEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EnchantmentRebalanceHelper {
    public static final float SHARPNESS_PER_LEVEL = 0.05F;
    public static final float BANE_PER_LEVEL = 0.12F;
    public static final float SMITE_PER_LEVEL = 0.08F;
    public static final float FIRE_ASPECT_CHANCE_PER_LEVEL = 0.15F;
    public static final float FIRE_ASPECT_CHANCE_CAP = 0.30F;
    public static final int FIRE_ASPECT_SECONDS_PER_LEVEL = 4;
    public static final float PROTECTION_PHYSICAL_REDUCTION_PER_LEVEL = 0.01F;
    public static final float MAGIC_RESISTANCE_REDUCTION_PER_LEVEL = 0.01F;
    public static final float POWER_PER_LEVEL = 0.04F;
    public static final float FLAME_CHANCE = 0.50F;
    public static final float INFINITY_SAVE_CHANCE = 0.30F;
    public static final int MAGIC_RESISTANCE_MAX_LEVEL = 5;

    // MobType was removed in 1.21; reuse vanilla's smite/bane entity-type tags (the same membership the
    // vanilla Smite / Bane of Arthropods enchantments use to gate their damage bonus).
    private static final TagKey<EntityType<?>> SMITE_AFFECTED = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.withDefaultNamespace("sensitive_to_smite"));
    private static final TagKey<EntityType<?>> BANE_AFFECTED = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.withDefaultNamespace("sensitive_to_bane_of_arthropods"));

    private EnchantmentRebalanceHelper() {
    }

    /** Reads an enchantment level from the stack's {@code minecraft:enchantments} component by registry key. */
    public static int levelOf(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack.isEmpty()) {
            return 0;
        }

        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static float getMeleeDamageMultiplier(ItemStack weapon, LivingEntity target) {
        if (weapon.isEmpty() || target == null) {
            return 0.0F;
        }

        int sharpness = levelOf(weapon, Enchantments.SHARPNESS);
        int smite = levelOf(weapon, Enchantments.SMITE);
        int bane = levelOf(weapon, Enchantments.BANE_OF_ARTHROPODS);

        if (target.getType().is(SMITE_AFFECTED) && smite > 0) {
            return smite * SMITE_PER_LEVEL;
        }

        if (target.getType().is(BANE_AFFECTED) && bane > 0) {
            return bane * BANE_PER_LEVEL;
        }

        if (sharpness > 0) {
            return sharpness * SHARPNESS_PER_LEVEL;
        }

        return 0.0F;
    }

    public static float getFireAspectChance(ItemStack weapon) {
        if (weapon.isEmpty()) {
            return 0.0F;
        }

        int level = levelOf(weapon, Enchantments.FIRE_ASPECT);
        return Math.min(level * FIRE_ASPECT_CHANCE_PER_LEVEL, FIRE_ASPECT_CHANCE_CAP);
    }

    public static int getFireAspectDurationSeconds(ItemStack weapon) {
        if (weapon.isEmpty()) {
            return 0;
        }

        int level = levelOf(weapon, Enchantments.FIRE_ASPECT);
        return level * FIRE_ASPECT_SECONDS_PER_LEVEL;
    }

    public static float getPhysicalProtectionReduction(LivingEntity entity, DamageSource source) {
        if (entity == null || source == null || !isPhysicalDamage(source)) {
            return 0.0F;
        }

        int totalLevels = 0;
        for (ItemStack armorPiece : entity.getArmorSlots()) {
            totalLevels += levelOf(armorPiece, Enchantments.PROTECTION);
        }

        return totalLevels * PROTECTION_PHYSICAL_REDUCTION_PER_LEVEL;
    }

    public static float getMagicResistanceReduction(LivingEntity entity, DamageSource source) {
        if (entity == null || source == null || !isMagicDamage(source)) {
            return 0.0F;
        }

        int totalLevels = 0;
        for (ItemStack armorPiece : entity.getArmorSlots()) {
            totalLevels += levelOf(armorPiece, PBMEnchantments.MAGIC_RESISTANCE);
        }

        return totalLevels * MAGIC_RESISTANCE_REDUCTION_PER_LEVEL;
    }

    public static boolean isPhysicalDamage(DamageSource source) {
        if (source == null) {
            return false;
        }

        return !source.is(DamageTypeTags.BYPASSES_ARMOR)
                && !source.is(DamageTypeTags.IS_FIRE)
                && !source.is(DamageTypeTags.IS_EXPLOSION)
                && !isMagicDamage(source);
    }

    public static boolean isMagicDamage(DamageSource source) {
        if (source == null) {
            return false;
        }

        return source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || ArmorCalculationHelper.isIronsSpellbooksDamage(source);
    }
}
