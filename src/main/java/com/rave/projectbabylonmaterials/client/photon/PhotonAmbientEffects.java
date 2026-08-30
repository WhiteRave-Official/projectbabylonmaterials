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

final class PhotonAmbientEffects {
    public static void spawnArmorDragonsteelRebirth(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level) || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        double centerX = entity.getX();
        double baseY = entity.getY() + 0.18D;
        double centerZ = entity.getZ();
        int count = photonCount(44);
        float baseAngle = entity.tickCount * 0.28F;

        for (int i = 0; i < count; i++) {
            float progress = i / (float) count;
            float angle = baseAngle + (progress * Mth.TWO_PI * 3.2F);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double height = 0.18D + (progress * Math.max(1.7D, entity.getBbHeight() + 0.55D));
            double radius = 0.25D + (progress * 1.35D);
            double x = centerX + cos * radius;
            double y = baseY + height;
            double z = centerZ + sin * radius;
            double outSpeed = 0.12D + progress * 0.18D;
            double vx = cos * outSpeed - sin * 0.055D;
            double vz = sin * outSpeed + cos * 0.055D;
            double vy = 0.06D + progress * 0.045D;
            int color = (i & 1) == 0 ? 0xFFD8C2FF : 0xFF9D5DFF;
            ResourceLocation texture = (i % 3 == 0) ? PHANTOM_TEXTURE : PORTAL_TEXTURE;
            createTrailEmitterNoBloom(texture, 0.28F + progress * 0.14F, 18, color, vx, vy, vz, (i & 1) == 0 ? 28.0F : -28.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitterNoBloom(PORTAL_TEXTURE, 0.58F, 20, 0xFFEDE4FF, 0.0D, 0.055D, 0.0D, 32.0F)
                .emmit(effect, new Vector3f((float) centerX, (float) (baseY + entity.getBbHeight() * 0.55D), (float) centerZ), IDENTITY_ROTATION, new Vector3f(1.8F, 1.0F, 1.8F));
    }
    public static void spawnShadowFormTransition(Entity entity, boolean entering) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        double centerX = entity.getX();
        double centerY = entity.getY() + Math.max(0.08D, entity.getBbHeight() * 0.56D);
        double centerZ = entity.getZ();
        int particleCount = entering ? 20 : 16;
        double baseSpeed = entering ? 0.09D : 0.13D;
        double verticalBias = entering ? 0.018D : 0.032D;
        float baseSize = entering ? 0.28F : 0.24F;
        int lifetime = entering ? 18 : 14;
        int primaryColor = entering ? 0xFFDCCBFF : 0xFFF3EAFF;
        int secondaryColor = entering ? 0xFF9A7DDB : 0xFFC8B6FF;

        for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2.0D * i) / particleCount;
            double radius = entering ? 0.12D + ((i & 1) == 0 ? 0.18D : 0.06D) : 0.05D;
            double px = centerX + Math.cos(angle) * radius;
            double pz = centerZ + Math.sin(angle) * radius;
            double speed = baseSpeed * (0.85D + ((i % 3) * 0.12D));
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = verticalBias + (((i & 1) == 0) ? 0.008D : -0.004D);
            int color = (i & 1) == 0 ? primaryColor : secondaryColor;
            float size = baseSize + ((i & 1) == 0 ? 0.04F : -0.02F);
            float roll = (i & 1) == 0 ? 24.0F : -24.0F;
            float startRoll = (float) Math.toDegrees(angle);
            createTrailEmitter(PHANTOM_TEXTURE, size, lifetime, color, vx, vy, vz, roll, startRoll)
                    .emmit(effect, new Vector3f((float) px, (float) centerY, (float) pz), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(PHANTOM_TEXTURE,
                entering ? 0.42F : 0.34F,
                entering ? 16 : 12,
                entering ? 0xFFF5EEFF : 0xFFE5D8FF,
                0.0D,
                entering ? 0.012D : 0.02D,
                0.0D,
                entering ? 18.0F : -18.0F)
                .emmit(effect, new Vector3f((float) centerX, (float) centerY, (float) centerZ), IDENTITY_ROTATION,
                        new Vector3f(entering ? 1.2F : 1.05F, 1.0F, entering ? 1.2F : 1.05F));
    }

    public static void spawnSpectralBurst(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        double centerX = entity.getX();
        double centerY = entity.getY() + Math.max(0.08D, entity.getBbHeight() * 0.45D);
        double centerZ = entity.getZ();
        int particleCount = 14;

        for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2.0D * i) / particleCount;
            double speed = 0.08D + ((i & 1) == 0 ? 0.03D : 0.0D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.01D + ((i % 3) * 0.004D);
            ResourceLocation texture = (i & 1) == 0 ? SPECTRAL_TEXTURE_1 : SPECTRAL_TEXTURE_2;
            int color = (i & 1) == 0 ? 0xFFB8FFF7 : 0xFF79E8E3;
            float size = (i & 1) == 0 ? 0.26F : 0.2F;
            createTrailEmitter(texture, size, 14, color, vx, vy, vz, (i & 1) == 0 ? 16.0F : -16.0F, (float) Math.toDegrees(angle))
                    .emmit(effect, new Vector3f((float) centerX, (float) centerY, (float) centerZ), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    public static void spawnSpectralFlightTrail(Entity entity, Vec3 movement) {
        if (!(entity.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 trailCenter = entity.position().subtract(normalized.scale(0.28D)).add(0.0D, Math.max(0.04D, entity.getBbHeight() * 0.18D), 0.0D);
        Vec3 sideways = new Vec3(-normalized.z, 0.0D, normalized.x);

        createTrailEmitter(SPECTRAL_TEXTURE_1, 0.22F, 12, 0xFFB8FFF7, 0.0D, 0.01D, 0.0D, 12.0F, entity.tickCount * 8.0F)
                .emmit(effect, toVector(trailCenter), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(SPECTRAL_TEXTURE_2, 0.18F, 10, 0xFF79E8E3, sideways.x * 0.01D, 0.008D, sideways.z * 0.01D, -14.0F, entity.tickCount * -10.0F)
                .emmit(effect, toVector(trailCenter.add(sideways.scale(0.12D))), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(SPECTRAL_TEXTURE_2, 0.18F, 10, 0xFF79E8E3, -sideways.x * 0.01D, 0.008D, -sideways.z * 0.01D, 14.0F, entity.tickCount * 10.0F)
                .emmit(effect, toVector(trailCenter.add(sideways.scale(-0.12D))), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnStormArrowFlight(Entity entity, Vec3 movement) {
        if (!(entity.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 center = entity.position().add(0.0D, 0.08D, 0.0D);
        Vec3 right = new Vec3(-normalized.z, 0.0D, normalized.x);
        Vec3 up = right.cross(normalized).normalize();
        float baseAngle = entity.tickCount * 0.55F;

        for (int i = 0; i < 3; i++) {
            float angle = baseAngle + ((Mth.TWO_PI / 3.0F) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            Vec3 orbitOffset = right.scale(cos * 0.24D).add(up.scale(sin * 0.24D));
            Vec3 orbitPos = center.add(orbitOffset);
            Vec3 tangent = right.scale(-sin * 0.015D).add(up.scale(cos * 0.015D));
            createTrailEmitter(LEAF_TEXTURE, 0.18F, 12, 0xFFB6E38B, tangent.x, tangent.y + 0.004D, tangent.z, (i & 1) == 0 ? 18.0F : -18.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, toVector(orbitPos), IDENTITY_ROTATION, UNIT_SCALE);
        }

        Vec3 trailBase = center.subtract(normalized.scale(0.34D));
        createTrailEmitter(LEAF_TEXTURE, 0.22F, 14, 0xFFB6E38B, 0.0D, 0.01D, 0.0D, 12.0F, entity.tickCount * 12.0F)
                .emmit(effect, toVector(trailBase), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(LEAF_TEXTURE, 0.16F, 12, 0xFFD7F2B8, right.x * 0.028D, 0.008D, right.z * 0.028D, 16.0F, entity.tickCount * -9.0F)
                .emmit(effect, toVector(trailBase.add(right.scale(0.26D))), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(LEAF_TEXTURE, 0.16F, 12, 0xFFD7F2B8, -right.x * 0.028D, 0.008D, -right.z * 0.028D, -16.0F, entity.tickCount * 9.0F)
                .emmit(effect, toVector(trailBase.add(right.scale(-0.26D))), IDENTITY_ROTATION, UNIT_SCALE);
    }

    public static void spawnStormArrowShot(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        Vec3 movement = entity.getDeltaMovement().lengthSqr() > 1.0E-6D ? entity.getDeltaMovement().normalize() : new Vec3(0.0D, 0.0D, 1.0D);
        Vec3 center = entity.position().add(movement.scale(0.16D)).add(0.0D, 0.08D, 0.0D);
        Vec3 right = new Vec3(-movement.z, 0.0D, movement.x);
        if (right.lengthSqr() < 1.0E-6D) {
            right = new Vec3(1.0D, 0.0D, 0.0D);
        } else {
            right = right.normalize();
        }
        Vec3 up = verticalAxis(movement, right);

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < 2; i++) {
            double radius = i == 0 ? 0.22D : 0.42D;
            float size = i == 0 ? 0.18F : 0.24F;
            int lifetime = i == 0 ? 12 : 14;
            int color = i == 0 ? 0xFFD7F2B8 : 0xFFB6E38B;
            double lateralSpeed = i == 0 ? 0.055D : 0.075D;
            double forwardBias = i == 0 ? 0.012D : 0.018D;

            for (int point = 0; point < 12; point++) {
                double angle = (Math.PI * 2.0D * point) / photonCount(12);
                double x = Math.cos(angle) * radius;
                double y = Math.sin(angle) * radius;
                Vec3 offset = right.scale(x).add(up.scale(y));
                Vec3 velocity = right.scale(x * lateralSpeed).add(up.scale(y * lateralSpeed)).add(movement.scale(forwardBias));
                createTrailEmitter(LEAF_TEXTURE, size, lifetime, color, velocity.x, velocity.y, velocity.z, (point & 1) == 0 ? 18.0F : -18.0F, (float) Math.toDegrees(angle))
                        .emmit(effect, toVector(center.add(offset)), IDENTITY_ROTATION, UNIT_SCALE);
            }
        }
    }
    public static void spawnFireStorm(Entity entity, float progress, float height, float radius, int tickCount) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 origin = entity.position().add(0.0D, 0.05D, 0.0D);
        float spin = tickCount * 0.34F;

        int layerCount = photonCount(12);
        float layerDenominator = Math.max(1.0F, layerCount - 1.0F);
        for (int i = 0; i < layerCount; i++) {
            float layerProgress = i / layerDenominator;
            float layerHeight = height * layerProgress;
            float layerRadius = radius * (0.48F + (layerProgress * 0.95F));
            float angle = spin + (layerProgress * 5.8F) + ((i & 1) == 0 ? 0.0F : Mth.PI * 0.55F);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            Vec3 position = origin.add(cos * layerRadius, layerHeight, sin * layerRadius);
            double inwardX = -cos * 0.04D;
            double inwardZ = -sin * 0.04D;
            double riseSpeed = 0.075D + (0.03D * layerProgress);
            float size = (float) Mth.lerp(layerProgress, 0.44D, 0.2D);
            int color = (i & 1) == 0 ? 0xFFFFC95A : 0xFFFF6A1E;
            ResourceLocation flameTexture = (i & 1) == 0 ? FIRE_TEXTURE_1 : FIRE_TEXTURE_2;
            createTrailEmitterNoBloom(flameTexture, size, 10, color, inwardX, riseSpeed, inwardZ, (i & 1) == 0 ? 22.0F : -22.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, toVector(position), IDENTITY_ROTATION, UNIT_SCALE);

            if ((i % 3) != 1) {
                Vec3 smokePos = origin.add(-cos * (layerRadius * 0.58F), layerHeight * 0.85F, -sin * (layerRadius * 0.58F));
                createTrailEmitterNoBloom((i % 4 == 0) ? SMOKE_TEXTURE_1 : SMOKE_TEXTURE_2, size * 0.85F, 12, 0xFF252525, inwardX * 0.65D, 0.035D, inwardZ * 0.65D, (i & 1) == 0 ? 12.0F : -12.0F)
                        .emmit(effect, toVector(smokePos), IDENTITY_ROTATION, UNIT_SCALE);
            }
        }

        int ribbonCount = photonCount(4);
        for (int ribbon = 0; ribbon < ribbonCount; ribbon++) {
            float ribbonAngle = (spin * 1.55F) + ((Mth.TWO_PI / ribbonCount) * ribbon);
            double ribbonRadius = radius * (0.18D + (0.055D * ribbon));
            double ribbonX = origin.x + Math.cos(ribbonAngle) * ribbonRadius;
            double ribbonZ = origin.z + Math.sin(ribbonAngle) * ribbonRadius;
            double ribbonY = origin.y + (height * (0.18D + (0.16D * ribbon)));
            double tangentX = -Math.sin(ribbonAngle) * 0.085D;
            double tangentZ = Math.cos(ribbonAngle) * 0.085D;
            ResourceLocation ribbonTexture = (ribbon & 1) == 0 ? FIRE_TEXTURE_1 : FIRE_TEXTURE_2;
            int ribbonColor = (ribbon & 1) == 0 ? 0xFFFFE08A : 0xFFFFA040;
            createTrailEmitterNoBloom(ribbonTexture,
                    0.34F + (progress * 0.08F),
                    8,
                    ribbonColor,
                    tangentX,
                    0.095D,
                    tangentZ,
                    (ribbon & 1) == 0 ? 26.0F : -26.0F,
                    ribbonAngle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) ribbonX, (float) ribbonY, (float) ribbonZ), IDENTITY_ROTATION, new Vector3f(1.05F, 1.0F, 1.05F));
        }
    }

    public static void startBlessingCast(Entity entity) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        spawnBlessingStartRing(entity);
    }

    public static void burstBlessingCast(Entity entity) {
        if (entity == null || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        spawnBlessingBurst(entity);
    }

    public static void stopBlessingCast(Entity entity) {
    }

    public static void spawnGlacierContactWave(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 origin = entity.position();
        double baseY = origin.y + 0.08D;
        double upperY = origin.y + Math.max(0.9D, entity.getBbHeight() * 0.45D);

        for (int i = 0; i < photonCount(GLACIER_WAVE_PARTICLE_COUNT); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(GLACIER_WAVE_PARTICLE_COUNT);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double lowerSpeed = 0.12D + ((i & 1) == 0 ? 0.02D : 0.0D);
            double upperSpeed = 0.085D + ((i % 3) * 0.01D);

            createTrailEmitter(SNOW_TEXTURE, 0.48F, 16, 0xC8D8F4FF, cos * lowerSpeed, 0.012D, sin * lowerSpeed, (i & 1) == 0 ? 18.0F : -18.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) baseY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.55F, 1.0F, 1.55F));
            createTrailEmitter(SNOWFLAKE_TEXTURE, 0.28F, 14, 0xE8FFFFFF, cos * upperSpeed, 0.018D, sin * upperSpeed, (i & 1) == 0 ? 24.0F : -24.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) upperY, (float) origin.z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(SNOW_TEXTURE, 0.62F, 18, 0xB8E8F7FF, 0.0D, 0.01D, 0.0D, 10.0F)
                .emmit(effect, new Vector3f((float) origin.x, (float) baseY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.8F, 1.0F, 1.8F));
    }


    public static void spawnBlessingHealPulse(Entity entity) {
        if (ShadowFormClientState.isConcealed(entity) || !(entity.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        double centerX = entity.getX();
        double baseY = entity.getY() + Math.max(0.2D, entity.getBbHeight() * 0.2D);
        double midY = entity.getY() + Math.max(0.8D, entity.getBbHeight() * 0.55D);
        double centerZ = entity.getZ();
        int particleCount = 10;

        for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2.0D * i) / particleCount;
            double radius = 0.22D + ((i & 1) == 0 ? 0.1D : 0.18D);
            double x = centerX + Math.cos(angle) * radius;
            double y = ((i % 3) == 0) ? midY : baseY + (0.18D * (i % 4));
            double z = centerZ + Math.sin(angle) * radius;
            double tangentX = -Math.sin(angle) * 0.018D;
            double tangentZ = Math.cos(angle) * 0.018D;
            double rise = 0.03D + ((i % 3) * 0.008D);
            ResourceLocation texture = (i % 4 == 0) ? HOLY_TEXTURE : HEAL_TEXTURE;
            int color = (i % 4 == 0) ? 0xFFFFFFFF : 0xFFFFF1C8;
            float size = (i % 4 == 0) ? 0.18F : 0.22F;
            createTrailEmitter(texture, size, 18, color, tangentX, rise, tangentZ, (i & 1) == 0 ? 14.0F : -14.0F)
                    .emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(HEAL_TEXTURE, 0.26F, 20, 0xFFFFF1C8, 0.0D, 0.04D, 0.0D, 10.0F)
                .emmit(effect, new Vector3f((float) centerX, (float) midY, (float) centerZ), IDENTITY_ROTATION, new Vector3f(1.1F, 1.0F, 1.1F));
    }
    public static void spawnBlessingAbsorptionPulse(Entity entity) {
        if (ShadowFormClientState.isConcealed(entity) || !(entity.level() instanceof ClientLevel level)) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        double centerX = entity.getX();
        double baseY = entity.getY() + Math.max(0.22D, entity.getBbHeight() * 0.24D);
        double midY = entity.getY() + Math.max(0.9D, entity.getBbHeight() * 0.6D);
        double centerZ = entity.getZ();
        int particleCount = 10;

        for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2.0D * i) / particleCount;
            double radius = 0.24D + ((i & 1) == 0 ? 0.12D : 0.2D);
            double x = centerX + Math.cos(angle) * radius;
            double y = ((i % 3) == 0) ? midY : baseY + (0.2D * (i % 4));
            double z = centerZ + Math.sin(angle) * radius;
            double tangentX = -Math.sin(angle) * 0.02D;
            double tangentZ = Math.cos(angle) * 0.02D;
            double rise = 0.028D + ((i % 3) * 0.008D);
            ResourceLocation texture = (i % 4 == 0) ? HOLY_TEXTURE : ABSORPTION_TEXTURE;
            int color = (i % 4 == 0) ? 0xFFFFFFFF : 0xFFFFD977;
            float size = (i % 4 == 0) ? 0.2F : 0.24F;
            createTrailEmitter(texture, size, 20, color, tangentX, rise, tangentZ, (i & 1) == 0 ? 16.0F : -16.0F)
                    .emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(ABSORPTION_TEXTURE, 0.28F, 22, 0xFFFFD977, 0.0D, 0.045D, 0.0D, 12.0F)
                .emmit(effect, new Vector3f((float) centerX, (float) midY, (float) centerZ), IDENTITY_ROTATION, new Vector3f(1.15F, 1.0F, 1.15F));
    }

    public static void spawnAbsorptionShield(LivingEntity entity, float progress, int tick, float absorptionAmount) {
        if (ShadowFormClientState.isConcealed(entity) || !(entity.level() instanceof ClientLevel level) || progress <= 0.01F || (tick & 1) != 0) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, tick)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        float clampedProgress = Mth.clamp(progress, 0.0F, 1.0F);
        float maxHealth = Math.max(1.0F, entity.getMaxHealth());
        float intensity = Mth.clamp(absorptionAmount / maxHealth, 0.0F, 1.0F);
        float width = Math.max(entity.getBbWidth(), 0.6F);
        float height = Math.max(entity.getBbHeight(), 1.2F);
        float radius = (width * 0.62F) + 0.5F + (0.22F * clampedProgress) + (0.1F * intensity);
        float lowerRadius = radius * 0.88F;
        float upperRadius = radius * 0.78F;
        float size = 0.12F + (0.12F * clampedProgress);
        float baseAngle = tick * (0.16F + (intensity * 0.08F));
        int goldColor = withAlpha(0xFFD977, Mth.clamp((int) (86.0F + (clampedProgress * 96.0F)), 0, 255));
        int holyColor = withAlpha(0xFFFFF4D0, Mth.clamp((int) (64.0F + (clampedProgress * 80.0F)), 0, 255));
        double centerX = entity.getX();
        double centerY = entity.getY();
        double centerZ = entity.getZ();
        spawnAbsorptionShieldRing(effect, centerX, centerY + (height * 0.24F), centerZ, lowerRadius, baseAngle, 5, size, 14, goldColor, ABSORPTION_TEXTURE, 0.02D, 0.004D, 16.0F);
        spawnAbsorptionShieldRing(effect, centerX, centerY + (height * 0.54F), centerZ, radius, baseAngle + 0.7F, 6, size * 1.08F, 16, holyColor, HOLY_TEXTURE, 0.024D, 0.008D, -18.0F);
        spawnAbsorptionShieldRing(effect, centerX, centerY + (height * 0.82F), centerZ, upperRadius, baseAngle + 1.35F, 5, size * 0.94F, 14, goldColor, ABSORPTION_TEXTURE, 0.018D, 0.012D, 20.0F);
        createTrailEmitter(ABSORPTION_TEXTURE, size * 1.8F, 12, withAlpha(0xFFFFE7A6, Mth.clamp((int) (56.0F + (clampedProgress * 56.0F)), 0, 255)), 0.0D, 0.012D, 0.0D, 12.0F)
                .emmit(effect, new Vector3f((float) centerX, (float) (centerY + (height * 0.5F)), (float) centerZ), IDENTITY_ROTATION, new Vector3f(1.15F + (clampedProgress * 0.2F), 1.0F, 1.15F + (clampedProgress * 0.2F)));
    }
    private static void spawnAbsorptionShieldRing(StaticLevelEffect effect, double centerX, double centerY, double centerZ,
                                                  float radius, float baseAngle, int particleCount, float size,
                                                  int lifetime, int color, ResourceLocation texture,
                                                  double tangentSpeed, double riseSpeed, float rollPerTick) {
        for (int i = 0; i < particleCount; i++) {
            float angle = baseAngle + ((Mth.TWO_PI / particleCount) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = centerX + (cos * radius);
            double z = centerZ + (sin * radius);
            double vx = -sin * tangentSpeed;
            double vz = cos * tangentSpeed;
            float startRoll = angle * Mth.RAD_TO_DEG;
            float spin = ((i & 1) == 0) ? rollPerTick : -rollPerTick;
            createTrailEmitter(texture, size, lifetime, color, vx, riseSpeed, vz, spin, startRoll)
                    .emmit(effect, new Vector3f((float) x, (float) centerY, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static void spawnBlessingStartRing(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 origin = entity.position();
        double groundY = origin.y + 0.08D;
        double torsoY = origin.y + Math.max(0.9D, entity.getBbHeight() * 0.62D);

        for (int i = 0; i < photonCount(18); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(18);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double outerSpeed = 0.085D + ((i & 1) == 0 ? 0.012D : 0.0D);
            createTrailEmitter((i % 4 == 0) ? HEAL_TEXTURE : HOLY_TEXTURE, 0.24F, 18, (i % 4 == 0) ? 0xFFFFF1C8 : 0xFFFFFFFF,
                    cos * outerSpeed, -0.008D, sin * outerSpeed, (i & 1) == 0 ? 20.0F : -20.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) groundY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.45F, 1.0F, 1.45F));
        }

        for (int i = 0; i < photonCount(12); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(12);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double innerSpeed = 0.055D + ((i % 3) * 0.005D);
            createTrailEmitter((i % 3 == 0) ? HEAL_TEXTURE : HOLY_TEXTURE, 0.18F, 18, (i % 3 == 0) ? 0xFFFFF1C8 : 0xFFFFFFFF,
                    cos * innerSpeed, -0.03D, sin * innerSpeed, (i & 1) == 0 ? 16.0F : -16.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) torsoY, (float) origin.z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(HOLY_TEXTURE, 0.34F, 16, 0xFFFFFFFF, 0.0D, -0.022D, 0.0D, 18.0F)
                .emmit(effect, new Vector3f((float) origin.x, (float) torsoY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.15F, 1.0F, 1.15F));
    }

    private static void spawnBlessingBurst(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 origin = entity.position();
        double groundY = origin.y + 0.08D;
        double torsoY = origin.y + Math.max(0.9D, entity.getBbHeight() * 0.62D);

        for (int i = 0; i < photonCount(BLESSING_BURST_PARTICLE_COUNT); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(BLESSING_BURST_PARTICLE_COUNT);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double outerSpeed = 0.13D + ((i & 1) == 0 ? 0.02D : 0.0D);
            double innerSpeed = 0.08D + ((i % 3) * 0.008D);
            ResourceLocation texture = (i % 4 == 0) ? HEAL_TEXTURE : HOLY_TEXTURE;
            int color = (i % 4 == 0) ? 0xFFFFF1C8 : 0xFFFFFFFF;

            createTrailEmitter(texture, 0.28F, 16, color, cos * outerSpeed, 0.004D, sin * outerSpeed, (i & 1) == 0 ? 24.0F : -24.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) groundY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.65F, 1.0F, 1.65F));
            createTrailEmitter(texture, 0.2F, 14, color, cos * innerSpeed, -0.035D, sin * innerSpeed, (i & 1) == 0 ? 18.0F : -18.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) torsoY, (float) origin.z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(HOLY_TEXTURE, 0.42F, 14, 0xFFFFFFFF, 0.0D, -0.02D, 0.0D, 18.0F)
                .emmit(effect, new Vector3f((float) origin.x, (float) torsoY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.35F, 1.0F, 1.35F));
        createTrailEmitter(HEAL_TEXTURE, 0.5F, 16, 0xFFFFF1C8, 0.0D, 0.008D, 0.0D, -14.0F)
                .emmit(effect, new Vector3f((float) origin.x, (float) groundY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.9F, 1.0F, 1.9F));
    }

}
