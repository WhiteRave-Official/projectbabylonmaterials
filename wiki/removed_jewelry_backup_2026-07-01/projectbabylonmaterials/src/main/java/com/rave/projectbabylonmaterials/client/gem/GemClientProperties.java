package com.rave.projectbabylonmaterials.client.gem;

import com.rave.projectbabylonmaterials.gem.GemVisualTier;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.world.item.ItemStack;

public final class GemClientProperties {
    private GemClientProperties() {
    }

    public static float getVisualProperty(ItemStack stack) {
        ItemRarityTier rarity = ItemRarityHelper.getRarity(stack).orElse(ItemRarityTier.COMMON);
        return GemVisualTier.fromRarity(rarity).getModelPredicate();
    }
}