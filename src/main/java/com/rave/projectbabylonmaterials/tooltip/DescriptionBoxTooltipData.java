package com.rave.projectbabylonmaterials.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

public record DescriptionBoxTooltipData(List<Component> lines, TooltipFrameStyle frameStyle) implements TooltipComponent {

    public DescriptionBoxTooltipData {
        lines = List.copyOf(lines);
    }
}