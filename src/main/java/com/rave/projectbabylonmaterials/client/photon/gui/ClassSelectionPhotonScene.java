package com.rave.projectbabylonmaterials.client.photon.gui;

import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.lowdragmc.photon.client.PhotonParticleManager;
import com.lowdragmc.photon.client.fx.IEffect;
import com.lowdragmc.photon.client.gameobject.emitter.PhotonParticleRenderType;
import com.lowdragmc.photon.client.gameobject.emitter.data.RendererSetting;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction3;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.rave.projectbabylonmaterials.config.PBMClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;


public final class ClassSelectionPhotonScene implements AutoCloseable {
    private final TrackedDummyWorld world;
    private final PhotonParticleManager particles = new PhotonParticleManager();
    private final Camera camera = new GuiCamera();
    private final PoseStack particlePose = new PoseStack();
    private final List<ParticleEmitter> emitters = new ArrayList<>(6);
    private final ParticleEmitter[] sparks = new ParticleEmitter[6];
    private ClassSelectionPhotonRibbons ribbons;
    private final Vector4f tint = new Vector4f();
    private int ticks;
    private int sparkAreaHeight;
    private boolean closed;

    public ClassSelectionPhotonScene(Level sourceLevel) {
        this.world = new TrackedDummyWorld(sourceLevel);
        world.setParticleManager(particles);
        particles.setLevel(world);
        IEffect effect = () -> world;
        try {
            ribbons = new ClassSelectionPhotonRibbons(world);
            boolean lite = PBMClientConfig.useLitePhotonEffects();
            for (int i = 0; i < sparks.length; i++) {
                int limit = i < 2 ? (lite ? 4 : 9) : (lite ? 1 : 2);
                sparks[i] = attach(ClassSelectionPhotonEffects.sparks(limit, i < 2), effect);
            }
            for (int i = 0; i < 60; i++) particles.tick();
        } catch (RuntimeException exception) {
            close();
            throw exception;
        }
    }

    private ParticleEmitter attach(ParticleEmitter emitter, IEffect effect) {
        emitters.add(emitter);
        emitter.emmit(effect, new Vector3f(), null, new Vector3f(1));
        return emitter;
    }

    public void tick() {
        if (!closed) {
            ticks++;
            particles.tick();
        }
    }

    public void render(GuiGraphics graphics, Frame frame, float partialTick) {
        if (closed || frame.width() <= 0 || frame.height() <= 0) return;
        if (sparkAreaHeight != frame.height()) {
            sparkAreaHeight = frame.height();
            for (int i = 0; i < sparks.length; i++) {
                sparks[i].config.shape.setScale(new NumberFunction3(
                        ClassSelectionPhotonMotion.sparkWidth(i < 2), frame.height(), 0));
            }
        }
        float time = ticks + Math.max(0, Math.min(1, partialTick));
        updateSparks(0, frame.centerX(), frame.mainBannerWidth(), frame.height(), frame.accent(), 1);
        updateSparks(1, frame.leftX(), frame.sideBannerWidth(), frame.height(), frame.leftAccent(), frame.sideOpacity());
        updateSparks(2, frame.rightX(), frame.sideBannerWidth(), frame.height(), frame.rightAccent(), frame.sideOpacity());

        graphics.flush();
        var previousFrustum = PhotonParticleRenderType.getFRUSTUM();
        var previousLayer = PhotonParticleRenderType.getLAYER();
        boolean previousBloom = PhotonParticleRenderType.bloomMark;
        try (PhotonGuiRenderState state = new PhotonGuiRenderState()) {
            PhotonParticleRenderType.bloomMark = false;
            PhotonParticleRenderType.prepareForParticleRendering(null);
            RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, frame.width(), -frame.height(), 0, -100, 100),
                    VertexSorting.ORTHOGRAPHIC_Z);
            PoseStack modelView = RenderSystem.getModelViewStack();
            modelView.pushPose();
            try {
                modelView.setIdentity();


                modelView.translate(0, 0, ClassSelectionPhotonMotion.CAMERA_DISTANCE);
                RenderSystem.applyModelViewMatrix();
                ribbons.render(frame, camera, time, partialTick);
                particles.render(particlePose, camera, Math.max(0, Math.min(1, partialTick)));
            } finally {
                modelView.popPose();
                RenderSystem.applyModelViewMatrix();
            }
        } finally {
            PhotonParticleRenderType.bloomMark = false;
            PhotonParticleRenderType.prepareForParticleRendering(previousFrustum);
            if (previousLayer == RendererSetting.Layer.Translucent) PhotonParticleRenderType.finishRender();
            PhotonParticleRenderType.bloomMark = previousBloom;
        }
    }

    private void updateSparks(int index, float x, float bannerWidth, int height, int color, float opacity) {
        for (int side = 0; side < 2; side++) {
            ParticleEmitter emitter = sparks[index * 2 + side];
            emitter.setPos(ClassSelectionPhotonMotion.sparkRail(x, bannerWidth, side), -height * 0.5F, 0);
            color(emitter, color, opacity * 0.8F);
        }
    }

    private void color(ParticleEmitter emitter, int rgb, float opacity) {
        tint.set(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F,
                (rgb & 255) / 255.0F, Math.max(0, Math.min(1, opacity)));
        emitter.setRGBAColor(tint);
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        if (ribbons != null) ribbons.close();
        for (ParticleEmitter emitter : emitters) emitter.remove(true);
        particles.clearAllParticles();
        world.setParticleManager(null);
        world.clear();
        emitters.clear();
    }

    private static final class GuiCamera extends Camera {
        private GuiCamera() { setPosition(0, 0, ClassSelectionPhotonMotion.CAMERA_DISTANCE); }
    }

    public record Frame(int width, int height, float centerX, float leftX, float rightX,
                        float mainBannerWidth, float sideBannerWidth,
                        int accent, int leftAccent, int rightAccent, float sideOpacity,
                        float pulseProgress, float pulseY) {
    }
}
