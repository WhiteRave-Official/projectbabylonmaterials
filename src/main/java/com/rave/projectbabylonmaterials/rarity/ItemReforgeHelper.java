package com.rave.projectbabylonmaterials.rarity;

import com.rave.projectbabylonmaterials.balance.PBMBalances;
import com.rave.projectbabylonmaterials.balance.RarityBalance;
import com.rave.projectbabylonmaterials.init.PBMItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ItemReforgeHelper {
    public static final int REQUIRED_MATERIAL_COUNT = 2;

    private ItemReforgeHelper() {
    }

    public static Optional<ReforgeRecipe> getReforgeRecipe(ItemStack materialStack, ItemStack targetStack) {
        if (materialStack.isEmpty() || targetStack.isEmpty() || !ItemRarityHelper.supportsSlottedItem(targetStack)) {
            return Optional.empty();
        }

        Optional<ItemRarityTier> rarityOptional = ItemRarityHelper.getRarity(targetStack);
        if (rarityOptional.isEmpty()) {
            return Optional.empty();
        }

        ItemRarityTier currentRarity = rarityOptional.get();
        ItemRarityTier nextRarity = getNextRarity(currentRarity);
        if (nextRarity == null) {
            return Optional.empty();
        }

        int materialCount = getRequiredMaterialCount(nextRarity);
        if (!matchesMaterial(materialStack, nextRarity) || materialStack.getCount() < materialCount) {
            return Optional.empty();
        }

        return Optional.of(new ReforgeRecipe(nextRarity, getRequiredXp(nextRarity), getSuccessChance(currentRarity), materialCount));
    }

    public static ItemStack createReforgedItem(ItemStack sourceStack, ItemRarityTier rarity, RandomSource random) {
        ItemStack result = sourceStack.copy();
        ItemRarityHelper.applyRarity(result, rarity, random);
        return result;
    }

    public static int getRequiredMaterialCount(ItemRarityTier targetRarity) {
        return PBMBalances.rarity(targetRarity).map(RarityBalance::reforgeMaterialCount).orElse(REQUIRED_MATERIAL_COUNT);
    }

    private static boolean matchesMaterial(ItemStack stack, ItemRarityTier targetRarity) {
        Optional<Item> material = PBMBalances.rarity(targetRarity).flatMap(RarityBalance::material);
        if (material.isPresent()) {
            return stack.is(material.get());
        }

        return switch (targetRarity) {
            case COMMON -> false;
            case UNCOMMON -> stack.is(PBMItems.PURE_TEAR.get());
            case RARE -> stack.is(PBMItems.ANCIENT_AMBER.get());
            case EPIC -> stack.is(PBMItems.MAGIC_CRYSTAL.get());
            case LEGENDARY -> stack.is(PBMItems.FATE_ORB.get());
        };
    }

    private static ItemRarityTier getNextRarity(ItemRarityTier currentRarity) {
        return switch (currentRarity) {
            case COMMON -> ItemRarityTier.UNCOMMON;
            case UNCOMMON -> ItemRarityTier.RARE;
            case RARE -> ItemRarityTier.EPIC;
            case EPIC -> ItemRarityTier.LEGENDARY;
            case LEGENDARY -> null;
        };
    }

    private static int getRequiredXp(ItemRarityTier targetRarity) {
        return PBMBalances.rarity(targetRarity).map(RarityBalance::requiredXp).orElseGet(() -> switch (targetRarity) {
            case COMMON -> 0;
            case UNCOMMON -> 3;
            case RARE -> 5;
            case EPIC -> 7;
            case LEGENDARY -> 10;
        });
    }

    private static int getSuccessChance(ItemRarityTier currentRarity) {
        return PBMBalances.rarity(currentRarity).map(RarityBalance::successChance).orElseGet(() -> switch (currentRarity) {
            case COMMON -> 75;
            case UNCOMMON -> 55;
            case RARE -> 35;
            case EPIC -> 20;
            case LEGENDARY -> 0;
        });
    }

    public record ReforgeRecipe(ItemRarityTier nextRarity, int requiredXp, int successChance, int materialCount) {
    }
}
