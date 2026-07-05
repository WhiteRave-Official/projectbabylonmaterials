package com.rave.projectbabylonmaterials.client.tooltip;

import com.mojang.datafixers.util.Either;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.gem.GemSlotHelper;
import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import com.rave.projectbabylonmaterials.tooltip.GemDetailsTooltipData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class GemTooltipEvents {
    private GemTooltipEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (Screen.hasControlDown() || !GemSlotHelper.hasGemTooltip(stack)) {
            return;
        }

        if (!Screen.hasAltDown()) {
            event.getToolTip().add(Component.translatable("tooltip.project_babylon_materials.hold_alt_gems")
                    .withStyle(ChatFormatting.WHITE));
            return;
        }

        EnchantmentTooltipEvents.filterTooltipComponents(event.getToolTip(), stack);
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (!Screen.hasAltDown() || Screen.hasControlDown() || !GemSlotHelper.hasGemTooltip(stack)) {
            return;
        }

        EnchantmentTooltipEvents.filterTooltipElements(event.getTooltipElements(), stack);
        event.getTooltipElements().add(Either.right(createTooltipData(stack)));
    }

    private static GemDetailsTooltipData createTooltipData(ItemStack stack) {
        List<GemDetailsTooltipData.SlotEntry> slots = new ArrayList<>();
        List<ItemStack> socketedGems = GemSlotHelper.getSocketedGems(stack);
        int visibleSlotCount = GemSlotHelper.getVisibleSlotCount(stack);

        for (int i = 0; i < visibleSlotCount; i++) {
            if (i < socketedGems.size()) {
                ItemStack gemStack = socketedGems.get(i);
                GemType gemType = GemType.fromStack(gemStack).orElse(null);
                ItemRarityTier rarity = ItemRarityHelper.getRarity(gemStack).orElse(ItemRarityTier.COMMON);
                slots.add(new GemDetailsTooltipData.SlotEntry(
                        false,
                        gemStack,
                        gemStack.getHoverName(),
                        gemType != null ? gemType.createDescription(rarity) : Component.translatable("tooltip.project_babylon_materials.no_gem_description"),
                        gemType != null ? gemType.createApplicablePreviewItems() : List.of()
                ));
            } else {
                slots.add(new GemDetailsTooltipData.SlotEntry(
                        true,
                        ItemStack.EMPTY,
                        Component.translatable("tooltip.project_babylon_materials.empty_gem_slot"),
                        Component.empty(),
                        List.of()
                ));
            }
        }

        return new GemDetailsTooltipData(
                Component.translatable("tooltip.project_babylon_materials.gems"),
                Component.translatable("tooltip.project_babylon_materials.applies_to"),
                slots
        );
    }
}