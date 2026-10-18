package com.rave.projectbabylonmaterials.client.photon.gui;

import com.lowdragmc.photon.client.gameobject.emitter.data.material.TextureMaterial;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.lowdragmc.photon.client.gameobject.emitter.trail.TrailConfig;
import com.lowdragmc.photon.client.gameobject.particle.TrailParticle;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.joml.Vector4f;


final class ClassSelectionPhotonRibbons implements AutoCloseable {
    private final ResourceLocation texture;
    private final TextureMaterial material;
    private final Ribbon[] columns = new Ribbon[6];
    private final Ribbon pulse;

    ClassSelectionPhotonRibbons(Level level) {
        NativeImage image = new NativeImage(2, 128, false);
        for (int y = 0; y < image.getHeight(); y++) {
            int alpha = Math.round(255 * ClassSelectionPhotonMotion.crossSectionAlpha(y / 127.0F));
            for (int x = 0; x < image.getWidth(); x++) image.setPixelRGBA(x, y, alpha << 24 | 0xFFFFFF);
        }
        DynamicTexture dynamic = new DynamicTexture(image);
        texture = Minecraft.getInstance().getTextureManager().register("class_selection_ribbon", dynamic);
        dynamic.setFilter(true, false);
        material = new TextureMaterial(texture);
        material.discardThreshold = 0.001F;
        try {
            for (int i = 0; i < columns.length; i++) columns[i] = new Ribbon(level);
            pulse = new Ribbon(level);
        } catch (RuntimeException exception) {
            Minecraft.getInstance().getTextureManager().release(texture);
            throw exception;
        }
    }

    void render(ClassSelectionPhotonScene.Frame frame, Camera camera, float time, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();

        RenderSystem.setShader(material::getShader);
        material.setupUniform();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        try {
            buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.PARTICLE);
            for (int column = 0; column < 3; column++) {
                float x = column == 0 ? frame.centerX() : column == 1 ? frame.leftX() : frame.rightX();
                int rgb = column == 0 ? frame.accent() : column == 1 ? frame.leftAccent() : frame.rightAccent();
                float opacity = column == 0 ? 1 : frame.sideOpacity() * 0.45F;
                for (int layer = 0; layer < 2; layer++) {
                    Ribbon ribbon = columns[column * 2 + layer];
                    ribbon.tint(rgb, opacity);
                    for (int i = 0; i < ribbon.points.length; i++) {
                        float t = i / (float) (ribbon.points.length - 1);
                        ribbon.set(i, x + ClassSelectionPhotonMotion.columnX(time, column, layer, t),
                                -frame.height() * (1 - t),
                                ClassSelectionPhotonMotion.columnWidth(time, column, layer, t),
                                ClassSelectionPhotonMotion.columnAlpha(time, column, layer, t));
                    }
                    ribbon.draw(buffer, camera, partialTick);
                }
            }
            float progress = Math.max(0, Math.min(1, frame.pulseProgress()));
            if (progress > 0 && progress < 1) {
                pulse.tint(frame.accent(), (1 - progress) * 0.4F);
                for (int i = 0; i < pulse.points.length; i++) {
                    float t = i / (float) (pulse.points.length - 1);
                    pulse.set(i, frame.centerX() + (t - 0.5F) * frame.width() * progress,
                            -frame.pulseY(), 1.3F, (float) Math.sin(Math.PI * t));
                }
                pulse.draw(buffer, camera, partialTick);
            }
            BufferUploader.drawWithShader(buffer.end());
        } catch (RuntimeException exception) {
            buffer.discard();
            throw exception;
        } finally {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
        }
    }

    @Override
    public void close() {
        for (Ribbon ribbon : columns) if (ribbon != null) ribbon.close();
        pulse.close();
        Minecraft.getInstance().getTextureManager().release(texture);
    }

    private static final class Ribbon {
        private final ParticleEmitter owner = new ParticleEmitter();
        private final TrailParticle trail;
        private final TrailParticle.Tail[] points = new TrailParticle.Tail[ClassSelectionPhotonMotion.TRAIL_POINTS];

        private Ribbon(Level level) {
            owner.setLevel(level);
            TrailConfig config = new TrailConfig();
            config.lights.setEnable(true);
            config.lights.setSkyLight(NumberFunction.constant(15));
            config.lights.setBlockLight(NumberFunction.constant(15));
            trail = new TrailParticle(owner, config, owner.getThreadSafeRandomSource());
            for (int i = 0; i < points.length; i++) {
                points[i] = new TrailParticle.Tail(new Vector3f(), 100);
                points[i].color = new Vector4f(1);
            }
            trail.setHeadPositionSupplier(partialTick -> points[points.length - 1].position);
        }

        private void tint(int rgb, float opacity) {
            owner.setRGBAColor(new Vector4f((rgb >> 16 & 255) / 255.0F, (rgb >> 8 & 255) / 255.0F,
                    (rgb & 255) / 255.0F, opacity));
        }

        private void set(int index, float x, float y, float width, float alpha) {
            points[index].position.set(x, y, 0);
            points[index].width = width;
            points[index].color.set(1, 1, 1, alpha);
        }

        private void draw(BufferBuilder buffer, Camera camera, float partialTick) {

            var tails = trail.getTails();
            tails.clear();
            for (var point : points) tails.add(point);
            trail.render(buffer, camera, partialTick);
        }

        private void close() {
            trail.setRemoved(true);
            trail.getTails().clear();
            trail.getRawTails().clear();
            owner.remove(true);
        }
    }
}
