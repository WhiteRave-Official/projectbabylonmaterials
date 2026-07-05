package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record ArmorSetBonusTooltipData(Component label, ResourceLocation frameTexture,
                                       ResourceLocation iconTexture) implements TooltipComponent {
}

