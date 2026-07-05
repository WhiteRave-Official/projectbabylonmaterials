package com.rave.projectbabylonmaterials.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record EnchantmentDetailsTooltipData(Component title, Component appliesToLabel, List<SlotEntry> slots) implements TooltipComponent {
    public EnchantmentDetailsTooltipData {
        slots = List.copyOf(slots);
    }

    public record SlotEntry(boolean empty, ItemStack iconStack, Component label, Component description, List<ItemStack> applicableItems) {
        public SlotEntry {
            iconStack = iconStack.copy();
            applicableItems = List.copyOf(applicableItems);
        }
    }
}
