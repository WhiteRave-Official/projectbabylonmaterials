package com.rave.projectbabylonmaterials.client.photon.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;


final class PhotonGuiRenderState implements AutoCloseable {
    private final Matrix4f projection = new Matrix4f(RenderSystem.getProjectionMatrix());
    private final VertexSorting sorting = RenderSystem.getVertexSorting();
    private final ShaderInstance shader = RenderSystem.getShader();
    private final float[] color = RenderSystem.getShaderColor().clone();
    private final boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
    private final boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
    private final boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
    private final boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
    private final int srcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
    private final int dstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
    private final int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
    private final int dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
    private final int[] textures = {RenderSystem.getShaderTexture(0), RenderSystem.getShaderTexture(1), RenderSystem.getShaderTexture(2)};

    @Override
    public void close() {
        RenderSystem.setProjectionMatrix(projection, sorting);
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
        for (int i = 0; i < textures.length; i++) RenderSystem.setShaderTexture(i, textures[i]);
        RenderSystem.blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
        RenderSystem.depthMask(depthMask);
        if (depthTest) RenderSystem.enableDepthTest(); else RenderSystem.disableDepthTest();
        if (blend) RenderSystem.enableBlend(); else RenderSystem.disableBlend();
        if (cull) RenderSystem.enableCull(); else RenderSystem.disableCull();
    }
}
