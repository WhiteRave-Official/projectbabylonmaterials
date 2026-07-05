package com.rave.projectbabylonmaterials.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rave.projectbabylonmaterials.tooltip.DescriptionBoxTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class DescriptionBoxClientTooltip implements ClientTooltipComponent {

    private static final int TILE_SIZE = 16;
    private static final int MIN_CONTENT_PADDING_X = 4;
    private static final int MEDIUM_CONTENT_PADDING_X = 6;
    private static final int MAX_CONTENT_PADDING_X = 8;
    private static final int CONTENT_PADDING_Y = 6;
    private static final int LINE_GAP = 2;
    private static final int TEXT_LINE_HEIGHT = 9;
    private static final int SHORT_TEXT_THRESHOLD = 120;
    private static final int MEDIUM_TEXT_THRESHOLD = 180;
    private final DescriptionBoxTooltipData data;

    public DescriptionBoxClientTooltip(DescriptionBoxTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return TILE_SIZE + getContentHeight() + TILE_SIZE;
    }

    @Override
    public int getWidth(Font font) {
        return TILE_SIZE + getFrameInnerWidth(font) + TILE_SIZE;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        int textY = y + TILE_SIZE + CONTENT_PADDING_Y;
        int textX = x + TILE_SIZE + getHorizontalPaddingX(font);
        for (Component line : data.lines()) {
            FormattedCharSequence text = line.getVisualOrderText();
            font.drawInBatch(text, textX, textY, 0xFFFFFF, false, matrix, bufferSource,
                    Font.DisplayMode.NORMAL, 0, 15728880);
            textY += font.lineHeight + LINE_GAP;
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int contentHeight = getContentHeight();
        int frameInnerWidth = getFrameInnerWidth(font);
        int outerWidth = TILE_SIZE + frameInnerWidth + TILE_SIZE;
        int outerHeight = TILE_SIZE + contentHeight + TILE_SIZE;

        int leftSpanWidth = Math.max(0, (frameInnerWidth - TILE_SIZE) / 2);
        int rightSpanWidth = Math.max(0, frameInnerWidth - TILE_SIZE - leftSpanWidth);
        int middleX = x + TILE_SIZE + leftSpanWidth;
        int topY = y;
        int bottomY = y + outerHeight - TILE_SIZE;
        int leftX = x;
        int rightX = x + outerWidth - TILE_SIZE;
        int bodyY = y + TILE_SIZE;

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

    private int getFrameInnerWidth(Font font) {
        int maxWidth = getMaxLineWidth(font);
        return Math.max(TILE_SIZE, maxWidth + (getHorizontalPaddingX(font) * 2));
    }

    private int getContentHeight() {
        if (data.lines().isEmpty()) {
            return TILE_SIZE;
        }
        int linesHeight = (data.lines().size() * TEXT_LINE_HEIGHT) + ((data.lines().size() - 1) * LINE_GAP);
        return Math.max(TILE_SIZE, linesHeight + (CONTENT_PADDING_Y * 2));
    }

    private int getMaxLineWidth(Font font) {
        int maxWidth = 0;
        for (Component line : data.lines()) {
            maxWidth = Math.max(maxWidth, font.width(line));
        }
        return maxWidth;
    }

    private int getHorizontalPaddingX(Font font) {
        int maxWidth = getMaxLineWidth(font);
        if (maxWidth <= SHORT_TEXT_THRESHOLD) {
            return MIN_CONTENT_PADDING_X;
        }
        if (maxWidth <= MEDIUM_TEXT_THRESHOLD) {
            return MEDIUM_CONTENT_PADDING_X;
        }
        return MAX_CONTENT_PADDING_X;
    }

    private void tileHorizontalLeftOfMiddle(GuiGraphics guiGraphics, ResourceLocation texture, int middleX, int y, int width, boolean mirrorY) {
        if (width <= 0) {
            return;
        }

        int remaining = width;
        int currentRight = middleX;
        while (remaining > 0) {
            int segmentWidth = Math.min(TILE_SIZE, remaining);
            int x = currentRight - segmentWidth;
            int sourceU = TILE_SIZE - segmentWidth;
            blit(guiGraphics, texture, x, y, sourceU, 0, segmentWidth, TILE_SIZE, segmentWidth, TILE_SIZE, false, mirrorY);
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

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, x, y, 0.0F).setUv(u0, v0);
        buffer.addVertex(matrix, x, y + drawHeight, 0.0F).setUv(u0, v1);
        buffer.addVertex(matrix, x + drawWidth, y + drawHeight, 0.0F).setUv(u1, v1);
        buffer.addVertex(matrix, x + drawWidth, y, 0.0F).setUv(u1, v0);
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.buildOrThrow());
    }
}
