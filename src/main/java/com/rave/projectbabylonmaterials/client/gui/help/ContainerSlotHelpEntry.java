package com.rave.projectbabylonmaterials.client.gui.help;

import com.rave.projectbabylonmaterials.tooltip.ContainerHelpTooltipData;
import com.rave.projectbabylonmaterials.tooltip.TooltipFrameStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ContainerSlotHelpEntry(int slotIndex,
                                     Component title,
                                     Component acceptsLabel,
                                     ItemStack acceptsStack,
                                     List<Component> descriptionLines) {
    public ContainerSlotHelpEntry {
        acceptsStack = acceptsStack.copy();
        descriptionLines = List.copyOf(descriptionLines);
    }

    public ContainerHelpTooltipData createTooltipData() {
        return new ContainerHelpTooltipData(title, acceptsLabel, acceptsStack, descriptionLines, TooltipFrameStyle.iron());
    }
}
