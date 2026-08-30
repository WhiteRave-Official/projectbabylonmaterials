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
import static com.rave.projectbabylonmaterials.client.photon.PhotonEmitterFactory.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonTextures.*;

final class PhotonProjectileEffects {
    public static void spawnDragonDescendFlight(Entity projectile, Vec3 movement) {
        spawnDragonDescendFlight(projectile, movement, TRAIL_VISUAL_LIFETIME);
    }

    public static void spawnDragonDescendFlight(Entity projectile, Vec3 movement, int trailVisualLifetimeTicks) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 breathDirection = new Vec3(normalized.x, normalized.y + BREATH_DOWNWARD_BIAS, normalized.z).normalize();
        Vec3 right = horizontalRight(normalized);

        Vec3 origin = projectile.position().subtract(normalized.scale(0.35D)).add(0.0D, 0.15D, 0.0D);
        Quaternionf breathRotation = quaternionFromDirection(breathDirection);
        ParticleEmitter breathEmitter = createBreathEmitter();
        breathEmitter.emmit(effect, toVector(origin), breathRotation, UNIT_SCALE);

        if ((projectile.tickCount % photonInterval(TRAIL_VISUAL_INTERVAL)) != 0) {
            return;
        }

        Vec3 trailCenter = projectile.position().subtract(normalized.scale(0.9D)).add(0.0D, 0.05D, 0.0D);
        spawnTrailSegment(effect, trailCenter, right, normalized, trailVisualLifetimeTicks, projectile.tickCount);
    }

    public static void spawnDragonDescendLingeringTrail(ClientLevel level, Vec3 center, Vec3 forward, int pulseLifetimeTicks) {
        if (forward.lengthSqr() < 1.0E-6D || !shouldRenderPointEffect(level, center)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = forward.normalize();
        Vec3 right = horizontalRight(normalized);
        int lifetime = Math.max(6, pulseLifetimeTicks);

        if (!useLitePhotonEffects()) {
            spawnRiftLingeringPulse(effect, center, right, normalized, lifetime, (int) level.getGameTime());
            return;
        }

        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.74F, lifetime, 0xD8D8C0FF, 0.0D, 0.002D, 0.0D, 5.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, new Vector3f(2.25F, 1.0F, 1.35F));
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.42F, lifetime, 0xE8F3EAFF, right.x * 0.025D, 0.012D, right.z * 0.025D, 18.0F)
                .emmit(effect, toVector(center.add(right.scale(0.85D))), IDENTITY_ROTATION, UNIT_SCALE);
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.42F, lifetime, 0xC8E8D8FF, -right.x * 0.025D, 0.012D, -right.z * 0.025D, -18.0F)
                .emmit(effect, toVector(center.add(right.scale(-0.85D))), IDENTITY_ROTATION, UNIT_SCALE);
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.28F, lifetime, 0xFFFFFFFF, normalized.x * -0.02D, 0.018D, normalized.z * -0.02D, 28.0F)
                .emmit(effect, toVector(center.add(normalized.scale(-0.35D)).add(0.0D, 0.04D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnEnderProjectileFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        if ((projectile.tickCount % photonInterval(ENDER_PROJECTILE_PARTICLE_INTERVAL)) != 0) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 center = projectile.position().subtract(normalized.scale(0.32D));
        Vec3 right = horizontalRight(normalized);
        Vec3 up = verticalAxis(normalized, right);

        float angle = projectile.tickCount * 0.55F;
        Vec3 portalOffset = right.scale(Math.cos(angle) * 0.14D).add(up.scale(Math.sin(angle) * 0.14D));
        Vec3 breathOffset = portalOffset.scale(-0.85D);

        createTrailEmitter(PORTAL_TEXTURE, 0.26F, 12, 0xFFE6D6FF, 0.0D, 0.01D, 0.0D, 22.0F)
                .emmit(effect, toVector(center.add(portalOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(PORTAL_TEXTURE, 0.22F, 10, 0xFFBA7BFF, 0.0D, 0.015D, 0.0D, -18.0F)
                .emmit(effect, toVector(center.add(breathOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(PORTAL_TEXTURE, 0.18F, 9, 0xFFFFFFFF, 0.0D, 0.005D, 0.0D, 30.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnEnderProjectileImpact(Entity projectile, Vec3 hitPos) {
        if (!(projectile.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderPointEffect(level, hitPos)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < photonCount(24); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(24);
            double speed = 0.12D + ((i & 1) * 0.035D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.015D + ((i % 3) * 0.01D);
            int color = (i % 4 == 0) ? 0xFFFFFFFF : ((i & 1) == 0 ? 0xFFE6D6FF : 0xFFBA7BFF);
            float size = (i % 4 == 0) ? 0.24F : 0.2F;
            float roll = ((i & 1) == 0) ? 30.0F : -30.0F;
            createTrailEmitter(PORTAL_TEXTURE, size, 12, color, vx, vy, vz, roll)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(PORTAL_TEXTURE, 0.34F, 10, 0xFFFFFFFF, 0.0D, 0.02D, 0.0D, 34.0F)
                .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.35F, 1.0F, 1.35F));
    }

    public static void spawnHolyProjectileFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        if ((projectile.tickCount % photonInterval(HOLY_PROJECTILE_PARTICLE_INTERVAL)) != 0) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 center = projectile.position().subtract(normalized.scale(0.3D));
        Vec3 right = horizontalRight(normalized);
        Vec3 up = verticalAxis(normalized, right);

        float angle = projectile.tickCount * 0.45F;
        Vec3 spiralOffset = right.scale(Math.cos(angle) * 0.1D).add(up.scale(Math.sin(angle) * 0.1D));
        Vec3 oppositeOffset = spiralOffset.scale(-1.0D);

        createTrailEmitter(HOLY_TEXTURE, 0.24F, 12, 0xFFFFFFFF, 0.0D, 0.005D, 0.0D, 20.0F)
                .emmit(effect, toVector(center.add(spiralOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(HEAL_TEXTURE, 0.2F, 10, 0xFFFFF1C8, 0.0D, 0.01D, 0.0D, -18.0F)
                .emmit(effect, toVector(center.add(oppositeOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(HOLY_TEXTURE, 0.16F, 9, 0xFFFFFFFF, 0.0D, 0.0D, 0.0D, 26.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnHolyProjectileImpact(Entity projectile, Vec3 hitPos) {
        if (!(projectile.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderPointEffect(level, hitPos)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < photonCount(14); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(14);
            double speed = 0.1D + ((i & 1) * 0.03D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.02D + ((i % 2) * 0.008D);
            ResourceLocation texture = (i % 3 == 0) ? HEAL_TEXTURE : HOLY_TEXTURE;
            int color = (i % 3 == 0) ? 0xFFFFF1C8 : 0xFFFFFFFF;
            createTrailEmitter(texture, 0.22F, 12, color, vx, vy, vz, (i & 1) == 0 ? 26.0F : -26.0F)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(HOLY_TEXTURE, 0.3F, 10, 0xFFFFFFFF, 0.0D, 0.015D, 0.0D, 32.0F)
                .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.25F, 1.0F, 1.25F));
    }

    public static void spawnIceProjectileFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        if ((projectile.tickCount % photonInterval(ICE_PROJECTILE_PARTICLE_INTERVAL)) != 0) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 center = projectile.position().subtract(normalized.scale(0.35D));
        Vec3 right = horizontalRight(normalized);
        Vec3 up = verticalAxis(normalized, right);

        float angle = projectile.tickCount * 0.6F;
        Vec3 spiralOffset = right.scale(Math.cos(angle) * 0.12D).add(up.scale(Math.sin(angle) * 0.12D));
        Vec3 oppositeOffset = spiralOffset.scale(-1.0D);

        createTrailEmitter(SNOWFLAKE_TEXTURE, 0.22F, 12, 0xFFFFFFFF, 0.0D, 0.01D, 0.0D, 22.0F)
                .emmit(effect, toVector(center.add(spiralOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(SNOW_TEXTURE, 0.2F, 11, 0xFFD8F4FF, 0.0D, 0.0D, 0.0D, -18.0F)
                .emmit(effect, toVector(center.add(oppositeOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(SNOWFLAKE_TEXTURE, 0.16F, 9, 0xFFFFFFFF, 0.0D, 0.004D, 0.0D, 28.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnIceProjectileImpact(Entity projectile, Vec3 hitPos) {
        if (!(projectile.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderPointEffect(level, hitPos)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < photonCount(24); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(24);
            double speed = 0.11D + ((i & 1) * 0.025D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.015D + ((i % 3) * 0.008D);
            ResourceLocation texture = (i % 2 == 0) ? SNOWFLAKE_TEXTURE : SNOW_TEXTURE;
            int color = (i % 2 == 0) ? 0xFFFFFFFF : 0xFFD8F4FF;
            createTrailEmitter(texture, 0.22F, 12, color, vx, vy, vz, (i & 1) == 0 ? 24.0F : -24.0F)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(SNOWFLAKE_TEXTURE, 0.3F, 10, 0xFFFFFFFF, 0.0D, 0.015D, 0.0D, 30.0F)
                .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.25F, 1.0F, 1.25F));
    }

    public static void spawnFireProjectileFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        if ((projectile.tickCount % photonInterval(FIRE_PROJECTILE_PARTICLE_INTERVAL)) != 0) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 center = projectile.position().subtract(normalized.scale(0.35D));
        Vec3 right = horizontalRight(normalized);
        Vec3 up = verticalAxis(normalized, right);

        float angle = projectile.tickCount * 0.6F;
        Vec3 flameOffset = right.scale(Math.cos(angle) * 0.14D).add(up.scale(Math.sin(angle) * 0.14D));
        Vec3 smokeOffset = flameOffset.scale(-0.75D);

        createTrailEmitter(FIRE_TEXTURE_1, 0.24F, 11, 0xFFFFC95A, 0.0D, 0.012D, 0.0D, 20.0F)
                .emmit(effect, toVector(center.add(flameOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(FIRE_TEXTURE_2, 0.2F, 10, 0xFFFF6A1E, 0.0D, 0.016D, 0.0D, -18.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(SMOKE_TEXTURE_1, 0.22F, 13, 0xFF1C1C1C, 0.0D, 0.004D, 0.0D, 12.0F)
                .emmit(effect, toVector(center.add(smokeOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(SMOKE_TEXTURE_2, 0.16F, 11, 0xFF2B2B2B, 0.0D, 0.002D, 0.0D, -10.0F)
                .emmit(effect, toVector(center.add(smokeOffset.scale(0.55D))), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnFireProjectileImpact(Entity projectile, Vec3 hitPos) {
        if (!(projectile.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderPointEffect(level, hitPos)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < photonCount(24); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(24);
            double speed = 0.18D + ((i & 1) * 0.05D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.03D + ((i % 3) * 0.015D);
            ResourceLocation texture = (i % 2 == 0) ? FIRE_TEXTURE_1 : FIRE_TEXTURE_2;
            int color = (i % 2 == 0) ? 0xFFFFC95A : 0xFFFF6A1E;
            createTrailEmitter(texture, 0.3F, 14, color, vx, vy, vz, (i & 1) == 0 ? 24.0F : -24.0F)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        for (int i = 0; i < photonCount(14); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(14);
            double speed = 0.1D + ((i & 1) * 0.03D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            createTrailEmitter((i & 1) == 0 ? SMOKE_TEXTURE_1 : SMOKE_TEXTURE_2, 0.26F, 16, 0xFF242424, vx, 0.012D, vz, (i & 1) == 0 ? 12.0F : -12.0F)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(FIRE_TEXTURE_1, 0.32F, 10, 0xFFFFE08A, 0.0D, 0.02D, 0.0D, 30.0F)
                .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.25F, 1.0F, 1.25F));
    }
    public static void spawnGoldenProjectileFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        if ((projectile.tickCount % photonInterval(GOLDEN_PROJECTILE_PARTICLE_INTERVAL)) != 0) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 center = projectile.position().subtract(normalized.scale(0.3D));
        Vec3 right = horizontalRight(normalized);
        Vec3 up = verticalAxis(normalized, right);

        float angle = projectile.tickCount * 0.58F;
        Vec3 spiralOffset = right.scale(Math.cos(angle) * 0.12D).add(up.scale(Math.sin(angle) * 0.12D));
        Vec3 oppositeOffset = spiralOffset.scale(-0.95D);

        createTrailEmitter(GOLDEN_TEXTURE_2, 0.24F, 12, 0xFFFFF1B0, 0.0D, 0.008D, 0.0D, 24.0F)
                .emmit(effect, toVector(center.add(spiralOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(GOLDEN_TEXTURE, 0.2F, 10, 0xFFF5D46C, 0.0D, 0.012D, 0.0D, -18.0F)
                .emmit(effect, toVector(center.add(oppositeOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(GOLDEN_TEXTURE, 0.16F, 9, 0xFFFFFFFF, 0.0D, 0.002D, 0.0D, 28.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnGoldenProjectileImpact(Entity projectile, Vec3 hitPos) {
        if (!(projectile.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderPointEffect(level, hitPos)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < photonCount(14); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(14);
            double speed = 0.105D + ((i & 1) * 0.028D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.018D + ((i % 2) * 0.01D);
            ResourceLocation texture = (i % 3 == 0) ? GOLDEN_TEXTURE_2 : GOLDEN_TEXTURE;
            int color = (i % 3 == 0) ? 0xFFFFF1B0 : ((i & 1) == 0 ? 0xFFF5D46C : 0xFFFFFFFF);
            createTrailEmitter(texture, 0.22F, 12, color, vx, vy, vz, (i & 1) == 0 ? 26.0F : -26.0F)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(GOLDEN_TEXTURE_2, 0.32F, 10, 0xFFFFFFFF, 0.0D, 0.015D, 0.0D, 32.0F)
                .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.24F, 1.0F, 1.24F));
    }

    public static void spawnDiamondProjectileFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        if ((projectile.tickCount % photonInterval(DIAMOND_PROJECTILE_PARTICLE_INTERVAL)) != 0) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 center = projectile.position().subtract(normalized.scale(0.32D));
        Vec3 right = horizontalRight(normalized);
        Vec3 up = verticalAxis(normalized, right);

        float angle = projectile.tickCount * 0.62F;
        Vec3 spiralOffset = right.scale(Math.cos(angle) * 0.11D).add(up.scale(Math.sin(angle) * 0.11D));
        Vec3 oppositeOffset = spiralOffset.scale(-1.0D);

        createTrailEmitter(DIAMOND_TEXTURE_2, 0.22F, 12, 0xFFE6FFFF, 0.0D, 0.01D, 0.0D, 22.0F)
                .emmit(effect, toVector(center.add(spiralOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(DIAMOND_TEXTURE, 0.2F, 10, 0xFF7FE3FF, 0.0D, 0.014D, 0.0D, -20.0F)
                .emmit(effect, toVector(center.add(oppositeOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(DIAMOND_TEXTURE, 0.16F, 9, 0xFFFFFFFF, 0.0D, 0.004D, 0.0D, 28.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnDiamondProjectileImpact(Entity projectile, Vec3 hitPos) {
        if (!(projectile.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderPointEffect(level, hitPos)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < photonCount(24); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(24);
            double speed = 0.18D + ((i & 1) * 0.05D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.015D + ((i % 3) * 0.009D);
            ResourceLocation texture = (i % 4 == 0) ? DIAMOND_TEXTURE_2 : DIAMOND_TEXTURE;
            int color = (i % 4 == 0) ? 0xFFFFFFFF : ((i & 1) == 0 ? 0xFFB7F6FF : 0xFF7FE3FF);
            createTrailEmitter(texture, 0.22F, 12, color, vx, vy, vz, (i & 1) == 0 ? 28.0F : -28.0F)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(DIAMOND_TEXTURE_2, 0.32F, 10, 0xFFFFFFFF, 0.0D, 0.015D, 0.0D, 34.0F)
                .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.26F, 1.0F, 1.26F));
    }


    private static void spawnTrailSegment(StaticLevelEffect effect, Vec3 center, Vec3 right, Vec3 forward, int trailVisualLifetimeTicks, int tick) {
        if (useLitePhotonEffects()) {
            spawnLiteTrailSegment(effect, center, right, forward, trailVisualLifetimeTicks);
            return;
        }

        spawnRiftTrailSegment(effect, center, right, forward, tick);
    }

    private static void spawnRiftTrailSegment(StaticLevelEffect effect, Vec3 center, Vec3 right, Vec3 forward, int tick) {
        Vec3 back = forward.scale(-0.35D);
        float swirl = tick * 0.42F;

        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.58F, 22, 0xC82D154A, 0.0D, 0.004D, 0.0D, 8.0F, swirl * Mth.RAD_TO_DEG)
                .emmit(effect, toVector(center.add(back)), IDENTITY_ROTATION, new Vector3f(2.55F, 0.8F, 0.62F));
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.34F, 18, 0xFFE8D8FF, 0.0D, 0.012D, 0.0D, -18.0F, -swirl * Mth.RAD_TO_DEG)
                .emmit(effect, toVector(center.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.45F, 1.0F, 0.42F));
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.22F, 16, 0xFFFFFFFF, 0.0D, 0.018D, 0.0D, 30.0F, swirl * 1.3F * Mth.RAD_TO_DEG)
                .emmit(effect, toVector(center.add(forward.scale(0.18D)).add(0.0D, 0.14D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);

        for (int i = 0; i < 6; i++) {
            double angle = swirl + (Mth.TWO_PI * i / 6.0D);
            double wave = Math.cos(angle);
            double lift = Math.sin(angle) * 0.28D;
            Vec3 offset = right.scale(wave * 0.95D).add(forward.scale(-0.2D + (i % 3) * 0.18D)).add(0.0D, 0.18D + lift, 0.0D);
            Vec3 tangent = right.scale(-Math.sin(angle) * 0.045D).add(forward.scale(-0.018D));
            int color = (i & 1) == 0 ? 0xFFE8D8FF : 0xFF9F5CFF;
            createLingeringTrailEmitter(PORTAL_TEXTURE, 0.24F, 17, color, tangent.x, 0.012D + Math.max(0.0D, lift) * 0.01D, tangent.z, (i & 1) == 0 ? 34.0F : -34.0F, (float) Math.toDegrees(angle))
                    .emmit(effect, toVector(center.add(offset)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        for (int i = 0; i < 4; i++) {
            double side = i < 2 ? 1.0D : -1.0D;
            double depth = -0.55D + (i % 2) * 0.45D;
            Vec3 edge = center.add(right.scale(side * (1.15D + (i % 2) * 0.25D))).add(forward.scale(depth)).add(0.0D, 0.06D, 0.0D);
            createLingeringTrailEmitter(PORTAL_TEXTURE, 0.18F, 14, 0xCCF3EAFF, right.x * side * 0.075D, 0.02D, right.z * side * 0.075D, (float) (side * 42.0D))
                    .emmit(effect, toVector(edge), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static void spawnRiftLingeringPulse(StaticLevelEffect effect, Vec3 center, Vec3 right, Vec3 forward, int lifetime, int tick) {
        float swirl = tick * 0.28F;

        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.54F, lifetime, 0xB82D154A, 0.0D, 0.001D, 0.0D, 5.0F, swirl * Mth.RAD_TO_DEG)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, new Vector3f(2.35F, 0.75F, 0.72F));
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.28F, lifetime, 0xD8E8D8FF, 0.0D, 0.006D, 0.0D, -12.0F, -swirl * Mth.RAD_TO_DEG)
                .emmit(effect, toVector(center.add(0.0D, 0.09D, 0.0D)), IDENTITY_ROTATION, new Vector3f(1.25F, 1.0F, 0.45F));

        for (int i = 0; i < 4; i++) {
            double angle = swirl + (Mth.HALF_PI * i);
            double side = Math.cos(angle);
            Vec3 pos = center.add(right.scale(side * 0.95D)).add(forward.scale(Math.sin(angle) * 0.24D)).add(0.0D, 0.16D + Math.sin(angle * 1.7D) * 0.08D, 0.0D);
            Vec3 velocity = right.scale(-Math.sin(angle) * 0.035D).add(forward.scale(-0.015D));
            createLingeringTrailEmitter(PORTAL_TEXTURE, 0.2F, lifetime, 0xD8F3EAFF, velocity.x, 0.012D, velocity.z, (i & 1) == 0 ? 24.0F : -24.0F, (float) Math.toDegrees(angle))
                    .emmit(effect, toVector(pos), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static void spawnLiteTrailSegment(StaticLevelEffect effect, Vec3 center, Vec3 right, Vec3 forward, int trailVisualLifetimeTicks) {
        Vec3 leftEdge = center.add(right.scale(TRAIL_HALF_WIDTH));
        Vec3 rightEdge = center.add(right.scale(-TRAIL_HALF_WIDTH));
        Vec3 forwardOffset = forward.scale(0.3D);
        Vec3 centerLeft = center.add(right.scale(0.5D));
        Vec3 centerRight = center.add(right.scale(-0.5D));
        int lifetime = Math.max(1, trailVisualLifetimeTicks);
        Vec3 rearOffset = forward.scale(-0.22D);
        Vec3 frontOffset = forward.scale(0.22D);

        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.62F, lifetime, 0xFFD8C0FF, 0.0D, 0.006D, 0.0D, 10.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, new Vector3f(2.15F, 1.0F, 2.15F));
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.48F, lifetime, 0xFFE8D8FF, 0.0D, 0.008D, 0.0D, -12.0F)
                .emmit(effect, toVector(center.add(rearOffset)), IDENTITY_ROTATION, new Vector3f(1.85F, 1.0F, 1.85F));
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.42F, lifetime, 0xFFF3EAFF, 0.0D, 0.01D, 0.0D, 14.0F)
                .emmit(effect, toVector(center.add(frontOffset)), IDENTITY_ROTATION, new Vector3f(1.55F, 1.0F, 1.55F));

        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.52F, lifetime, 0xFFE8D8FF, right.x * 0.018D, 0.012D, right.z * 0.018D, -10.0F)
                .emmit(effect, toVector(centerLeft), IDENTITY_ROTATION, new Vector3f(1.6F, 1.0F, 1.6F));
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.52F, lifetime, 0xFFE8D8FF, -right.x * 0.018D, 0.012D, -right.z * 0.018D, 10.0F)
                .emmit(effect, toVector(centerRight), IDENTITY_ROTATION, new Vector3f(1.6F, 1.0F, 1.6F));

        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.46F, lifetime, 0xFFFFFFFF, right.x * 0.075D, 0.032D, right.z * 0.075D, 22.0F)
                .emmit(effect, toVector(leftEdge.add(forwardOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.46F, lifetime, 0xFFE8D8FF, -right.x * 0.075D, 0.032D, -right.z * 0.075D, -22.0F)
                .emmit(effect, toVector(rightEdge.add(forwardOffset)), IDENTITY_ROTATION, UNIT_SCALE);

        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.34F, lifetime, 0xFFF3EAFF, right.x * 0.045D, 0.018D, right.z * 0.045D, 28.0F)
                .emmit(effect, toVector(center.add(right.scale(0.75D))), IDENTITY_ROTATION, UNIT_SCALE);
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.34F, lifetime, 0xFFF3EAFF, -right.x * 0.045D, 0.018D, -right.z * 0.045D, -28.0F)
                .emmit(effect, toVector(center.add(right.scale(-0.75D))), IDENTITY_ROTATION, UNIT_SCALE);
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.26F, lifetime, 0xFFFFFFFF, right.x * 0.11D, 0.026D, right.z * 0.11D, 36.0F)
                .emmit(effect, toVector(center.add(right.scale(1.15D)).add(rearOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createLingeringTrailEmitter(PORTAL_TEXTURE, 0.26F, lifetime, 0xFFE8D8FF, -right.x * 0.11D, 0.026D, -right.z * 0.11D, -36.0F)
                .emmit(effect, toVector(center.add(right.scale(-1.15D)).add(rearOffset)), IDENTITY_ROTATION, UNIT_SCALE);
    }
}
