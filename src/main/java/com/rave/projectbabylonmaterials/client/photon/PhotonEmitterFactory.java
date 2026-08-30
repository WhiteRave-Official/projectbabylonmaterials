package com.rave.projectbabylonmaterials.client.photon;

import com.lowdragmc.photon.client.fx.IEffect;
import com.lowdragmc.lowdraglib.utils.GradientColor;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.Gradient;
import com.lowdragmc.photon.client.gameobject.emitter.data.VelocityOverLifetimeSetting;
import com.lowdragmc.photon.client.gameobject.emitter.data.material.TextureMaterial;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction3;
import com.lowdragmc.photon.client.gameobject.emitter.data.shape.Cone;
import com.lowdragmc.photon.client.gameobject.emitter.data.shape.Dot;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.client.shadow.ShadowFormClientState;
import com.rave.projectbabylonmaterials.config.PBMClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.rave.projectbabylonmaterials.client.photon.PhotonEffectConstants.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonTextures.*;

final class PhotonEmitterFactory {
    static ParticleEmitter createBreathEmitter() {
        ParticleEmitter emitter = new ParticleEmitter();
        ParticleConfig config = emitter.config;

        emitter.setName("dragon_descend_breath");
        config.setDuration(1);
        config.setLooping(false);
        config.setMaxParticles(photonCount(BREATH_PARTICLE_COUNT));
        config.setStartLifetime(NumberFunction.constant(10));
        config.setStartSpeed(NumberFunction.constant(0.9F));
        config.setStartSize(new NumberFunction3(0.42F, 0.42F, 0.42F));
        config.setStartRotation(new NumberFunction3(0.0F, 0.0F, 0.0F));
        config.setStartColor(NumberFunction.color(0xFFDBC3FF));
        config.emission.setEmissionRate(NumberFunction.constant(0.0F));

        var burst = new com.lowdragmc.photon.client.gameobject.emitter.data.EmissionSetting.Burst();
        burst.time = 0;
        burst.cycles = 1;
        burst.interval = 1;
        burst.probability = 1.0F;
        burst.setCount(NumberFunction.constant(photonCount(BREATH_PARTICLE_COUNT)));
        config.emission.getBursts().add(burst);

        Cone cone = new Cone();
        cone.setRadius(0.2F);
        cone.setRadiusThickness(0.0F);
        cone.setAngle(22.0F);
        cone.setArc(360.0F);
        config.shape.setShape(cone);
        config.shape.setPosition(new NumberFunction3(0.0F, 0.0F, 0.0F));

        applySharedMaterial(config, PORTAL_TEXTURE, 0xFFDBC3FF);

        config.velocityOverLifetime.setEnable(true);
        config.velocityOverLifetime.setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
        config.velocityOverLifetime.setLinear(new NumberFunction3(0.0F, 0.0F, 0.0F));
        config.velocityOverLifetime.setSpeedModifier(NumberFunction.constant(0.92F));

        config.rotationOverLifetime.setEnable(true);
        config.rotationOverLifetime.setRoll(NumberFunction.constant(36.0F));

        return emitter;
    }

    static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                      double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F);
    }

    static ParticleEmitter createTrailEmitterNoBloom(ResourceLocation texture, float size, int lifetime, int color,
                                                             double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitterNoBloom(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F);
    }

    static ParticleEmitter createTrailEmitterNoBloom(ResourceLocation texture, float size, int lifetime, int color,
                                                             double vx, double vy, double vz, float rollPerTickDegrees,
                                                             float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, false);
    }

    static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                      double vx, double vy, double vz, float rollPerTickDegrees,
                                                      float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, true);
    }

    static ParticleEmitter createLingeringTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                               double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F, true, false);
    }

    static ParticleEmitter createLingeringTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                               double vx, double vy, double vz, float rollPerTickDegrees,
                                                               float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, true, false);
    }

    static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                      double vx, double vy, double vz, float rollPerTickDegrees,
                                                      float startRollDegrees, boolean bloom) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, bloom, true);
    }

    static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                      double vx, double vy, double vz, float rollPerTickDegrees,
                                                      float startRollDegrees, boolean bloom, boolean fadeOverLifetime) {
        ParticleEmitter emitter = new ParticleEmitter();
        ParticleConfig config = emitter.config;

        emitter.setName("projectile_trail");
        config.setDuration(1);
        config.setLooping(false);
        config.setMaxParticles(1);
        config.setStartLifetime(NumberFunction.constant(lifetime));
        config.setStartSpeed(NumberFunction.constant(0.0F));
        config.setStartSize(new NumberFunction3(size, size, size));
        config.setStartRotation(new NumberFunction3(startRollDegrees, 0.0F, 0.0F));
        config.setStartColor(NumberFunction.color(color));
        config.emission.setEmissionRate(NumberFunction.constant(0.0F));

        var burst = new com.lowdragmc.photon.client.gameobject.emitter.data.EmissionSetting.Burst();
        burst.time = 0;
        burst.cycles = 1;
        burst.interval = 1;
        burst.probability = 1.0F;
        burst.setCount(NumberFunction.constant(1));
        config.emission.getBursts().add(burst);

        config.shape.setShape(new Dot());
        config.shape.setPosition(new NumberFunction3(0.0F, 0.0F, 0.0F));

        applySharedMaterial(config, texture, color, bloom);

        config.velocityOverLifetime.setEnable(true);
        config.velocityOverLifetime.setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
        config.velocityOverLifetime.setLinear(new NumberFunction3(vx * 20.0D, vy * 20.0D, vz * 20.0D));
        config.velocityOverLifetime.setSpeedModifier(NumberFunction.constant(1.0F));

        if (fadeOverLifetime) {
            config.colorOverLifetime.setEnable(true);
            Gradient fadeGradient = new Gradient();
            fadeGradient.getGradientColor().deserializeNBT(new GradientColor(color, color & 0x00FFFFFF).serializeNBT());
            config.colorOverLifetime.setColor(fadeGradient);
        }

        config.rotationOverLifetime.setEnable(true);
        config.rotationOverLifetime.setRoll(NumberFunction.constant(rollPerTickDegrees));

        return emitter;
    }
    static void applySharedMaterial(ParticleConfig config, ResourceLocation texture, int color) {
        applySharedMaterial(config, texture, color, false);
    }

    static void applySharedMaterial(ParticleConfig config, ResourceLocation texture, int color, boolean bloom) {
        config.material.setCull(false);
        config.material.setDepthMask(false);
        config.material.setDepthTest(true);
        TextureMaterial textureMaterial = new TextureMaterial(texture);
        textureMaterial.discardThreshold = 0.02F;
        config.material.setMaterial(textureMaterial);
        config.renderer.setBloomEffect(false);
    }

}
