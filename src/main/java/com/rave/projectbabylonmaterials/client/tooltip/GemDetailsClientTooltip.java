package com.rave.projectbabylonmaterials.client.tooltip;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.tooltip.GemDetailsTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.List;

public final class GemDetailsClientTooltip implements ClientTooltipComponent {
    private static final ResourceLocation EMPTY_SLOT_FRAME = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/gui/tooltip/frame/stone/stone_frame_empty.png");
    private static final ResourceLocation EQUIPPED_SLOT_FRAME = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/gui/tooltip/frame/stone/stone_frame_equipped.png");
    private static final int BASE_SLOT_WIDTH = 126;
    private static final int MIN_SLOT_WIDTH = 126;
    private static final int SLOT_HEIGHT = 42;
    private static final int TITLE_HEIGHT = 10;
    private static final int SLOT_ICON_SIZE = 16;
    private static final int EQUIPPED_ICON_SIZE = 12;
    private static final int APPLIES_ICON_SIZE = 9;
    private static final int PADDING_X = 5;
    private static final int PADDING_Y = 5;
    private static final int SLOT_GAP_Y = 4;
    private static final int TITLE_GAP = 4;
    private static final int ICON_TEXT_GAP = 4;
    private static final int APPLIES_GAP = 2;
    private static final int PANEL_PADDING_X = 6;
    private static final int PANEL_PADDING_Y = 6;
    private static final int EMPTY_TEXT_COLOR = 0xBEBEBE;
    private static final int TITLE_COLOR = 0xFFD700;
    private static final int NAME_COLOR = 0x55FFFF;
    private static final int DESCRIPTION_COLOR = 0xD0D0D0;
    private static final int LABEL_COLOR = 0xAAAAAA;
    private static final int PANEL_FILL_COLOR = 0xCC242424;
    private static final int PANEL_BORDER_COLOR = 0xFFBDBDBD;
    private static final float EMPTY_TEXT_SCALE = 0.85F;
    private static final float NAME_SCALE = 0.85F;
    private static final float DESCRIPTION_SCALE = 0.68F;
    private static final float APPLIES_SCALE = 0.60F;
    private static final int DESCRIPTION_LINE_LIMIT = 2;

    private final GemDetailsTooltipData data;

    public GemDetailsClientTooltip(GemDetailsTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        int slotCount = Math.max(1, data.slots().size());
        return PADDING_Y * 2 + TITLE_HEIGHT + TITLE_GAP + PANEL_PADDING_Y * 2 + SLOT_HEIGHT * slotCount + SLOT_GAP_Y * Math.max(0, slotCount - 1);
    }

    @Override
    public int getWidth(Font font) {
        return PADDING_X * 2 + PANEL_PADDING_X * 2 + computeSlotWidth(font);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int slotWidth = computeSlotWidth(font);
        int titleX = x + PADDING_X;
        int titleY = y + PADDING_Y;
        guiGraphics.drawString(font, data.title(), titleX, titleY, TITLE_COLOR, false);

        int panelX = x + PADDING_X;
        int panelY = titleY + TITLE_HEIGHT + TITLE_GAP;
        int panelWidth = PANEL_PADDING_X * 2 + slotWidth;
        int panelHeight = PANEL_PADDING_Y * 2 + SLOT_HEIGHT * data.slots().size() + SLOT_GAP_Y * Math.max(0, data.slots().size() - 1);
        renderPanel(guiGraphics, panelX, panelY, panelWidth, panelHeight);

        int slotX = panelX + PANEL_PADDING_X;
        int slotY = panelY + PANEL_PADDING_Y;
        for (GemDetailsTooltipData.SlotEntry slot : data.slots()) {
            renderSlot(guiGraphics, font, slotX, slotY, slot, slotWidth);
            slotY += SLOT_HEIGHT + SLOT_GAP_Y;
        }
    }

    private int computeSlotWidth(Font font) {
        int width = BASE_SLOT_WIDTH;
        for (GemDetailsTooltipData.SlotEntry slot : data.slots()) {
            int lineWidth = SLOT_ICON_SIZE + ICON_TEXT_GAP + scaledTextWidth(font, slot.label(), slot.empty() ? EMPTY_TEXT_SCALE : NAME_SCALE) + 10;
            width = Math.max(width, lineWidth);
        }
        return Math.max(MIN_SLOT_WIDTH, width);
    }

    private void renderPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, PANEL_FILL_COLOR);
        guiGraphics.fill(x, y, x + width, y + 1, PANEL_BORDER_COLOR);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, PANEL_BORDER_COLOR);
        guiGraphics.fill(x, y, x + 1, y + height, PANEL_BORDER_COLOR);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, PANEL_BORDER_COLOR);
    }

    private void renderSlot(GuiGraphics guiGraphics, Font font, int x, int y, GemDetailsTooltipData.SlotEntry slot, int slotWidth) {
        if (slot.empty()) {
            int iconY = y + (SLOT_HEIGHT - SLOT_ICON_SIZE) / 2;
            int textY = y + (SLOT_HEIGHT - scaledTextHeight(EMPTY_TEXT_SCALE)) / 2;
            guiGraphics.blit(EMPTY_SLOT_FRAME, x, iconY, 0, 0, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE);
            drawScaledText(guiGraphics, font, slot.label(), x + SLOT_ICON_SIZE + ICON_TEXT_GAP, textY, EMPTY_TEXT_COLOR, EMPTY_TEXT_SCALE);
            return;
        }

        int frameY = y + 2;
        guiGraphics.blit(EQUIPPED_SLOT_FRAME, x, frameY, 0, 0, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE);
        renderCenteredScaledItem(guiGraphics, slot.iconStack(), x, frameY, SLOT_ICON_SIZE, EQUIPPED_ICON_SIZE);
        drawScaledText(guiGraphics, font, slot.label(), x + SLOT_ICON_SIZE + ICON_TEXT_GAP, y + 1, NAME_COLOR, NAME_SCALE);

        int contentX = x + SLOT_ICON_SIZE + ICON_TEXT_GAP;
        int descriptionY = y + 12;
        for (FormattedCharSequence line : splitDescription(font, slot.description(), slotWidth)) {
            drawScaledSequence(guiGraphics, font, line, contentX, descriptionY, DESCRIPTION_COLOR, DESCRIPTION_SCALE);
            descriptionY += scaledTextHeight(DESCRIPTION_SCALE);
        }

        int appliesY = y + SLOT_HEIGHT - scaledTextHeight(APPLIES_SCALE) - APPLIES_ICON_SIZE;
        drawScaledText(guiGraphics, font, data.appliesToLabel(), contentX, appliesY, LABEL_COLOR, APPLIES_SCALE);
        int iconStartX = contentX + scaledTextWidth(font, data.appliesToLabel(), APPLIES_SCALE) + 4;
        renderApplicableItems(guiGraphics, iconStartX, appliesY - 1, slot.applicableItems());
    }

    private List<FormattedCharSequence> splitDescription(Font font, Component description, int slotWidth) {
        int maxWidth = Math.max(40, Math.round((slotWidth - SLOT_ICON_SIZE - ICON_TEXT_GAP - 6) / DESCRIPTION_SCALE));
        List<FormattedCharSequence> lines = font.split(description, maxWidth);
        return lines.size() <= DESCRIPTION_LINE_LIMIT ? lines : lines.subList(0, DESCRIPTION_LINE_LIMIT);
    }

    private void renderApplicableItems(GuiGraphics guiGraphics, int x, int y, List<ItemStack> items) {
        int currentX = x;
        for (ItemStack stack : items) {
            renderScaledItem(guiGraphics, stack, currentX, y, APPLIES_ICON_SIZE / (float) SLOT_ICON_SIZE);
            currentX += APPLIES_ICON_SIZE + APPLIES_GAP;
        }
    }

    private void renderCenteredScaledItem(GuiGraphics guiGraphics, ItemStack stack, int frameX, int frameY, int frameSize, int iconSize) {
        int offset = (frameSize - iconSize) / 2;
        float scale = iconSize / 16.0F;
        renderScaledItem(guiGraphics, stack, frameX + offset, frameY + offset, scale);
    }

    private void renderScaledItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y, float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.renderItem(stack, 0, 0);
        guiGraphics.pose().popPose();
    }

    private int scaledTextHeight(float scale) {
        return Math.max(6, Math.round(9 * scale));
    }

    private int scaledTextWidth(Font font, Component text, float scale) {
        return Math.max(1, Math.round(font.width(text) * scale));
    }

    private void drawScaledText(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color, float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(font, text, 0, 0, color, false);
        guiGraphics.pose().popPose();
    }

    private void drawScaledSequence(GuiGraphics guiGraphics, Font font, FormattedCharSequence text, int x, int y, int color, float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(font, text, 0, 0, color, false);
        guiGraphics.pose().popPose();
    }
}