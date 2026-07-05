package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ArmorSetTooltipData(
        Component setName,
        int matchedPieces,
        List<ArmorPieceEntry> armorPieces,
        List<BonusEntry> bonuses
) implements TooltipComponent {

    public record ArmorPieceEntry(ItemStack stack, Component label, boolean equipped) {
    }

    public record BonusEntry(ArmorSetBonusType type, Component displayName, ResourceLocation frameTexture,
                             ResourceLocation iconTexture,
                             List<Component> descriptionLines) {
    }
}

