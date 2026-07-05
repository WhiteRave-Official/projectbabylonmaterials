package com.rave.projectbabylonmaterials.client.tooltip;

import com.rave.projectbabylonmaterials.tooltip.IconLabelTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class IconLabelClientTooltip implements ClientTooltipComponent {

    private static final int ICON_SIZE = 16;
    private static final float ITEM_SCALE = 1.0F;
    private static final float ITEM_OFFSET = (ICON_SIZE - (ICON_SIZE * ITEM_SCALE)) / 2.0F;
    private static final int ICON_TEXT_GAP = 4;
    private final IconLabelTooltipData data;

    public IconLabelClientTooltip(IconLabelTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return ICON_SIZE;
    }

    @Override
    public int getWidth(Font font) {
        return ICON_SIZE + ICON_TEXT_GAP + font.width(data.label());
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        FormattedCharSequence text = data.label().getVisualOrderText();
        font.drawInBatch(text, x + ICON_SIZE + ICON_TEXT_GAP, y + 4, 0xFFFFFF, false, matrix, bufferSource,
                Font.DisplayMode.NORMAL, 0, 15728880);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        guiGraphics.blit(data.frameTexture(), x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + ITEM_OFFSET, y + ITEM_OFFSET, 0.0F);
        guiGraphics.pose().scale(ITEM_SCALE, ITEM_SCALE, 1.0F);
        guiGraphics.blit(data.iconTexture(), 0, 0, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        guiGraphics.pose().popPose();
    }
}