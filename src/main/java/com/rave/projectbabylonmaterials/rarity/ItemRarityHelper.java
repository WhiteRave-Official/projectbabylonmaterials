package com.rave.projectbabylonmaterials.rarity;

import com.rave.projectbabylonmaterials.gem.GemApplication;
import com.rave.projectbabylonmaterials.gem.GemUpgradeHelper;
import com.rave.projectbabylonmaterials.init.PBMDataComponents;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.balance.PBMBalances;
import com.rave.projectbabylonmaterials.balance.RarityBalance;
import com.rave.projectbabylonmaterials.item.gem.GemItem;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

import java.util.Optional;

public final class ItemRarityHelper {

    private ItemRarityHelper() {
    }

    public static boolean supportsRarity(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        return item instanceof GemItem || supportsSlottedItem(stack);
    }

    public static boolean supportsSlottedItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        return item instanceof ArmorItem
                || item instanceof TieredItem
                || item instanceof BowItem
                || item instanceof CrossbowItem
                || item instanceof TridentItem
                || GemApplication.isMagicWeapon(item);
    }

    public static boolean hasRarity(ItemStack stack) {
        return stack.has(PBMDataComponents.RARITY.get());
    }

    public static Optional<ItemRarityTier> getRarity(ItemStack stack) {
        return Optional.ofNullable(stack.get(PBMDataComponents.RARITY.get()));
    }

    public static boolean ensureRarity(ItemStack stack, RandomSource random) {
        if (!supportsRarity(stack)) {
            return false;
        }

        if (hasRarity(stack)) {
            boolean changed = ensureTags(stack, random);
            if (stack.getItem() instanceof GemItem) {
                changed |= GemUpgradeHelper.ensureUpgradeAttempts(stack);
            }
            return changed;
        }

        applyRarity(stack, rollRarity(random), random);
        if (stack.getItem() instanceof GemItem) {
            GemUpgradeHelper.ensureUpgradeAttempts(stack);
        }
        return true;
    }

    public static void applyRarity(ItemStack stack, ItemRarityTier rarity, RandomSource random) {
        stack.set(PBMDataComponents.RARITY.get(), rarity);

        if (supportsSlottedItem(stack)) {
            stack.set(PBMDataComponents.ENCHANT_SLOT_COUNT.get(), rollEnchantSlots(rarity, random));
            stack.set(PBMDataComponents.GEM_SLOT_COUNT.get(), rollGemSlots(rarity, random));
        }
    }

    public static MutableComponent createDisplayName(ItemStack stack, Component originalName) {
        Optional<ItemRarityTier> fixedMaterialRarity = getFixedMaterialColor(stack);
        if (fixedMaterialRarity.isPresent()) {
            return originalName.copy().withStyle(fixedMaterialRarity.get().getColor());
        }

        Optional<ItemRarityTier> rarity = getRarity(stack);
        if (rarity.isEmpty() || rarity.get() == ItemRarityTier.COMMON) {
            return originalName.copy();
        }

        MutableComponent prefix = Component.translatable("rarity.project_babylon_materials." + rarity.get().getId());
        return Component.empty()
                .append(prefix)
                .append(CommonComponents.SPACE)
                .append(originalName.copy())
                .withStyle(rarity.get().getColor());
    }

    private static Optional<ItemRarityTier> getFixedMaterialColor(ItemStack stack) {
        if (stack.is(PBMItems.PURE_TEAR.get())) {
            return Optional.of(ItemRarityTier.UNCOMMON);
        }
        if (stack.is(PBMItems.ANCIENT_AMBER.get())) {
            return Optional.of(ItemRarityTier.RARE);
        }
        if (stack.is(PBMItems.MAGIC_CRYSTAL.get())) {
            return Optional.of(ItemRarityTier.EPIC);
        }
        if (stack.is(PBMItems.FATE_ORB.get())) {
            return Optional.of(ItemRarityTier.LEGENDARY);
        }
        return Optional.empty();
    }

    public static ItemRarityTier rollRarity(RandomSource random) {
        int roll = random.nextInt(100) + 1;
        int cumulative = 0;
        for (ItemRarityTier rarity : ItemRarityTier.values()) {
            cumulative += rarity.getRollWeight();
            if (roll <= cumulative) {
                return rarity;
            }
        }
        return ItemRarityTier.COMMON;
    }

    private static boolean ensureTags(ItemStack stack, RandomSource random) {
        if (!supportsSlottedItem(stack)) {
            return false;
        }

        boolean changed = false;
        ItemRarityTier rarity = getRarity(stack).orElse(ItemRarityTier.COMMON);

        if (!stack.has(PBMDataComponents.ENCHANT_SLOT_COUNT.get())) {
            stack.set(PBMDataComponents.ENCHANT_SLOT_COUNT.get(), rollEnchantSlots(rarity, random));
            changed = true;
        }

        if (!stack.has(PBMDataComponents.GEM_SLOT_COUNT.get())) {
            stack.set(PBMDataComponents.GEM_SLOT_COUNT.get(), rollGemSlots(rarity, random));
            changed = true;
        }

        return changed;
    }

    private static int rollEnchantSlots(ItemRarityTier rarity, RandomSource random) {
        Optional<RarityBalance> balance = PBMBalances.rarity(rarity);
        if (balance.isPresent()) {
            RarityBalance values = balance.get();
            int slots = values.baseEnchantSlots();
            if (values.enchantBonusChance() > 0 && rollPercent(random, values.enchantBonusChance())) {
                slots += 1;
            }
            return slots;
        }

        return switch (rarity) {
            case COMMON -> 0;
            case UNCOMMON -> 1;
            case RARE -> 1 + (rollPercent(random, 30) ? 1 : 0);
            case EPIC -> 2 + (rollPercent(random, 45) ? 1 : 0);
            case LEGENDARY -> 3 + (rollPercent(random, 50) ? 1 : 0);
        };
    }

    private static int rollGemSlots(ItemRarityTier rarity, RandomSource random) {
        Optional<RarityBalance> balance = PBMBalances.rarity(rarity);
        if (balance.isPresent()) {
            RarityBalance values = balance.get();
            int slots = values.baseGemSlots();
            if (values.gemSlotBonusChance() > 0 && rollPercent(random, values.gemSlotBonusChance())) {
                slots += 1;
            }
            return slots;
        }

        return switch (rarity) {
            case COMMON, UNCOMMON -> 0;
            case RARE -> rollPercent(random, 35) ? 1 : 0;
            case EPIC -> 1;
            case LEGENDARY -> 2;
        };
    }

    private static boolean rollPercent(RandomSource random, int chance) {
        return random.nextInt(100) < chance;
    }
}
