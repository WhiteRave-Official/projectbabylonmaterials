package com.rave.projectbabylonmaterials.item.gem;

import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.gem.GemUpgradeHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GemItem extends Item {
    private final GemType gemType;

    public GemItem(GemType gemType) {
        super(new Item.Properties().stacksTo(1));
        this.gemType = gemType;
    }

    public GemType getGemType() {
        return gemType;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemRarityTier rarity = ItemRarityHelper.getRarity(stack).orElse(ItemRarityTier.COMMON);
        tooltip.add(gemType.createDescription(rarity).copy().withStyle(ChatFormatting.GRAY));
        if (rarity == ItemRarityTier.LEGENDARY) {
            tooltip.add(Component.translatable("tooltip.project_babylon_materials.gem_cannot_upgrade").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.project_babylon_materials.gem_remaining_attempts", GemUpgradeHelper.getRemainingAttempts(stack))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltip.add(Component.translatable("tooltip.project_babylon_materials.applies_to")
                .append(": ")
                .append(gemType.createApplicationsText())
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
