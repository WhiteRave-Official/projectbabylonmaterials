package com.rave.projectbabylonmaterials.client.photon;

import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import com.lowdragmc.photon.client.gameobject.emitter.IParticleEmitter;
import com.lowdragmc.photon.client.gameobject.emitter.data.RendererSetting;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction3;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.lowdragmc.photon.client.gameobject.particle.TileParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

import static com.rave.projectbabylonmaterials.client.photon.PhotonEmitterFactory.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonTextures.texture;


public final class LightBreachColumnVisual implements AutoCloseable {
    private final ClientLevel level;
    private final Vec3 base;
    private final float radius;
    private final float height;
    private final ParticleEmitter seal;
    private ParticleEmitter shell;
    private ParticleEmitter core;
    private boolean visible;
    private boolean closed;
    private long nextVisibilityCheck;

    LightBreachColumnVisual(ClientLevel level, Vec3 base, float radius, float height) {
        this.level = level;
        this.base = base;
        this.radius = radius;
        this.height = height;
        this.seal = createTrailEmitter(texture("light_breach_seal.png"), radius * 2.7F,
                600, 0xE1FFE5A0, 0, 0, 0, 0, 0, false, false);
        seal.setName("light_breach_fixed_seal");

        seal.config.setStartRotation(new NumberFunction3(0, 0, 0));
        seal.config.rotationOverLifetime.setEnable(false);
        seal.config.renderer.setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        seal.emmit(new StaticLevelEffect(level), toVector(base.add(0, 0.01D, 0)), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public void update(float sealOpacity, int activeAge, int fadeIn, int active, int fadeOut) {
        if (closed) return;
        long now = level.getGameTime();
        if (now >= nextVisibilityCheck) {
            visible = shouldRenderPointEffect(level, base.add(0, height * 0.35D, 0));
            nextVisibilityCheck = now + 6;
        }
        seal.setVisible(visible);
        seal.setRGBAColor(new Vector4f(1, 1, 1, Mth.clamp(sealOpacity, 0, 1)));
        int duration = fadeIn + active + fadeOut;
        if (activeAge >= 0 && activeAge < duration && shell == null && visible) {
            shell = cylinder(now - activeAge, fadeIn, active, fadeOut, radius * 2.0F, 0xAFFFF0B4);
            if (!useLitePhotonEffects()) {
                core = cylinder(now - activeAge, fadeIn, active, fadeOut, radius * 0.70F, 0xDCFFFFFF);
            }
        }
        if (shell != null) shell.setVisible(visible);
        if (core != null) core.setVisible(visible);
    }

    private ParticleEmitter cylinder(long activeSince, int fadeIn, int active, int fadeOut,
                                     float diameter, int color) {
        ParticleEmitter emitter = createColumnEmitter(fadeIn + active + fadeOut);
        ParticleConfig config = emitter.config;
        emitter.setName("light_breach_model_column");
        config.setStartSize(new NumberFunction3(diameter, height, diameter));
        config.setStartRotation(new NumberFunction3(0, 0, 0));
        config.setStartColor(NumberFunction.color(color));
        config.renderer.setRenderMode(RendererSetting.Particle.Mode.Model);
        config.renderer.setModel(new IModelRenderer(LightBreachModels.CYLINDER));
        config.renderer.setShade(false);
        config.renderer.setUseBlockUV(false);
        applySharedMaterial(config, texture("light_breach_cylinder.png"), color, false);
        config.sizeOverLifetime.setEnable(true);
        config.sizeOverLifetime.setSize(new NumberFunction3(
                LightBreachPhotonTimeline.size(fadeIn, active, fadeOut, 0.15F),
                LightBreachPhotonTimeline.size(fadeIn, active, fadeOut, 0),
                LightBreachPhotonTimeline.size(fadeIn, active, fadeOut, 0.15F)));
        config.colorOverLifetime.setEnable(true);
        config.colorOverLifetime.setColor(LightBreachPhotonTimeline.opacity(fadeIn, active, fadeOut));

        emitter.emmit(new StaticLevelEffect(level), toVector(base), IDENTITY_ROTATION, UNIT_SCALE);
        int age = (int) Math.max(0, level.getGameTime() - activeSince);
        emitter.emitParticle(new AlignedParticle(emitter, config, emitter.getThreadSafeRandomSource(), age));
        return emitter;
    }

    static ParticleEmitter createColumnEmitter(int lifetime) {


        ParticleEmitter emitter = new ParticleEmitter();
        ParticleConfig config = emitter.config;
        config.setDuration(1);
        config.setLooping(false);
        config.setMaxParticles(1);
        config.setStartLifetime(NumberFunction.constant(lifetime));
        config.setStartSpeed(NumberFunction.constant(0));
        config.emission.setEmissionRate(NumberFunction.constant(0));
        config.emission.getBursts().clear();
        config.shape.setShape(new com.lowdragmc.photon.client.gameobject.emitter.data.shape.Dot());
        return emitter;
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        seal.remove(true);
        if (shell != null) shell.remove(true);
        if (core != null) core.remove(true);
    }


    private static final class AlignedParticle extends TileParticle {
        private AlignedParticle(IParticleEmitter emitter, ParticleConfig config, RandomSource random, int age) {
            super(emitter, config, random);
            this.age = age;
            this.t = (float) age / Math.max(1, getLifetime());
            update();
            updateOrigin();
        }
    }
}
