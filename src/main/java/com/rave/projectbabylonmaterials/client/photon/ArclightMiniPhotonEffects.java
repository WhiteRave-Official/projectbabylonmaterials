package com.rave.projectbabylonmaterials.client.photon;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static com.rave.projectbabylonmaterials.client.photon.PhotonEmitterFactory.createTrailEmitterNoBloom;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.IDENTITY_ROTATION;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.UNIT_SCALE;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.StaticLevelEffect;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.horizontalRight;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.toVector;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.verticalAxis;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.photonCount;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.photonLifetime;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.shouldRenderPointEffect;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.shouldRenderTransientEffect;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.useLitePhotonEffects;
import static com.rave.projectbabylonmaterials.client.photon.PhotonTextures.LIGHT_BIG_TEXTURE;
import static com.rave.projectbabylonmaterials.client.photon.PhotonTextures.LIGHT_MEDIUM_TEXTURE;
import static com.rave.projectbabylonmaterials.client.photon.PhotonTextures.LIGHT_SMALL_TEXTURE;

final class ArclightMiniPhotonEffects {
    private ArclightMiniPhotonEffects() {
    }

    static void spawnPortal(Entity projectile, Vec3 direction) {
        spawnPortal(projectile, direction, 1.0F);
    }

    static void spawnSpearPortal(Entity projectile, Vec3 direction) {
        spawnPortal(projectile, direction, 1.55F);
    }

    private static void spawnPortal(Entity projectile, Vec3 direction, float visualScale) {
        if (!(projectile.level() instanceof ClientLevel level)
                || !shouldRenderTransientEffect(projectile, projectile.tickCount)
                || direction.lengthSqr() < 1.0E-6D) {
            return;
        }

        Vec3 forward = direction.normalize();
        Vec3 right = horizontalRight(forward);
        Vec3 up = verticalAxis(forward, right);
        Vec3 center = projectile.position();
        StaticLevelEffect effect = new StaticLevelEffect(level);
        int count = photonCount(10);
        for (int i = 0; i < count; i++) {
            double angle = Mth.TWO_PI * i / count + projectile.tickCount * 0.13D;
            Vec3 radial = right.scale(Math.cos(angle)).add(up.scale(Math.sin(angle)));
            Vec3 position = center.add(radial.scale(0.95D * visualScale));
            Vec3 velocity = radial.scale(-0.055D).add(forward.scale(0.008D));
            ResourceLocation texture = (i & 1) == 0 ? LIGHT_SMALL_TEXTURE : LIGHT_MEDIUM_TEXTURE;
            createTrailEmitterNoBloom(texture, 0.27F * visualScale, photonLifetime(14), 0xFFF4F0FF,
                    velocity.x, velocity.y, velocity.z, (i & 1) == 0 ? 24.0F : -24.0F)
                    .emmit(effect, toVector(position), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    static void spawnLaunch(Entity projectile, Vec3 direction) {
        spawnLaunch(projectile, direction, 1.0F);
    }

    static void spawnSpearLaunch(Entity projectile, Vec3 direction) {
        spawnLaunch(projectile, direction, 1.35F);
    }

    private static void spawnLaunch(Entity projectile, Vec3 direction, float visualScale) {
        if (!(projectile.level() instanceof ClientLevel level)
                || !shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        Vec3 forward = safeDirection(direction);
        Vec3 right = horizontalRight(forward);
        Vec3 up = verticalAxis(forward, right);
        StaticLevelEffect effect = new StaticLevelEffect(level);
        int count = photonCount(14);
        for (int i = 0; i < count; i++) {
            double angle = Mth.TWO_PI * i / count;
            Vec3 radial = right.scale(Math.cos(angle)).add(up.scale(Math.sin(angle)));
            Vec3 velocity = radial.scale(0.07D).add(forward.scale(0.14D + (i % 3) * 0.025D));
            createTrailEmitterNoBloom((i % 3) == 0 ? LIGHT_BIG_TEXTURE : LIGHT_SMALL_TEXTURE,
                    ((i % 3) == 0 ? 0.23F : 0.14F) * visualScale, photonLifetime(12), 0xFFFFFFFF,
                    velocity.x, velocity.y, velocity.z, (i & 1) == 0 ? 34.0F : -34.0F)
                    .emmit(effect, toVector(projectile.position().add(radial.scale(0.18D * visualScale))), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    static void spawnFlight(Entity projectile, Vec3 movement) {
        spawnFlight(projectile, movement, 1.0F);
    }

    static void spawnSpearFlight(Entity projectile, Vec3 movement) {
        spawnFlight(projectile, movement, 1.35F);
    }

    private static void spawnFlight(Entity projectile, Vec3 movement, float visualScale) {
        if (!(projectile.level() instanceof ClientLevel level)
                || movement.lengthSqr() < 1.0E-6D
                || !shouldRenderTransientEffect(projectile, projectile.tickCount)) {
            return;
        }

        Vec3 forward = movement.normalize();
        Vec3 right = horizontalRight(forward);
        Vec3 center = projectile.position().subtract(forward.scale(0.35D));
        StaticLevelEffect effect = new StaticLevelEffect(level);
        createTrailEmitterNoBloom(LIGHT_MEDIUM_TEXTURE, 0.16F * visualScale, photonLifetime(9), 0xFFFFFFFF,
                -forward.x * 0.025D, -forward.y * 0.025D, -forward.z * 0.025D, 28.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
        if (!useLitePhotonEffects()) {
            double wave = Math.sin(projectile.tickCount * 0.85D) * 0.13D;
            createTrailEmitterNoBloom(LIGHT_SMALL_TEXTURE, 0.11F * visualScale, 8, 0xDDE7E2FF,
                    right.x * 0.018D, 0.004D, right.z * 0.018D, -36.0F)
                    .emmit(effect, toVector(center.add(right.scale(wave))), IDENTITY_ROTATION, UNIT_SCALE);
            createTrailEmitterNoBloom(LIGHT_SMALL_TEXTURE, 0.11F * visualScale, 8, 0xDDE7E2FF,
                    -right.x * 0.018D, 0.004D, -right.z * 0.018D, 36.0F)
                    .emmit(effect, toVector(center.add(right.scale(-wave))), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    static void spawnImpact(Entity projectile, Vec3 hitPos, Vec3 direction) {
        spawnImpact(projectile, hitPos, direction, 1.0F);
    }

    static void spawnSpearImpact(Entity projectile, Vec3 hitPos, Vec3 direction) {
        spawnImpact(projectile, hitPos, direction, 1.4F);
    }

    private static void spawnImpact(Entity projectile, Vec3 hitPos, Vec3 direction, float visualScale) {
        if (!(projectile.level() instanceof ClientLevel level) || !shouldRenderPointEffect(level, hitPos)) {
            return;
        }

        Vec3 forward = safeDirection(direction);
        StaticLevelEffect effect = new StaticLevelEffect(level);
        int count = photonCount(18);
        for (int i = 0; i < count; i++) {
            double angle = Mth.TWO_PI * i / count;
            double lift = -0.08D + (i % 5) * 0.04D;
            Vec3 velocity = new Vec3(Math.cos(angle) * 0.12D, lift, Math.sin(angle) * 0.12D)
                    .add(forward.scale(0.035D));
            createTrailEmitterNoBloom((i % 4) == 0 ? LIGHT_BIG_TEXTURE : LIGHT_SMALL_TEXTURE,
                    ((i % 4) == 0 ? 0.25F : 0.14F) * visualScale, photonLifetime(15), 0xFFFFFFFF,
                    velocity.x, velocity.y, velocity.z, (i & 1) == 0 ? 42.0F : -42.0F)
                    .emmit(effect, toVector(hitPos), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    static void spawnDissolve(Entity projectile, Vec3 position) {
        spawnDissolve(projectile, position, 1.0F);
    }

    static void spawnSpearDissolve(Entity projectile, Vec3 position) {
        spawnDissolve(projectile, position, 1.4F);
    }

    private static void spawnDissolve(Entity projectile, Vec3 position, float visualScale) {
        if (!(projectile.level() instanceof ClientLevel level) || !shouldRenderPointEffect(level, position)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        int count = photonCount(9);
        for (int i = 0; i < count; i++) {
            double angle = Mth.TWO_PI * i / count;
            Vec3 velocity = new Vec3(Math.cos(angle) * 0.045D, 0.025D + (i % 3) * 0.012D,
                    Math.sin(angle) * 0.045D);
            createTrailEmitterNoBloom(i % 3 == 0 ? LIGHT_MEDIUM_TEXTURE : LIGHT_SMALL_TEXTURE,
                    0.13F * visualScale, photonLifetime(13), 0xDDF4F0FF,
                    velocity.x, velocity.y, velocity.z, (i & 1) == 0 ? 25.0F : -25.0F)
                    .emmit(effect, toVector(position), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static Vec3 safeDirection(Vec3 direction) {
        return direction.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 0.0D, 1.0D) : direction.normalize();
    }
}