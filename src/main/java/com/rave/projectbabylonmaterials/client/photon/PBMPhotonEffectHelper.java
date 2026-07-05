package com.rave.projectbabylonmaterials.client.photon;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO(port): Photon2 (mc1.21.1) + LDLib2 replaced the programmatic emitter API this helper relied on.
// The old path (build a ParticleEmitter/ParticleConfig and call ParticleEmitter.emmit(IEffect, pos, rot, scale))
// is gone: IEffect was removed, ParticleConfig.material and the renderer bloom settings were removed,
// GradientColor moved to com.lowdragmc.lowdraglib2.math (with a provider-aware NBT API), and effects are now
// spawned through FX/FXRuntime/FXEffectExecutor (see com.lowdragmc.photon.client.fx.*). All public methods and
// the per-effect geometry below are preserved; the actual emission is funneled through EmitterHandle.emmit and
// is currently a no-op pending re-implementation against the Photon2 FX runtime.
@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class PBMPhotonEffectHelper {
    private static final Map<Integer, OrbitState> ACTIVE_DRAGON_DESCEND_CASTS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_GLACIER_CASTS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_FIRE_STORM_CASTS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_BASTION_FROST_AURAS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_BASTION_RULE_AURAS = new ConcurrentHashMap<>();
    private static final Quaternionf IDENTITY_ROTATION = new Quaternionf();
    private static final Vector3f UNIT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
    private static final ResourceLocation PORTAL_TEXTURE = texture("dragon_descend_spell.png");
    private static final ResourceLocation HOLY_TEXTURE = texture("holy_particle.png");
    private static final ResourceLocation HEAL_TEXTURE = texture("heal_particle.png");
    private static final ResourceLocation ABSORPTION_TEXTURE = texture("absorption_particle.png");
    private static final ResourceLocation SNOWFLAKE_TEXTURE = texture("snowflake_particle.png");
    private static final ResourceLocation SNOW_TEXTURE = texture("snow_particle.png");
    private static final ResourceLocation GOLDEN_TEXTURE = texture("golden_particle.png");
    private static final ResourceLocation DIAMOND_TEXTURE = texture("diamond_particle.png");
    private static final ResourceLocation GOLDEN_TEXTURE_2 = texture("golden_particle_2.png");
    private static final ResourceLocation DIAMOND_TEXTURE_2 = texture("diamond_particle_2.png");
    private static final ResourceLocation FIRE_TEXTURE_1 = texture("fire_particle_1.png");
    private static final ResourceLocation FIRE_TEXTURE_2 = texture("fire_particle_2.png");
    private static final ResourceLocation SMOKE_TEXTURE_1 = texture("smoke_particle_1.png");
    private static final ResourceLocation SMOKE_TEXTURE_2 = texture("smoke_particle_2.png");
    private static final float INNER_RADIUS = 1.35F;
    private static final float OUTER_RADIUS = 1.85F;
    private static final float INNER_HEIGHT = 1.05F;
    private static final float OUTER_HEIGHT = 1.55F;
    private static final float ROTATION_SPEED = 0.42F;
    private static final int ORBIT_PARTICLES_PER_TICK = 4;
    private static final int BURST_PARTICLE_COUNT = 32;
    private static final double BURST_SPEED = 0.36D;
    private static final int BREATH_PARTICLE_COUNT = 5;
    private static final double BREATH_DOWNWARD_BIAS = -0.24D;
    private static final int ENDER_PROJECTILE_PARTICLE_INTERVAL = 2;
    private static final int HOLY_PROJECTILE_PARTICLE_INTERVAL = 2;
    private static final int ICE_PROJECTILE_PARTICLE_INTERVAL = 2;
    private static final int FIRE_PROJECTILE_PARTICLE_INTERVAL = 2;
    private static final int GOLDEN_PROJECTILE_PARTICLE_INTERVAL = 2;
    private static final int DIAMOND_PROJECTILE_PARTICLE_INTERVAL = 2;
    private static final int GLACIER_WAVE_PARTICLE_COUNT = 28;
    private static final int GLACIER_VORTEX_PARTICLES_PER_TICK = 7;
    private static final float GLACIER_VORTEX_ROTATION_SPEED = 0.48F;
    private static final int BLESSING_VORTEX_PARTICLES_PER_TICK = 8;
    private static final float BLESSING_VORTEX_ROTATION_SPEED = 0.34F;
    private static final int BLESSING_BURST_PARTICLE_COUNT = 22;
    private static final int FIRE_STORM_CAST_PARTICLES_PER_TICK = 8;
    private static final float FIRE_STORM_CAST_ROTATION_SPEED = 0.46F;
    private static final int FIRE_STORM_BURST_PARTICLE_COUNT = 22;
    private static final int TRAIL_VISUAL_INTERVAL = 2;
    private static final int TRAIL_VISUAL_LIFETIME = 60;
    private static final double TRAIL_HALF_WIDTH = 1.5D;

    private PBMPhotonEffectHelper() {
    }

    public static void startDragonDescendCast(Entity entity) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_DRAGON_DESCEND_CASTS.put(entity.getId(), new OrbitState(entity.getId()));
    }

    public static void burstDragonDescendCast(Entity entity) {
        if (entity == null || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_DRAGON_DESCEND_CASTS.remove(entity.getId());
        spawnBurst(entity);
    }

    public static void stopDragonDescendCast(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_DRAGON_DESCEND_CASTS.remove(entity.getId());
    }

    public static void startGlacierCast(Entity entity) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_GLACIER_CASTS.put(entity.getId(), new OrbitState(entity.getId()));
    }

    public static void stopGlacierCast(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_GLACIER_CASTS.remove(entity.getId());
    }

    public static void startFireStormCast(Entity entity) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_FIRE_STORM_CASTS.put(entity.getId(), new OrbitState(entity.getId()));
    }

    public static void burstFireStormCast(Entity entity) {
        if (entity == null || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_FIRE_STORM_CASTS.remove(entity.getId());
        spawnFireStormCastBurst(entity);
    }

    public static void stopFireStormCast(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_FIRE_STORM_CASTS.remove(entity.getId());
    }

    public static void startBastionFrostAura(Entity entity, float radiusBlocks) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_BASTION_FROST_AURAS.put(entity.getId(), new OrbitState(entity.getId(), radiusBlocks));
    }

    public static void stopBastionFrostAura(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_BASTION_FROST_AURAS.remove(entity.getId());
    }

    public static void startBastionRuleAura(Entity entity, float radiusBlocks) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_BASTION_RULE_AURAS.put(entity.getId(), new OrbitState(entity.getId(), radiusBlocks));
    }

    public static void stopBastionRuleAura(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_BASTION_RULE_AURAS.remove(entity.getId());
    }

    public static void spawnFireStorm(Entity entity, float progress, float height, float radius, int tickCount) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 origin = entity.position().add(0.0D, 0.05D, 0.0D);
        float spin = tickCount * 0.34F;

        for (int i = 0; i < 12; i++) {
            float layerProgress = i / 11.0F;
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

        for (int ribbon = 0; ribbon < 4; ribbon++) {
            float ribbonAngle = (spin * 1.55F) + ((Mth.TWO_PI / 4.0F) * ribbon);
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
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        spawnBlessingStartRing(entity);
    }

    public static void burstBlessingCast(Entity entity) {
        if (entity == null || entity.level() == null || !entity.level().isClientSide) {
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

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 origin = entity.position();
        double baseY = origin.y + 0.08D;
        double upperY = origin.y + Math.max(0.9D, entity.getBbHeight() * 0.45D);

        for (int i = 0; i < GLACIER_WAVE_PARTICLE_COUNT; i++) {
            double angle = (Math.PI * 2.0D * i) / GLACIER_WAVE_PARTICLE_COUNT;
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

    public static void spawnDragonDescendFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 normalized = movement.normalize();
        Vec3 breathDirection = new Vec3(normalized.x, normalized.y + BREATH_DOWNWARD_BIAS, normalized.z).normalize();
        Vec3 right = horizontalRight(normalized);

        Vec3 origin = projectile.position().subtract(normalized.scale(0.35D)).add(0.0D, 0.15D, 0.0D);
        Quaternionf breathRotation = quaternionFromDirection(breathDirection);
        EmitterHandle breathEmitter = createBreathEmitter();
        breathEmitter.emmit(effect, toVector(origin), breathRotation, UNIT_SCALE);

        if ((projectile.tickCount % TRAIL_VISUAL_INTERVAL) != 0) {
            return;
        }

        Vec3 trailCenter = projectile.position().subtract(normalized.scale(0.9D)).add(0.0D, 0.05D, 0.0D);
        spawnTrailSegment(effect, trailCenter, right, normalized);
    }

    public static void spawnEnderProjectileFlight(Entity projectile, Vec3 movement) {
        if (!(projectile.level() instanceof ClientLevel level) || movement.lengthSqr() < 1.0E-6D) {
            return;
        }

        if ((projectile.tickCount % ENDER_PROJECTILE_PARTICLE_INTERVAL) != 0) {
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

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < 24; i++) {
            double angle = (Math.PI * 2.0D * i) / 24.0D;
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

        if ((projectile.tickCount % HOLY_PROJECTILE_PARTICLE_INTERVAL) != 0) {
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

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < 14; i++) {
            double angle = (Math.PI * 2.0D * i) / 14.0D;
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

        if ((projectile.tickCount % ICE_PROJECTILE_PARTICLE_INTERVAL) != 0) {
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

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < 24; i++) {
            double angle = (Math.PI * 2.0D * i) / 24.0D;
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

        if ((projectile.tickCount % FIRE_PROJECTILE_PARTICLE_INTERVAL) != 0) {
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

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < 24; i++) {
            double angle = (Math.PI * 2.0D * i) / 24.0D;
            double speed = 0.18D + ((i & 1) * 0.05D);
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.03D + ((i % 3) * 0.015D);
            ResourceLocation texture = (i % 2 == 0) ? FIRE_TEXTURE_1 : FIRE_TEXTURE_2;
            int color = (i % 2 == 0) ? 0xFFFFC95A : 0xFFFF6A1E;
            createTrailEmitter(texture, 0.3F, 14, color, vx, vy, vz, (i & 1) == 0 ? 24.0F : -24.0F)
                    .emmit(effect, toVector(hitPos.add(0.0D, 0.08D, 0.0D)), IDENTITY_ROTATION, UNIT_SCALE);
        }

        for (int i = 0; i < 14; i++) {
            double angle = (Math.PI * 2.0D * i) / 14.0D;
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

        if ((projectile.tickCount % GOLDEN_PROJECTILE_PARTICLE_INTERVAL) != 0) {
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

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < 14; i++) {
            double angle = (Math.PI * 2.0D * i) / 14.0D;
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

        if ((projectile.tickCount % DIAMOND_PROJECTILE_PARTICLE_INTERVAL) != 0) {
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

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (int i = 0; i < 24; i++) {
            double angle = (Math.PI * 2.0D * i) / 24.0D;
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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            ACTIVE_DRAGON_DESCEND_CASTS.clear();
            ACTIVE_GLACIER_CASTS.clear();
            ACTIVE_FIRE_STORM_CASTS.clear();
            ACTIVE_BASTION_FROST_AURAS.clear();
            ACTIVE_BASTION_RULE_AURAS.clear();
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (OrbitState state : ACTIVE_DRAGON_DESCEND_CASTS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_DRAGON_DESCEND_CASTS.remove(state.entityId);
                continue;
            }

            spawnOrbit(effect, entity, state.tick);
            state.tick++;
        }

        for (OrbitState state : ACTIVE_GLACIER_CASTS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_GLACIER_CASTS.remove(state.entityId);
                continue;
            }

            spawnGlacierVortex(effect, entity, state.tick);
            state.tick++;
        }

        for (OrbitState state : ACTIVE_FIRE_STORM_CASTS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_FIRE_STORM_CASTS.remove(state.entityId);
                continue;
            }

            spawnFireStormCastOrbit(effect, entity, state.tick);
            state.tick++;
        }

        for (OrbitState state : ACTIVE_BASTION_FROST_AURAS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_BASTION_FROST_AURAS.remove(state.entityId);
                continue;
            }

            spawnBastionFrostAura(effect, entity, state.tick, state.radiusBlocks);
            state.tick++;
        }

        for (OrbitState state : ACTIVE_BASTION_RULE_AURAS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_BASTION_RULE_AURAS.remove(state.entityId);
                continue;
            }

            spawnBastionRuleAura(effect, entity, state.tick, state.radiusBlocks);
            state.tick++;
        }

    }

    private static void spawnBastionFrostAura(StaticLevelEffect effect, Entity entity, int tick, float radiusBlocks) {
        float baseAngle = tick * 0.14F;
        double baseY = entity.getY() + 0.08D;
        double outerRadius = Math.max(1.2D, radiusBlocks - 0.2D);
        double innerRadius = Math.max(0.8D, outerRadius * 0.56D);
        float outerScale = Mth.clamp(radiusBlocks / 8.0F, 0.45F, 1.35F);
        for (int i = 0; i < 10; i++) {
            float angle = baseAngle + ((Mth.TWO_PI / 10.0F) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double radius = outerRadius + (((i & 1) == 0 ? 0.18D : -0.06D) * outerScale);
            double x = entity.getX() + (cos * radius);
            double z = entity.getZ() + (sin * radius);
            createTrailEmitter(SNOWFLAKE_TEXTURE, 0.24F, 14, 0xFFE8F6FF, -sin * 0.02D, 0.012D, cos * 0.02D, (i & 1) == 0 ? 16.0F : -16.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) baseY, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        for (int i = 0; i < 6; i++) {
            float angle = -baseAngle * 0.82F + ((Mth.TWO_PI / 6.0F) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = entity.getX() + (cos * innerRadius);
            double z = entity.getZ() + (sin * innerRadius);
            createTrailEmitter(SNOW_TEXTURE, 0.32F, 12, 0xFFBFE8FF, -cos * 0.012D, 0.02D, -sin * 0.012D, (i & 1) == 0 ? 10.0F : -10.0F, -angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) (baseY + 0.36D), (float) z), IDENTITY_ROTATION, new Vector3f(1.25F, 1.0F, 1.25F));
        }
    }

    private static void spawnBastionRuleAura(StaticLevelEffect effect, Entity entity, int tick, float radiusBlocks) {
        float baseAngle = tick * 0.18F;
        double groundY = entity.getY() + 0.1D;
        double waistY = entity.getY() + Math.max(0.65D, entity.getBbHeight() * 0.42D);
        double outerRadius = Math.max(1.2D, radiusBlocks - 0.2D);
        double innerRadius = Math.max(0.82D, outerRadius * 0.55D);
        for (int i = 0; i < 8; i++) {
            float angle = baseAngle + ((Mth.TWO_PI / 8.0F) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            createTrailEmitter(PORTAL_TEXTURE, 0.3F, 14, 0xFFD7C2FF, -sin * 0.026D, 0.008D, cos * 0.026D, (i & 1) == 0 ? 18.0F : -18.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) (entity.getX() + (cos * outerRadius)), (float) groundY, (float) (entity.getZ() + (sin * outerRadius))), IDENTITY_ROTATION, new Vector3f(1.2F, 1.0F, 1.2F));
        }

        for (int i = 0; i < 5; i++) {
            float angle = -baseAngle * 0.9F + ((Mth.TWO_PI / 5.0F) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            createTrailEmitter(PORTAL_TEXTURE, 0.38F, 12, 0xFFF1E8FF, -cos * 0.014D, 0.016D, -sin * 0.014D, (i & 1) == 0 ? 12.0F : -12.0F, -angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) (entity.getX() + (cos * innerRadius)), (float) waistY, (float) (entity.getZ() + (sin * innerRadius))), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static void spawnOrbit(StaticLevelEffect effect, Entity entity, int tick) {
        float baseAngle = (tick * ROTATION_SPEED) % Mth.TWO_PI;
        for (int i = 0; i < ORBIT_PARTICLES_PER_TICK; i++) {
            float angle = baseAngle + ((Mth.TWO_PI / ORBIT_PARTICLES_PER_TICK) * i);
            emitPhotonParticle(effect, entity, angle, INNER_RADIUS, INNER_HEIGHT, 0.24F, 7, 0xFFFFFFFF, 0.0D, -0.01D, 0.0D, angle * Mth.RAD_TO_DEG, 26.0F);
            emitPhotonParticle(effect, entity, angle + (Mth.TWO_PI / 8.0F), OUTER_RADIUS, OUTER_HEIGHT, 0.32F, 7, 0xFFE8D8FF, 0.0D, 0.018D, 0.0D, -angle * Mth.RAD_TO_DEG, -34.0F);
        }
    }

    private static void spawnGlacierVortex(StaticLevelEffect effect, Entity entity, int tick) {
        float baseAngle = tick * GLACIER_VORTEX_ROTATION_SPEED;
        double baseX = entity.getX();
        double baseY = entity.getY() + 0.05D;
        double baseZ = entity.getZ();
        double topY = entity.getY() + entity.getBbHeight() + 0.35D;

        for (int i = 0; i < GLACIER_VORTEX_PARTICLES_PER_TICK; i++) {
            float progress = i / (float) GLACIER_VORTEX_PARTICLES_PER_TICK;
            double swirlHeight = progress * Math.max(1.35D, entity.getBbHeight() + 0.2D);
            double radius = Mth.lerp(progress, 1.3D, 0.12D);
            float angle = baseAngle + (progress * 3.6F) + ((Mth.TWO_PI / GLACIER_VORTEX_PARTICLES_PER_TICK) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = baseX + cos * radius;
            double y = baseY + swirlHeight;
            double z = baseZ + sin * radius;
            double vx = -cos * 0.035D;
            double vz = -sin * 0.035D;
            double vy = 0.075D + (0.02D * (1.0D - progress));
            float size = (float) Mth.lerp(progress, 0.24D, 0.1D);
            int color = (i & 1) == 0 ? 0xE8FFFFFF : 0xC8D8F4FF;
            ResourceLocation texture = (i % 3 == 0) ? SNOW_TEXTURE : SNOWFLAKE_TEXTURE;
            createTrailEmitter(texture, size, 12, color, vx, vy, vz, (i & 1) == 0 ? 22.0F : -22.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        createTrailEmitter(SNOW_TEXTURE, 0.38F, 10, 0x90D8F4FF, 0.0D, 0.1D, 0.0D, 16.0F)
                .emmit(effect, new Vector3f((float) baseX, (float) topY, (float) baseZ), IDENTITY_ROTATION, new Vector3f(1.15F, 1.0F, 1.15F));
    }

    private static void spawnFireStormCastOrbit(StaticLevelEffect effect, Entity entity, int tick) {
        float baseAngle = tick * FIRE_STORM_CAST_ROTATION_SPEED;
        double baseX = entity.getX();
        double baseY = entity.getY() + 0.08D;
        double baseZ = entity.getZ();

        for (int i = 0; i < FIRE_STORM_CAST_PARTICLES_PER_TICK; i++) {
            float progress = i / (float) FIRE_STORM_CAST_PARTICLES_PER_TICK;
            double swirlHeight = progress * Math.max(1.6D, entity.getBbHeight() + 0.55D);
            double radius = Mth.lerp(progress, 1.15D, 0.22D);
            float angle = baseAngle + (progress * 4.4F) + ((Mth.TWO_PI / FIRE_STORM_CAST_PARTICLES_PER_TICK) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = baseX + cos * radius;
            double y = baseY + swirlHeight;
            double z = baseZ + sin * radius;
            double vx = -cos * 0.04D;
            double vz = -sin * 0.04D;
            double vy = 0.075D + (0.03D * (1.0D - progress));
            float size = (float) Mth.lerp(progress, 0.28D, 0.12D);
            ResourceLocation texture = (i & 1) == 0 ? FIRE_TEXTURE_1 : FIRE_TEXTURE_2;
            int color = (i & 1) == 0 ? 0xFFFFC95A : 0xFFFF6A1E;
            createTrailEmitterNoBloom(texture, size, 10, color, vx, vy, vz, (i & 1) == 0 ? 24.0F : -24.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);

            if ((i % 3) == 0) {
                createTrailEmitterNoBloom((i & 1) == 0 ? SMOKE_TEXTURE_1 : SMOKE_TEXTURE_2, size * 0.95F, 12, 0xFF262626, vx * 0.6D, 0.025D, vz * 0.6D, (i & 1) == 0 ? 14.0F : -14.0F)
                        .emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
            }
        }
    }

    private static void spawnFireStormCastBurst(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        Vec3 origin = entity.position();
        double groundY = origin.y + 0.08D;
        double torsoY = origin.y + Math.max(0.95D, entity.getBbHeight() * 0.58D);

        for (int i = 0; i < FIRE_STORM_BURST_PARTICLE_COUNT; i++) {
            double angle = (Math.PI * 2.0D * i) / FIRE_STORM_BURST_PARTICLE_COUNT;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double outerSpeed = 0.12D + ((i & 1) == 0 ? 0.03D : 0.0D);
            double innerSpeed = 0.08D + ((i % 3) * 0.01D);
            ResourceLocation fireTexture = (i & 1) == 0 ? FIRE_TEXTURE_1 : FIRE_TEXTURE_2;
            int fireColor = (i & 1) == 0 ? 0xFFFFC95A : 0xFFFF6A1E;

            createTrailEmitterNoBloom(fireTexture, 0.28F, 14, fireColor, cos * outerSpeed, 0.01D, sin * outerSpeed, (i & 1) == 0 ? 26.0F : -26.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) groundY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.5F, 1.0F, 1.5F));
            createTrailEmitterNoBloom(fireTexture, 0.2F, 12, fireColor, cos * innerSpeed, 0.025D, sin * innerSpeed, (i & 1) == 0 ? 18.0F : -18.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) torsoY, (float) origin.z), IDENTITY_ROTATION, UNIT_SCALE);

            if ((i % 3) == 0) {
                createTrailEmitterNoBloom((i & 1) == 0 ? SMOKE_TEXTURE_1 : SMOKE_TEXTURE_2, 0.22F, 15, 0xFF242424, cos * (outerSpeed * 0.75D), 0.015D, sin * (outerSpeed * 0.75D), (i & 1) == 0 ? 12.0F : -12.0F)
                        .emmit(effect, new Vector3f((float) origin.x, (float) groundY, (float) origin.z), IDENTITY_ROTATION, UNIT_SCALE);
            }
        }

        createTrailEmitterNoBloom(FIRE_TEXTURE_1, 0.42F, 12, 0xFFFFE08A, 0.0D, 0.04D, 0.0D, 18.0F)
                .emmit(effect, new Vector3f((float) origin.x, (float) torsoY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.35F, 1.0F, 1.35F));
    }


    public static void spawnBlessingHealPulse(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
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
        if (!(entity.level() instanceof ClientLevel level)) {
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
        if (!(entity.level() instanceof ClientLevel level) || progress <= 0.01F || (tick & 1) != 0) {
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

        for (int i = 0; i < 18; i++) {
            double angle = (Math.PI * 2.0D * i) / 18.0D;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double outerSpeed = 0.085D + ((i & 1) == 0 ? 0.012D : 0.0D);
            createTrailEmitter((i % 4 == 0) ? HEAL_TEXTURE : HOLY_TEXTURE, 0.24F, 18, (i % 4 == 0) ? 0xFFFFF1C8 : 0xFFFFFFFF,
                    cos * outerSpeed, -0.008D, sin * outerSpeed, (i & 1) == 0 ? 20.0F : -20.0F)
                    .emmit(effect, new Vector3f((float) origin.x, (float) groundY, (float) origin.z), IDENTITY_ROTATION, new Vector3f(1.45F, 1.0F, 1.45F));
        }

        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI * 2.0D * i) / 12.0D;
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

        for (int i = 0; i < BLESSING_BURST_PARTICLE_COUNT; i++) {
            double angle = (Math.PI * 2.0D * i) / BLESSING_BURST_PARTICLE_COUNT;
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

    private static void emitPhotonParticle(StaticLevelEffect effect, Entity entity, float angle, float radius, float height,
                                           float size, int lifetime, int color, double vx, double vy, double vz,
                                           float startRollDegrees, float rollPerTickDegrees) {
        double x = entity.getX() + Mth.cos(angle) * radius;
        double y = entity.getY() + height;
        double z = entity.getZ() + Mth.sin(angle) * radius;
        EmitterHandle emitter = createTrailEmitter(PORTAL_TEXTURE, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees);
        emitter.emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
    }

    private static void spawnBurst(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        double baseY = entity.getY() + entity.getBbHeight() * 0.72D;
        for (int i = 0; i < BURST_PARTICLE_COUNT; i++) {
            double angle = (Math.PI * 2.0D * i) / BURST_PARTICLE_COUNT;
            double spread = BURST_SPEED * (0.85D + ((i & 1) * 0.2D));
            double vx = Math.cos(angle) * spread;
            double vz = Math.sin(angle) * spread;
            double vy = ((i % 4) - 1.5D) * 0.03D;
            float startRollDegrees = (float) Math.toDegrees(angle);
            float rollPerTickDegrees = ((i & 1) == 0) ? 42.0F : -42.0F;
            EmitterHandle emitter = createTrailEmitter(PORTAL_TEXTURE, 0.34F, 11, 0xFFFFFFFF, vx, vy, vz, rollPerTickDegrees, startRollDegrees);
            emitter.emmit(effect, new Vector3f((float) entity.getX(), (float) baseY, (float) entity.getZ()), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static void spawnTrailSegment(StaticLevelEffect effect, Vec3 center, Vec3 right, Vec3 forward) {
        Vec3 leftEdge = center.add(right.scale(TRAIL_HALF_WIDTH));
        Vec3 rightEdge = center.add(right.scale(-TRAIL_HALF_WIDTH));
        Vec3 forwardOffset = forward.scale(0.3D);
        Vec3 centerLeft = center.add(right.scale(0.5D));
        Vec3 centerRight = center.add(right.scale(-0.5D));

        createTrailEmitter(PORTAL_TEXTURE, 0.52F, TRAIL_VISUAL_LIFETIME, 0xFFD8C0FF, 0.0D, 0.01D, 0.0D, 10.0F)
                .emmit(effect, toVector(center), IDENTITY_ROTATION, new Vector3f(1.9F, 1.0F, 1.9F));
        createTrailEmitter(PORTAL_TEXTURE, 0.46F, TRAIL_VISUAL_LIFETIME, 0xFFE8D8FF, 0.0D, 0.012D, 0.0D, -8.0F)
                .emmit(effect, toVector(centerLeft), IDENTITY_ROTATION, new Vector3f(1.45F, 1.0F, 1.45F));
        createTrailEmitter(PORTAL_TEXTURE, 0.46F, TRAIL_VISUAL_LIFETIME, 0xFFE8D8FF, 0.0D, 0.012D, 0.0D, 8.0F)
                .emmit(effect, toVector(centerRight), IDENTITY_ROTATION, new Vector3f(1.45F, 1.0F, 1.45F));
        createTrailEmitter(PORTAL_TEXTURE, 0.42F, TRAIL_VISUAL_LIFETIME, 0xFFFFFFFF, right.x * 0.05D, 0.03D, right.z * 0.05D, 18.0F)
                .emmit(effect, toVector(leftEdge.add(forwardOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(PORTAL_TEXTURE, 0.42F, TRAIL_VISUAL_LIFETIME, 0xFFE8D8FF, -right.x * 0.05D, 0.03D, -right.z * 0.05D, -18.0F)
                .emmit(effect, toVector(rightEdge.add(forwardOffset)), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(PORTAL_TEXTURE, 0.3F, TRAIL_VISUAL_LIFETIME - 8, 0xFFF3EAFF, right.x * 0.03D, 0.015D, right.z * 0.03D, 24.0F)
                .emmit(effect, toVector(center.add(right.scale(0.75D))), IDENTITY_ROTATION, UNIT_SCALE);
        createTrailEmitter(PORTAL_TEXTURE, 0.3F, TRAIL_VISUAL_LIFETIME - 8, 0xFFF3EAFF, -right.x * 0.03D, 0.015D, -right.z * 0.03D, -24.0F)
                .emmit(effect, toVector(center.add(right.scale(-0.75D))), IDENTITY_ROTATION, UNIT_SCALE);
    }

    private static EmitterHandle createBreathEmitter() {
        // TODO(port): re-author the dragon-descend breath cone emitter against the Photon2 FX runtime.
        return new EmitterHandle(PORTAL_TEXTURE, 0xFFDBC3FF);
    }

    private static EmitterHandle createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                    double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F);
    }

    private static EmitterHandle createTrailEmitterNoBloom(ResourceLocation texture, float size, int lifetime, int color,
                                                           double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitterNoBloom(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F);
    }

    private static EmitterHandle createTrailEmitterNoBloom(ResourceLocation texture, float size, int lifetime, int color,
                                                           double vx, double vy, double vz, float rollPerTickDegrees,
                                                           float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, false);
    }

    private static EmitterHandle createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                    double vx, double vy, double vz, float rollPerTickDegrees,
                                                    float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, true);
    }

    private static EmitterHandle createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                    double vx, double vy, double vz, float rollPerTickDegrees,
                                                    float startRollDegrees, boolean bloom) {
        // TODO(port): rebuild this single-particle trail emitter (size/lifetime/velocity/roll/bloom) on the
        // Photon2 FX runtime; the legacy ParticleEmitter/ParticleConfig build path no longer exists.
        return new EmitterHandle(texture, color);
    }

    private static int withAlpha(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }    private static Vec3 horizontalRight(Vec3 normalized) {
        Vec3 right = new Vec3(-normalized.z, 0.0D, normalized.x);
        return right.lengthSqr() < 1.0E-6D ? new Vec3(1.0D, 0.0D, 0.0D) : right.normalize();
    }

    private static Vec3 verticalAxis(Vec3 normalized, Vec3 right) {
        Vec3 up = normalized.cross(right);
        return up.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 1.0D, 0.0D) : up.normalize();
    }

    private static ResourceLocation texture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/particles/" + fileName);
    }

    private static Quaternionf quaternionFromDirection(Vec3 direction) {
        Vector3f target = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
        return new Quaternionf().rotateTo(new Vector3f(0.0F, 1.0F, 0.0F), target);
    }

    private static Vector3f toVector(Vec3 vec3) {
        return new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
    }

    private record StaticLevelEffect(Level level) {
    }

    /**
     * Thin stand-in for the removed Photon emitter handle. It preserves the
     * {@code emitter.emmit(effect, position, rotation, scale)} call shape used throughout this class so the
     * per-effect geometry compiles unchanged while the Photon2 FX emission path is re-implemented.
     */
    private record EmitterHandle(ResourceLocation texture, int color) {
        private void emmit(StaticLevelEffect effect, Vector3f position, Quaternionf rotation, Vector3f scale) {
            // TODO(port): spawn into the Photon2 FX runtime (FXHelper/FXRuntime + IEffectExecutor). No-op for now.
        }
    }

    private static final class OrbitState {
        private final int entityId;
        private final float radiusBlocks;
        private int tick;

        private OrbitState(int entityId) {
            this(entityId, 0.0F);
        }

        private OrbitState(int entityId, float radiusBlocks) {
            this.entityId = entityId;
            this.radiusBlocks = radiusBlocks;
            this.tick = 0;
        }
    }
}





































