package com.rave.projectbabylonmaterials.gem;

import com.rave.projectbabylonmaterials.balance.PBMBalances;
import com.rave.projectbabylonmaterials.balance.RarityBalance;
import com.rave.projectbabylonmaterials.init.PBMDataComponents;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.item.gem.GemItem;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class GemUpgradeHelper {
    public static final int DEFAULT_UPGRADE_ATTEMPTS = 3;

    private GemUpgradeHelper() {
    }

    public static boolean isGem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof GemItem;
    }

    public static boolean ensureUpgradeAttempts(ItemStack stack) {
        if (!isGem(stack)) {
            return false;
        }

        if (stack.has(PBMDataComponents.GEM_UPGRADE_ATTEMPTS.get())) {
            return false;
        }

        ItemRarityTier rarity = ItemRarityHelper.getRarity(stack).orElse(ItemRarityTier.COMMON);
        stack.set(PBMDataComponents.GEM_UPGRADE_ATTEMPTS.get(), getMaxAttempts(rarity));
        return true;
    }

    public static int getRemainingAttempts(ItemStack stack) {
        if (!isGem(stack)) {
            return 0;
        }

        Integer attempts = stack.get(PBMDataComponents.GEM_UPGRADE_ATTEMPTS.get());
        if (attempts == null) {
            return getMaxAttempts(ItemRarityHelper.getRarity(stack).orElse(ItemRarityTier.COMMON));
        }

        return Math.max(0, attempts);
    }

    public static void consumeFailedAttempt(ItemStack stack) {
        if (!isGem(stack)) {
            return;
        }

        stack.set(PBMDataComponents.GEM_UPGRADE_ATTEMPTS.get(), Math.max(0, getRemainingAttempts(stack) - 1));
    }

    public static boolean canUpgrade(ItemStack stack) {
        if (!isGem(stack)) {
            return false;
        }

        ItemRarityTier rarity = ItemRarityHelper.getRarity(stack).orElse(ItemRarityTier.COMMON);
        return rarity != ItemRarityTier.LEGENDARY && getRemainingAttempts(stack) > 0;
    }

    public static Optional<UpgradeRecipe> getUpgradeRecipe(ItemStack leftMaterial, ItemStack rightMaterial, ItemStack gemStack) {
        if (!canUpgrade(gemStack)) {
            return Optional.empty();
        }

        ItemRarityTier currentRarity = ItemRarityHelper.getRarity(gemStack).orElse(ItemRarityTier.COMMON);
        ItemRarityTier nextRarity = getNextRarity(currentRarity);
        if (nextRarity == null) {
            return Optional.empty();
        }

        Item requiredMaterial = getRequiredMaterial(nextRarity);
        int requiredDust = getRequiredDust(nextRarity);
        int requiredXp = getRequiredXp(nextRarity);
        int successChance = getSuccessChance(currentRarity);

        boolean leftDust = leftMaterial.is(PBMItems.GEM_DUST.get()) && leftMaterial.getCount() >= requiredDust;
        boolean rightDust = rightMaterial.is(PBMItems.GEM_DUST.get()) && rightMaterial.getCount() >= requiredDust;
        boolean leftMaterialMatch = leftMaterial.is(requiredMaterial);
        boolean rightMaterialMatch = rightMaterial.is(requiredMaterial);

        if ((leftDust && rightMaterialMatch) || (rightDust && leftMaterialMatch)) {
            return Optional.of(new UpgradeRecipe(nextRarity, requiredMaterial, requiredDust, requiredXp, successChance));
        }

        return Optional.empty();
    }

    public static ItemStack createUpgradedGem(ItemStack sourceGem, ItemRarityTier nextRarity) {
        ItemStack result = sourceGem.copy();
        result.setCount(1);
        int remainingAttempts = getRemainingAttempts(sourceGem);
        ItemRarityHelper.applyRarity(result, nextRarity, RandomSource.create());
        result.set(PBMDataComponents.GEM_UPGRADE_ATTEMPTS.get(), nextRarity == ItemRarityTier.LEGENDARY ? 0 : remainingAttempts);
        return result;
    }

    public static ItemRarityTier getNextRarity(ItemRarityTier currentRarity) {
        return switch (currentRarity) {
            case COMMON -> ItemRarityTier.UNCOMMON;
            case UNCOMMON -> ItemRarityTier.RARE;
            case RARE -> ItemRarityTier.EPIC;
            case EPIC -> ItemRarityTier.LEGENDARY;
            case LEGENDARY -> null;
        };
    }

    private static int getMaxAttempts(ItemRarityTier rarity) {
        return PBMBalances.rarity(rarity).map(RarityBalance::maxUpgradeAttempts)
                .orElse(rarity == ItemRarityTier.LEGENDARY ? 0 : DEFAULT_UPGRADE_ATTEMPTS);
    }

    private static Item getRequiredMaterial(ItemRarityTier targetRarity) {
        Optional<Item> material = PBMBalances.rarity(targetRarity).flatMap(RarityBalance::material);
        if (material.isPresent()) {
            return material.get();
        }

        return switch (targetRarity) {
            case UNCOMMON -> PBMItems.PURE_TEAR.get();
            case RARE -> PBMItems.ANCIENT_AMBER.get();
            case EPIC -> PBMItems.MAGIC_CRYSTAL.get();
            case LEGENDARY -> PBMItems.FATE_ORB.get();
            case COMMON -> throw new IllegalArgumentException("Common rarity is not a valid upgrade target");
        };
    }

    private static int getRequiredDust(ItemRarityTier targetRarity) {
        return PBMBalances.rarity(targetRarity).map(RarityBalance::requiredDust).orElseGet(() -> switch (targetRarity) {
            case UNCOMMON -> 2;
            case RARE -> 4;
            case EPIC -> 6;
            case LEGENDARY -> 8;
            case COMMON -> 0;
        });
    }

    private static int getRequiredXp(ItemRarityTier targetRarity) {
        return PBMBalances.rarity(targetRarity).map(RarityBalance::requiredXp).orElseGet(() -> switch (targetRarity) {
            case UNCOMMON -> 3;
            case RARE -> 5;
            case EPIC -> 7;
            case LEGENDARY -> 10;
            case COMMON -> 0;
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

    public record UpgradeRecipe(ItemRarityTier nextRarity, Item requiredMaterial, int requiredDust, int requiredXp, int successChance) {
    }
}
