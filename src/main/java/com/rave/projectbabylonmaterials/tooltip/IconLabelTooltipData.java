package com.rave.projectbabylonmaterials.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record IconLabelTooltipData(Component label, ResourceLocation frameTexture,
                                   ResourceLocation iconTexture) implements TooltipComponent {
}