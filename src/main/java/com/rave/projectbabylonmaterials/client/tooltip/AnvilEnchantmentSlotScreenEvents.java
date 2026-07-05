package com.rave.projectbabylonmaterials.client.tooltip;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import com.rave.projectbabylonmaterials.mixin.AbstractContainerScreenAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class AnvilEnchantmentSlotScreenEvents {
    private static final int MESSAGE_COLOR = 0xFF5555;
    private static final int BOX_COLOR = 0x4F000000;
    private static final int MESSAGE_Y_OFFSET = 69;
    private static final int BOX_TOP_Y_OFFSET = 67;
    private static final int BOX_BOTTOM_Y_OFFSET = 79;
    private static final int RIGHT_PADDING = 8;

    private AnvilEnchantmentSlotScreenEvents() {
    }

    @SubscribeEvent
    public static void onRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof AnvilScreen anvilScreen)) {
            return;
        }

        int cost = anvilScreen.getMenu().getCost();
        if (cost != EnchantmentSlotHelper.NO_AVAILABLE_ENCHANTMENT_SLOTS_COST
                && cost != EnchantmentSlotHelper.NO_ENCHANTMENT_SLOTS_COST) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        Component message = Component.translatable(
                        cost == EnchantmentSlotHelper.NO_ENCHANTMENT_SLOTS_COST
                                ? "tooltip.project_babylon_materials.no_enchantment_slots"
                                : "tooltip.project_babylon_materials.no_available_enchantment_slots")
                .withStyle(ChatFormatting.RED);
        var accessor = (AbstractContainerScreenAccessor) anvilScreen;
        int textWidth = font.width(message);
        int rightX = accessor.pbm$getLeftPos() + accessor.pbm$getImageWidth() - RIGHT_PADDING;
        int drawX = rightX - textWidth - 2;
        int drawY = accessor.pbm$getTopPos() + MESSAGE_Y_OFFSET;

        event.getGuiGraphics().fill(drawX - 2, accessor.pbm$getTopPos() + BOX_TOP_Y_OFFSET, rightX, accessor.pbm$getTopPos() + BOX_BOTTOM_Y_OFFSET, BOX_COLOR);
        event.getGuiGraphics().drawString(font, message, drawX, drawY, MESSAGE_COLOR, false);
    }
}