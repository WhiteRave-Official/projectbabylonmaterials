package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ArmorPieceTooltipData(ItemStack stack, Component label, boolean equipped) implements TooltipComponent {
}

