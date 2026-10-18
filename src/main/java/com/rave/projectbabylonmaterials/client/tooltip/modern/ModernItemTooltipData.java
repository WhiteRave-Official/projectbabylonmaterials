package com.rave.projectbabylonmaterials.client.tooltip.modern;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ModernItemTooltipData(ItemStack stack, List<Either<FormattedText, TooltipComponent>> elements,
                                    int screenWidth, int screenHeight) implements TooltipComponent {
    public ModernItemTooltipData {
        elements = List.copyOf(elements);
    }
}
