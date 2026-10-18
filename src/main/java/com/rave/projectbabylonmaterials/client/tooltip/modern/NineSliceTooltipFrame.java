package com.rave.projectbabylonmaterials.client.tooltip.modern;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rave.projectbabylonmaterials.tooltip.TooltipFrameStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class NineSliceTooltipFrame {
    private static final int TILE_SIZE = 16;

    private NineSliceTooltipFrame() {
    }

    public static void render(GuiGraphics guiGraphics, int x, int y, int outerWidth, int outerHeight,
                              TooltipFrameStyle style) {
        int frameInnerWidth = outerWidth - TILE_SIZE * 2;
        int contentHeight = outerHeight - TILE_SIZE * 2;

        int leftSpanWidth = Math.max(0, (frameInnerWidth - TILE_SIZE) / 2);
        int rightSpanWidth = Math.max(0, frameInnerWidth - TILE_SIZE - leftSpanWidth);
        int middleX = x + TILE_SIZE + leftSpanWidth;
        int topY = y;
        int bottomY = y + outerHeight - TILE_SIZE;
        int leftX = x;
        int rightX = x + outerWidth - TILE_SIZE;
        int bodyY = y + TILE_SIZE;

        blit(guiGraphics, style.topLeftCorner(), leftX, topY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, style.topRightCorner(), rightX, topY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, style.bottomLeftCorner(), leftX, bottomY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, style.bottomRightCorner(), rightX, bottomY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);

        blit(guiGraphics, style.middle(), middleX, bottomY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, false);
        blit(guiGraphics, style.middle(), middleX, topY, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE, false, true);

        tileHorizontalLeftOfMiddle(guiGraphics, style.horizontalEdge(), middleX, bottomY, leftSpanWidth, false);
        tileHorizontalRightOfMiddle(guiGraphics, style.horizontalEdge(), middleX + TILE_SIZE, bottomY, rightSpanWidth, false);
        tileHorizontalLeftOfMiddle(guiGraphics, style.horizontalEdge(), middleX, topY, leftSpanWidth, true);
        tileHorizontalRightOfMiddle(guiGraphics, style.horizontalEdge(), middleX + TILE_SIZE, topY, rightSpanWidth, true);

        tileVertical(guiGraphics, style.verticalEdge(), leftX, bodyY, contentHeight, false);
        tileVertical(guiGraphics, style.verticalEdge(), rightX, bodyY, contentHeight, true);
    }

    private static void tileHorizontalLeftOfMiddle(GuiGraphics guiGraphics, ResourceLocation texture, int middleX, int y, int width, boolean mirrorY) {
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

    private static void tileHorizontalRightOfMiddle(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, boolean mirrorY) {
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

    private static void tileVertical(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int height, boolean mirrorX) {
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

    private static void blit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int sourceU, int sourceV,
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
