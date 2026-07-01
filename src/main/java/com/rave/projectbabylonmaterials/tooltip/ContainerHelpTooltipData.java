package com.rave.projectbabylonmaterials.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ContainerHelpTooltipData(Component title,
                                       Component acceptsLabel,
                                       ItemStack acceptsStack,
                                       List<Component> descriptionLines,
                                       TooltipFrameStyle frameStyle) implements TooltipComponent {
    public ContainerHelpTooltipData {
        acceptsStack = acceptsStack.copy();
        descriptionLines = List.copyOf(descriptionLines);
    }
}
