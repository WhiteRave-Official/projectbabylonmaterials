package com.rave.projectbabylonmaterials.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rave.projectbabylonmaterials.tooltip.ContainerHelpTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public final class ContainerHelpClientTooltip implements ClientTooltipComponent {
    private static final int TILE_SIZE = 16;
    private static final int PADDING_X = 8;
    private static final int PADDING_Y = 6;
    private static final int LINE_GAP = 3;
    private static final int ITEM_TEXT_GAP = 4;
    private static final int ITEM_SIZE = 16;
    private static final int TEXT_LINE_HEIGHT = 9;
    private static final int DESCRIPTION_TO_FRAME_GAP = 4;
    private static final Component DESCRIPTION_LABEL = Component.translatable("tooltip.project_babylon_materials.container_help.description_label");

    private final ContainerHelpTooltipData data;

    public ContainerHelpClientTooltip(ContainerHelpTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return getAcceptsSectionHeight()
                + LINE_GAP
                + TEXT_LINE_HEIGHT
                + DESCRIPTION_TO_FRAME_GAP
                + TILE_SIZE
                + getFrameContentHeight()
                + TILE_SIZE;
    }

    @Override
    public int getWidth(Font font) {
        return Math.max(Math.max(getAcceptsWidth(font), font.width(DESCRIPTION_LABEL)), TILE_SIZE + getFrameInnerWidth(font) + TILE_SIZE);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        int acceptsY = y + 4;
        draw(font, data.acceptsLabel().getVisualOrderText(), x, acceptsY, 0xD8D8D8, matrix, bufferSource);

        int descriptionLabelY = y + getAcceptsSectionHeight() + LINE_GAP + 4;
        draw(font, DESCRIPTION_LABEL.getVisualOrderText(), x, descriptionLabelY, 0xF5C96A, matrix, bufferSource);

        int frameTextX = x + TILE_SIZE + PADDING_X;
        int frameTextY = y + getAcceptsSectionHeight() + LINE_GAP + TEXT_LINE_HEIGHT + DESCRIPTION_TO_FRAME_GAP + TILE_SIZE + PADDING_Y;

        for (Component line : data.descriptionLines()) {
            draw(font, line.getVisualOrderText(), frameTextX, frameTextY, 0xFFFFFF, matrix, bufferSource);
            frameTextY += TEXT_LINE_HEIGHT + LINE_GAP;
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int itemX = x + font.width(data.acceptsLabel()) + ITEM_TEXT_GAP;
        int itemY = y;
        guiGraphics.renderItem(data.acceptsStack(), itemX, itemY);

        int frameY = y + getAcceptsSectionHeight() + LINE_GAP + TEXT_LINE_HEIGHT + DESCRIPTION_TO_FRAME_GAP;
        int contentHeight = getFrameContentHeight();
        int frameInnerWidth = getFrameInnerWidth(font);
        int outerWidth = TILE_SIZE + frameInnerWidth + TILE_SIZE;
        int outerHeight = TILE_SIZE + contentHeight + TILE_SIZE;

        int leftSpanWidth = Math.max(0, (frameInnerWidth - TILE_SIZE) / 2);
        int rightSpanWidth = Math.max(0, frameInnerWidth - TILE_SIZE - leftSpanWidth);
        int middleX = x + TILE_SIZE + leftSpanWidth;
        int topY = frameY;
        int bottomY = frameY + outerHeight - TILE_SIZE;
        int leftX = x;
        int rightX = x + outerWidth - TILE_SIZE;
        int bodyY = frameY + TILE_SIZE;

        blit(guiGraphics, data.frameStyle().topLeftCorner(), leftX, topY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, data.frameStyle().topRightCorner(), rightX, topY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, data.frameStyle().bottomLeftCorner(), leftX, bottomY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, data.frameStyle().bottomRightCorner(), rightX, bottomY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);

        blit(guiGraphics, data.frameStyle().middle(), middleX, bottomY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, data.frameStyle().middle(), middleX, topY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, true);

        tileHorizontalLeftOfMiddle(guiGraphics, data.frameStyle().horizontalEdge(), middleX, bottomY, leftSpanWidth, false);
        tileHorizontalRightOfMiddle(guiGraphics, data.frameStyle().horizontalEdge(), middleX + TILE_SIZE, bottomY, rightSpanWidth, false);
        tileHorizontalLeftOfMiddle(guiGraphics, data.frameStyle().horizontalEdge(), middleX, topY, leftSpanWidth, true);
        tileHorizontalRightOfMiddle(guiGraphics, data.frameStyle().horizontalEdge(), middleX + TILE_SIZE, topY, rightSpanWidth, true);

        tileVertical(guiGraphics, data.frameStyle().verticalEdge(), leftX, bodyY, contentHeight, false);
        tileVertical(guiGraphics, data.frameStyle().verticalEdge(), rightX, bodyY, contentHeight, true);
    }

    private int getAcceptsWidth(Font font) {
        return font.width(data.acceptsLabel()) + ITEM_TEXT_GAP + ITEM_SIZE;
    }

    private int getAcceptsSectionHeight() {
        return Math.max(TEXT_LINE_HEIGHT, ITEM_SIZE);
    }

    private int getFrameInnerWidth(Font font) {
        int contentWidth = 0;
        for (Component line : data.descriptionLines()) {
            contentWidth = Math.max(contentWidth, font.width(line));
        }
        return Math.max(TILE_SIZE, contentWidth + (PADDING_X * 2));
    }

    private int getFrameContentHeight() {
        int linesHeight = data.descriptionLines().size() * (TEXT_LINE_HEIGHT + LINE_GAP);
        return Math.max(TILE_SIZE, linesHeight + (PADDING_Y * 2));
    }

    private static void draw(Font font, FormattedCharSequence text, int x, int y, int color,
                             Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        font.drawInBatch(text, x, y, color, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
    }

    private void tileHorizontalLeftOfMiddle(GuiGraphics guiGraphics, ResourceLocation texture, int middleX, int y, int width, boolean mirrorY) {
        if (width <= 0) {
            return;
        }

        int remaining = width;
        int currentRight = middleX;
        while (remaining > 0) {
            int segmentWidth = Math.min(TILE_SIZE, remaining);
            int drawX = currentRight - segmentWidth;
            int sourceU = TILE_SIZE - segmentWidth;
            blit(guiGraphics, texture, drawX, y, sourceU, 0, segmentWidth, TILE_SIZE, segmentWidth, TILE_SIZE, false, mirrorY);
            currentRight -= segmentWidth;
            remaining -= segmentWidth;
        }
    }

    private void tileHorizontalRightOfMiddle(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, boolean mirrorY) {
        if (width <= 0) {
            return;
        }

        int remaining = width;
        int currentX = x;
        while (remaining > 0) {
            int segmentWidth = Math.min(TILE_SIZE, remaining);
            blit(guiGraphics, texture, currentX, y, 0, 0, segmentWidth, TILE_SIZE, segmentWidth, TILE_SIZE, false, mirrorY);
            currentX += segmentWidth;
            remaining -= segmentWidth;
        }
    }

    private void tileVertical(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int height, boolean mirrorX) {
        if (height <= 0) {
            return;
        }

        int remaining = height;
        int currentY = y;
        while (remaining > 0) {
            int segmentHeight = Math.min(TILE_SIZE, remaining);
            if (!mirrorX) {
                guiGraphics.blit(texture, x, currentY, 0, 0, TILE_SIZE, segmentHeight, TILE_SIZE, TILE_SIZE);
            } else {
                blit(guiGraphics, texture, x, currentY, 0, 0, TILE_SIZE, segmentHeight, TILE_SIZE, segmentHeight, true, false);
            }
            currentY += segmentHeight;
            remaining -= segmentHeight;
        }
    }

    private void blit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int sourceU, int sourceV,
                      int sourceWidth, int sourceHeight, int drawWidth, int drawHeight,
                      boolean mirrorX, boolean mirrorY) {
        float u0 = sourceU / (float) TILE_SIZE;
        float u1 = (sourceU + sourceWidth) / (float) TILE_SIZE;
        float v0 = sourceV / (float) TILE_SIZE;
        float v1 = (sourceV + sourceHeight) / (float) TILE_SIZE;

        if (mirrorX) {
            float tmp = u0;
            u0 = u1;
            u1 = tmp;
        }
        if (mirrorY) {
            float tmp = v0;
            v0 = v1;
            v1 = tmp;
        }

        Matrix4f matrix = guiGraphics.pose().last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x, y, 0.0F).uv(u0, v0).endVertex();
        buffer.vertex(matrix, x, y + drawHeight, 0.0F).uv(u0, v1).endVertex();
        buffer.vertex(matrix, x + drawWidth, y + drawHeight, 0.0F).uv(u1, v1).endVertex();
        buffer.vertex(matrix, x + drawWidth, y, 0.0F).uv(u1, v0).endVertex();
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
    }
}
