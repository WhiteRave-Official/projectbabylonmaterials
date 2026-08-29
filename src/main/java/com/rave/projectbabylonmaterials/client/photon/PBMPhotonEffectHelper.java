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

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PBMPhotonEffectHelper {
    private static final Map<Integer, OrbitState> ACTIVE_DRAGON_DESCEND_CASTS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_ARCLIGHT_AWAKENINGS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_GLACIER_CASTS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_FIRE_STORM_CASTS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_MAGICAL_VEILS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_BASTION_FROST_AURAS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_BASTION_RULE_AURAS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_BASTION_HEAVENS_GIFT_AURAS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_ARMOR_ICE_AURAS = new ConcurrentHashMap<>();
    private static final Map<Integer, OrbitState> ACTIVE_ARMOR_NETHERITE_FIRE_RINGS = new ConcurrentHashMap<>();
    private static final Quaternionf IDENTITY_ROTATION = new Quaternionf();
    private static final Vector3f UNIT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
    private static final ResourceLocation PORTAL_TEXTURE = texture("dragon_descend_spell.png");
    private static final ResourceLocation LIGHT_SMALL_TEXTURE = texture("light_particle_small.png");
    private static final ResourceLocation LIGHT_MEDIUM_TEXTURE = texture("light_particle_medium.png");
    private static final ResourceLocation LIGHT_BIG_TEXTURE = texture("light_particle_big.png");
    private static final ResourceLocation PHANTOM_TEXTURE = texture("phantom_particle.png");
    private static final ResourceLocation HOLY_TEXTURE = texture("holy_particle.png");
    private static final ResourceLocation HEAL_TEXTURE = texture("heal_particle.png");
    private static final ResourceLocation ABSORPTION_TEXTURE = texture("absorption_particle.png");
    private static final ResourceLocation MAGICAL_VEIL_TEXTURE = texture("magical_veil_particle.png");
    private static final ResourceLocation SPECTRAL_TEXTURE_1 = texture("spectral_particle_1.png");
    private static final ResourceLocation SPECTRAL_TEXTURE_2 = texture("spectral_particle_2.png");
    private static final ResourceLocation LEAF_TEXTURE = texture("leaf_particle.png");
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
    private static final int MAGICAL_VEIL_PARTICLES_PER_TICK = 6;
    private static final float MAGICAL_VEIL_ROTATION_SPEED = 0.14F;
    private static final int TRAIL_VISUAL_INTERVAL = 2;
    private static final int TRAIL_VISUAL_LIFETIME = 60;
    private static final double TRAIL_HALF_WIDTH = 1.5D;
    private static final double PHOTON_NEAR_DISTANCE_SQR = 18.0D * 18.0D;
    private static final double PHOTON_MEDIUM_DISTANCE_SQR = 32.0D * 32.0D;
    private static final double PHOTON_FAR_DISTANCE_SQR = 48.0D * 48.0D;
    private static final double PHOTON_MAX_DISTANCE_SQR = 64.0D * 64.0D;
    private static final double PHOTON_OCCLUDED_MAX_DISTANCE_SQR = 5.0D * 5.0D;
    private static boolean distanceLitePhotonEffect;

    private PBMPhotonEffectHelper() {
    }

    private static boolean useLitePhotonEffects() {
        return PBMClientConfig.useLitePhotonEffects() || distanceLitePhotonEffect;
    }


    private static int photonCount(int baseCount) {
        if (!useLitePhotonEffects()) {
            return baseCount;
        }
        return Math.max(1, Mth.ceil(baseCount * 0.45F));
    }

    private static int photonInterval(int baseInterval) {
        if (!useLitePhotonEffects()) {
            return baseInterval;
        }
        return Math.max(1, baseInterval * 2);
    }

    private static int photonLifetime(int baseLifetime) {
        if (!useLitePhotonEffects()) {
            return baseLifetime;
        }
        return Math.max(6, Mth.ceil(baseLifetime * 0.6F));
    }

    private static boolean shouldRenderPersistentEffect(Entity entity, int tick) {
        distanceLitePhotonEffect = false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused() || minecraft.player == null || minecraft.level == null || entity == null || !entity.isAlive()) {
            return false;
        }

        if (minecraft.player == entity) {
            return true;
        }

        double distanceSqr = minecraft.player.distanceToSqr(entity);
        if (distanceSqr > PHOTON_MAX_DISTANCE_SQR) {
            return false;
        }

        boolean visible = minecraft.player.hasLineOfSight(entity);
        if (!visible && distanceSqr > PHOTON_OCCLUDED_MAX_DISTANCE_SQR) {
            return false;
        }

        distanceLitePhotonEffect = visible && distanceSqr > PHOTON_MEDIUM_DISTANCE_SQR;
        int interval = resolveDistanceInterval(distanceSqr, visible);
        boolean render = interval <= 1 || (tick % interval) == 0;
        return render;
    }
    private static boolean shouldRenderTransientEffect(Entity entity, int tick) {
        distanceLitePhotonEffect = false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused() || minecraft.player == null || minecraft.level == null || entity == null || !entity.isAlive()) {
            return false;
        }

        if (minecraft.player == entity) {
            return true;
        }

        double distanceSqr = minecraft.player.distanceToSqr(entity);
        if (distanceSqr > PHOTON_MAX_DISTANCE_SQR) {
            return false;
        }

        boolean visible = minecraft.player.hasLineOfSight(entity);
        if (!visible && distanceSqr > PHOTON_OCCLUDED_MAX_DISTANCE_SQR) {
            return false;
        }

        distanceLitePhotonEffect = visible && distanceSqr > PHOTON_MEDIUM_DISTANCE_SQR;
        int interval = resolveDistanceInterval(distanceSqr, visible);
        boolean render = interval <= 1 || (tick % interval) == 0;
        return render;
    }
    private static boolean shouldRenderPointEffect(ClientLevel level, Vec3 position) {
        distanceLitePhotonEffect = false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused() || minecraft.player == null || minecraft.level != level) {
            return false;
        }

        Vec3 eyePosition = minecraft.player.getEyePosition();
        double distanceSqr = eyePosition.distanceToSqr(position);
        if (distanceSqr > PHOTON_MAX_DISTANCE_SQR) {
            return false;
        }

        boolean visible = hasLineOfSight(level, eyePosition, position);
        if (!visible && distanceSqr > PHOTON_OCCLUDED_MAX_DISTANCE_SQR) {
            return false;
        }

        distanceLitePhotonEffect = visible && distanceSqr > PHOTON_MEDIUM_DISTANCE_SQR;
        return true;
    }
    private static int resolveDistanceInterval(double distanceSqr, boolean visible) {
        int interval;
        if (distanceSqr <= PHOTON_NEAR_DISTANCE_SQR) {
            interval = 1;
        } else if (distanceSqr <= PHOTON_MEDIUM_DISTANCE_SQR) {
            interval = visible ? 2 : 3;
        } else if (distanceSqr <= PHOTON_FAR_DISTANCE_SQR) {
            interval = visible ? 3 : 5;
        } else {
            interval = visible ? 4 : 6;
        }
        return useLitePhotonEffects() ? interval * 2 : interval;
    }

    private static boolean hasLineOfSight(ClientLevel level, Vec3 from, Vec3 to) {
        return level.clip(new net.minecraft.world.level.ClipContext(
                from,
                to,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                Minecraft.getInstance().player
        )).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
    }

    public static void startArclightAwakening(Entity entity) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide
                || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        ACTIVE_ARCLIGHT_AWAKENINGS.put(entity.getId(), new OrbitState(entity.getId()));
    }

    public static void burstArclightAwakening(Entity entity) {
        if (entity == null || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        ACTIVE_ARCLIGHT_AWAKENINGS.remove(entity.getId());
        if (shouldRenderTransientEffect(entity, entity.tickCount)) {
            spawnArclightAwakeningBurst(entity);
        }
    }

    public static void stopArclightAwakening(Entity entity) {
        if (entity != null) {
            ACTIVE_ARCLIGHT_AWAKENINGS.remove(entity.getId());
        }
    }
    public static void startDragonDescendCast(Entity entity) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
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
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
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
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        ACTIVE_FIRE_STORM_CASTS.put(entity.getId(), new OrbitState(entity.getId()));
    }

    public static void burstFireStormCast(Entity entity) {
        if (entity == null || entity.level() == null || !entity.level().isClientSide) {
            return;
        }

        if (!shouldRenderTransientEffect(entity, entity.tickCount)) {
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

    public static void startMagicalVeil(Entity entity) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        ACTIVE_MAGICAL_VEILS.put(entity.getId(), new OrbitState(entity.getId()));
    }

    public static void stopMagicalVeil(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_MAGICAL_VEILS.remove(entity.getId());
    }

    public static void startBastionFrostAura(Entity entity, float radiusBlocks) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
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
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
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

    public static void startBastionHeavensGiftAura(Entity entity, float radiusBlocks) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        ACTIVE_BASTION_HEAVENS_GIFT_AURAS.put(entity.getId(), new OrbitState(entity.getId(), radiusBlocks));
    }

    public static void stopBastionHeavensGiftAura(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_BASTION_HEAVENS_GIFT_AURAS.remove(entity.getId());
    }
    public static void startArmorIceAura(Entity entity, float radiusBlocks) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        ACTIVE_ARMOR_ICE_AURAS.put(entity.getId(), new OrbitState(entity.getId(), radiusBlocks));
    }

    public static void stopArmorIceAura(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_ARMOR_ICE_AURAS.remove(entity.getId());
    }

    public static void startArmorNetheriteFireRing(Entity entity, float radiusBlocks) {
        if (entity == null || !entity.isAlive() || entity.level() == null || !entity.level().isClientSide || ShadowFormClientState.isConcealed(entity)) {
            return;
        }

        ACTIVE_ARMOR_NETHERITE_FIRE_RINGS.put(entity.getId(), new OrbitState(entity.getId(), radiusBlocks));
    }

    public static void stopArmorNetheriteFireRing(Entity entity) {
        if (entity == null) {
            return;
        }

        ACTIVE_ARMOR_NETHERITE_FIRE_RINGS.remove(entity.getId());
    }

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

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused()) {
            return;
        }

        ClientLevel level = minecraft.level;
        if (level == null) {
            ACTIVE_DRAGON_DESCEND_CASTS.clear();
            ACTIVE_ARCLIGHT_AWAKENINGS.clear();
            ACTIVE_GLACIER_CASTS.clear();
            ACTIVE_FIRE_STORM_CASTS.clear();
            ACTIVE_MAGICAL_VEILS.clear();
            ACTIVE_BASTION_FROST_AURAS.clear();
            ACTIVE_BASTION_RULE_AURAS.clear();
            ACTIVE_BASTION_HEAVENS_GIFT_AURAS.clear();
            ACTIVE_ARMOR_ICE_AURAS.clear();
            ACTIVE_ARMOR_NETHERITE_FIRE_RINGS.clear();
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        for (OrbitState state : ACTIVE_ARCLIGHT_AWAKENINGS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_ARCLIGHT_AWAKENINGS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnArclightAwakening(effect, entity, state.tick);
            }
            state.tick++;
        }
        for (OrbitState state : ACTIVE_DRAGON_DESCEND_CASTS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_DRAGON_DESCEND_CASTS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnOrbit(effect, entity, state.tick);
            }
            state.tick++;
        }

        for (OrbitState state : ACTIVE_GLACIER_CASTS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_GLACIER_CASTS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnGlacierVortex(effect, entity, state.tick);
            }
            state.tick++;
        }

        for (OrbitState state : ACTIVE_FIRE_STORM_CASTS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_FIRE_STORM_CASTS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnFireStormCastOrbit(effect, entity, state.tick);
            }
            state.tick++;
        }

        for (OrbitState state : ACTIVE_MAGICAL_VEILS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_MAGICAL_VEILS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnMagicalVeilOrbit(effect, entity, state.tick);
            }
            state.tick++;
        }

        for (OrbitState state : ACTIVE_BASTION_FROST_AURAS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_BASTION_FROST_AURAS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnBastionFrostAura(effect, entity, state.tick, state.radiusBlocks);
            }
            state.tick++;
        }

        for (OrbitState state : ACTIVE_BASTION_RULE_AURAS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_BASTION_RULE_AURAS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnBastionRuleAura(effect, entity, state.tick, state.radiusBlocks);
            }
            state.tick++;
        }


        for (OrbitState state : ACTIVE_BASTION_HEAVENS_GIFT_AURAS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_BASTION_HEAVENS_GIFT_AURAS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnBastionHeavensGiftAura(effect, entity, state.tick, state.radiusBlocks);
            }
            state.tick++;
        }

        for (OrbitState state : ACTIVE_ARMOR_ICE_AURAS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_ARMOR_ICE_AURAS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnArmorIceAura(effect, entity, state.tick, state.radiusBlocks);
            }
            state.tick++;
        }

        for (OrbitState state : ACTIVE_ARMOR_NETHERITE_FIRE_RINGS.values()) {
            Entity entity = level.getEntity(state.entityId);
            if (entity == null || !entity.isAlive()) {
                ACTIVE_ARMOR_NETHERITE_FIRE_RINGS.remove(state.entityId);
                continue;
            }

            if (!ShadowFormClientState.isConcealed(entity) && shouldRenderPersistentEffect(entity, state.tick)) {
                spawnArmorNetheriteFireRing(effect, entity, state.tick, state.radiusBlocks);
            }
            state.tick++;
        }
    }
    private static void spawnArmorIceAura(StaticLevelEffect effect, Entity entity, int tick, float radiusBlocks) {
        float baseAngle = tick * 0.075F;
        double groundY = entity.getY() + 0.09D;
        double radius = Math.max(1.1D, radiusBlocks);
        int outerCount = photonCount(9);
        int innerCount = photonCount(5);

        for (int i = 0; i < outerCount; i++) {
            float angle = baseAngle + ((Mth.TWO_PI / outerCount) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = entity.getX() + cos * radius;
            double z = entity.getZ() + sin * radius;
            createTrailEmitterNoBloom(SNOWFLAKE_TEXTURE, 0.22F, 18, 0xFFEAF9FF, -sin * 0.012D, 0.008D, cos * 0.012D, (i & 1) == 0 ? 10.0F : -10.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) groundY, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        for (int i = 0; i < innerCount; i++) {
            float angle = -baseAngle * 0.65F + ((Mth.TWO_PI / innerCount) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = entity.getX() + cos * (radius * 0.62D);
            double z = entity.getZ() + sin * (radius * 0.62D);
            createTrailEmitterNoBloom(SNOW_TEXTURE, 0.3F, 16, 0xFFCDEEFF, -cos * 0.006D, 0.016D, -sin * 0.006D, (i & 1) == 0 ? 8.0F : -8.0F, -angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) (groundY + 0.18D), (float) z), IDENTITY_ROTATION, new Vector3f(1.15F, 1.0F, 1.15F));
        }
    }

    private static void spawnArmorNetheriteFireRing(StaticLevelEffect effect, Entity entity, int tick, float radiusBlocks) {
        float baseAngle = tick * 0.32F;
        double baseY = entity.getY() + 0.08D;
        double radius = Math.max(1.2D, radiusBlocks);
        int count = photonCount(10);

        for (int i = 0; i < count; i++) {
            float progress = i / (float) count;
            float angle = baseAngle + ((Mth.TWO_PI / count) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double height = (i % 3) * 0.18D;
            double swirlRadius = radius - (progress * 0.22D);
            double x = entity.getX() + cos * swirlRadius;
            double z = entity.getZ() + sin * swirlRadius;
            double tangentX = -sin * 0.035D;
            double tangentZ = cos * 0.035D;
            double rise = 0.045D + (i % 4) * 0.012D;
            ResourceLocation flame = (i & 1) == 0 ? FIRE_TEXTURE_1 : FIRE_TEXTURE_2;
            int color = (i & 1) == 0 ? 0xFFFFC85A : 0xFFFF5A1E;
            createTrailEmitterNoBloom(flame, 0.28F, 13, color, tangentX, rise, tangentZ, (i & 1) == 0 ? 24.0F : -24.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) (baseY + height), (float) z), IDENTITY_ROTATION, UNIT_SCALE);

            if ((i % 3) == 0) {
                createTrailEmitterNoBloom(SMOKE_TEXTURE_1, 0.24F, 18, 0xFF242424, tangentX * 0.55D, rise * 0.55D, tangentZ * 0.55D, 10.0F)
                        .emmit(effect, new Vector3f((float) x, (float) (baseY + height + 0.06D), (float) z), IDENTITY_ROTATION, UNIT_SCALE);
            }
        }

        createTrailEmitterNoBloom(FIRE_TEXTURE_1, 0.38F, 12, 0xFFFFE08A, 0.0D, 0.04D, 0.0D, 28.0F)
                .emmit(effect, new Vector3f((float) entity.getX(), (float) (baseY + 0.22D), (float) entity.getZ()), IDENTITY_ROTATION, new Vector3f(1.35F, 1.0F, 1.35F));
    }
    private static void spawnBastionFrostAura(StaticLevelEffect effect, Entity entity, int tick, float radiusBlocks) {
        float baseAngle = tick * 0.14F;
        double baseY = entity.getY() + 0.08D;
        double outerRadius = Math.max(1.2D, radiusBlocks - 0.2D);
        double innerRadius = Math.max(0.8D, outerRadius * 0.56D);
        float outerScale = Mth.clamp(radiusBlocks / photonCount(8), 0.45F, 1.35F);
        for (int i = 0; i < photonCount(10); i++) {
            float angle = baseAngle + ((Mth.TWO_PI / photonCount(10)) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double radius = outerRadius + (((i & 1) == 0 ? 0.18D : -0.06D) * outerScale);
            double x = entity.getX() + (cos * radius);
            double z = entity.getZ() + (sin * radius);
            createTrailEmitter(SNOWFLAKE_TEXTURE, 0.24F, 14, 0xFFE8F6FF, -sin * 0.02D, 0.012D, cos * 0.02D, (i & 1) == 0 ? 16.0F : -16.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) x, (float) baseY, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
        }

        for (int i = 0; i < photonCount(6); i++) {
            float angle = -baseAngle * 0.82F + ((Mth.TWO_PI / photonCount(6)) * i);
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
        for (int i = 0; i < photonCount(8); i++) {
            float angle = baseAngle + ((Mth.TWO_PI / photonCount(8)) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            createTrailEmitter(PORTAL_TEXTURE, 0.3F, 14, 0xFFD7C2FF, -sin * 0.026D, 0.008D, cos * 0.026D, (i & 1) == 0 ? 18.0F : -18.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) (entity.getX() + (cos * outerRadius)), (float) groundY, (float) (entity.getZ() + (sin * outerRadius))), IDENTITY_ROTATION, new Vector3f(1.2F, 1.0F, 1.2F));
        }

        for (int i = 0; i < photonCount(5); i++) {
            float angle = -baseAngle * 0.9F + ((Mth.TWO_PI / photonCount(5)) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            createTrailEmitter(PORTAL_TEXTURE, 0.38F, 12, 0xFFF1E8FF, -cos * 0.014D, 0.016D, -sin * 0.014D, (i & 1) == 0 ? 12.0F : -12.0F, -angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) (entity.getX() + (cos * innerRadius)), (float) waistY, (float) (entity.getZ() + (sin * innerRadius))), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static void spawnBastionHeavensGiftAura(StaticLevelEffect effect, Entity entity, int tick, float radiusBlocks) {
        float baseAngle = tick * 0.13F;
        double groundY = entity.getY() + 0.12D;
        double chestY = entity.getY() + Math.max(0.82D, entity.getBbHeight() * 0.52D);
        double outerRadius = Math.max(1.2D, radiusBlocks - 0.18D);
        double middleRadius = Math.max(0.9D, outerRadius * 0.68D);
        double innerRadius = Math.max(0.7D, outerRadius * 0.38D);

        for (int i = 0; i < photonCount(9); i++) {
            float angle = baseAngle + ((Mth.TWO_PI / photonCount(9)) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            createTrailEmitter(HOLY_TEXTURE, 0.28F, 15, 0xFFFFF6D8, -sin * 0.018D, 0.014D, cos * 0.018D, (i & 1) == 0 ? 14.0F : -14.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) (entity.getX() + (cos * outerRadius)), (float) groundY, (float) (entity.getZ() + (sin * outerRadius))), IDENTITY_ROTATION, new Vector3f(1.18F, 1.0F, 1.18F));
        }

        for (int i = 0; i < photonCount(6); i++) {
            float angle = -baseAngle * 0.72F + ((Mth.TWO_PI / photonCount(6)) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            createTrailEmitter(HEAL_TEXTURE, 0.24F, 13, 0xFFE9FFE6, -cos * 0.01D, 0.03D, -sin * 0.01D, (i & 1) == 0 ? 10.0F : -10.0F, -angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) (entity.getX() + (cos * middleRadius)), (float) (groundY + 0.18D), (float) (entity.getZ() + (sin * middleRadius))), IDENTITY_ROTATION, UNIT_SCALE);
        }

        for (int i = 0; i < photonCount(4); i++) {
            float angle = baseAngle * 1.35F + ((Mth.TWO_PI / photonCount(4)) * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            createTrailEmitter(HOLY_TEXTURE, 0.34F, 12, 0xFFFFFFFF, -sin * 0.012D, 0.018D, cos * 0.012D, (i & 1) == 0 ? 12.0F : -12.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, new Vector3f((float) (entity.getX() + (cos * innerRadius)), (float) chestY, (float) (entity.getZ() + (sin * innerRadius))), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }
    private static void spawnArclightAwakening(StaticLevelEffect effect, Entity entity, int tick) {
        Vec3 target = arclightWeaponTarget(entity);
        float ramp = Mth.clamp(tick / 220.0F, 0.0F, 1.0F);
        float baseAngle = tick * (0.22F + ramp * 0.2F);
        int vortexCount = photonCount(8);
        double outerRadius = Mth.lerp(ramp, 5.4D, 3.2D);

        for (int i = 0; i < vortexCount; i++) {
            float lane = i / (float) vortexCount;
            float angle = baseAngle + lane * Mth.TWO_PI + (float) Math.sin(tick * 0.055F + i) * 0.24F;
            double height = 0.2D + (i + tick * 0.18D) % vortexCount / vortexCount * (entity.getBbHeight() + 2.8D);
            double radius = outerRadius * (0.72D + 0.28D * Math.sin(lane * Math.PI));
            Vec3 origin = new Vec3(entity.getX() + Math.cos(angle) * radius, entity.getY() + height, entity.getZ() + Math.sin(angle) * radius);
            Vec3 inward = target.subtract(origin).normalize();
            Vec3 tangent = new Vec3(-Math.sin(angle), 0.0D, Math.cos(angle));
            Vec3 velocity = inward.scale(0.105D + ramp * 0.045D).add(tangent.scale(0.055D));
            ResourceLocation texture = i % 3 == 0 ? LIGHT_BIG_TEXTURE : (i % 2 == 0 ? LIGHT_MEDIUM_TEXTURE : LIGHT_SMALL_TEXTURE);
            float size = i % 3 == 0 ? 0.42F : (i % 2 == 0 ? 0.29F : 0.2F);
            createTrailEmitterNoBloom(texture, size, 20, 0xE900FFFF, velocity.x, velocity.y, velocity.z,
                    (i & 1) == 0 ? 18.0F : -18.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, toVector(origin), IDENTITY_ROTATION, UNIT_SCALE);
        }

        int absorptionCount = photonCount(4);
        for (int i = 0; i < absorptionCount; i++) {
            float angle = -baseAngle * 1.4F + i * (Mth.TWO_PI / absorptionCount);
            double radius = 1.4D + i * 0.32D;
            Vec3 origin = new Vec3(target.x + Math.cos(angle) * radius,
                    target.y + Math.sin(angle * 0.7F) * 1.15D,
                    target.z + Math.sin(angle) * radius);
            Vec3 velocity = target.subtract(origin).normalize().scale(0.12D + ramp * 0.06D);
            ResourceLocation texture = (i & 1) == 0 ? LIGHT_MEDIUM_TEXTURE : LIGHT_SMALL_TEXTURE;
            createTrailEmitterNoBloom(texture, (i & 1) == 0 ? 0.28F : 0.18F, 14, 0xFFFFFFFF,
                    velocity.x, velocity.y, velocity.z, (i & 1) == 0 ? 26.0F : -26.0F)
                    .emmit(effect, toVector(origin), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    private static void spawnArclightAwakeningBurst(Entity entity) {
        StaticLevelEffect effect = new StaticLevelEffect(entity.level());
        Vec3 center = arclightWeaponTarget(entity);
        int count = photonCount(56);

        for (int i = 0; i < count; i++) {
            double y = 1.0D - 2.0D * (i + 0.5D) / count;
            double radial = Math.sqrt(Math.max(0.0D, 1.0D - y * y));
            double angle = i * 2.399963229728653D;
            Vec3 direction = new Vec3(Math.cos(angle) * radial, y, Math.sin(angle) * radial);
            double speed = 0.18D + (i % 7) * 0.018D;
            ResourceLocation texture = i % 5 == 0 ? LIGHT_BIG_TEXTURE : (i % 2 == 0 ? LIGHT_MEDIUM_TEXTURE : LIGHT_SMALL_TEXTURE);
            float size = i % 5 == 0 ? 0.58F : (i % 2 == 0 ? 0.34F : 0.22F);
            createTrailEmitterNoBloom(texture, size, 22, 0xFFFFFFFF,
                    direction.x * speed, direction.y * speed, direction.z * speed,
                    (i & 1) == 0 ? 34.0F : -34.0F, (float) angle * Mth.RAD_TO_DEG)
                    .emmit(effect, toVector(center), IDENTITY_ROTATION, UNIT_SCALE);
        }

        int ringCount = photonCount(24);
        for (int i = 0; i < ringCount; i++) {
            float angle = i * Mth.TWO_PI / ringCount;
            Vec3 direction = new Vec3(Math.cos(angle), 0.08D, Math.sin(angle)).normalize();
            createTrailEmitterNoBloom(LIGHT_BIG_TEXTURE, 0.52F, 18, 0xE900FFFF,
                    direction.x * 0.3D, direction.y * 0.3D, direction.z * 0.3D,
                    (i & 1) == 0 ? 28.0F : -28.0F, angle * Mth.RAD_TO_DEG)
                    .emmit(effect, toVector(center), IDENTITY_ROTATION, new Vector3f(1.25F, 1.0F, 1.25F));
        }
    }

    private static Vec3 arclightWeaponTarget(Entity entity) {
        float yaw = entity.getYRot() * Mth.DEG_TO_RAD;
        Vec3 right = new Vec3(-Math.cos(yaw), 0.0D, -Math.sin(yaw));
        Vec3 forward = entity.getLookAngle().normalize();
        return entity.position()
                .add(0.0D, Math.max(1.0D, entity.getBbHeight() * 0.68D), 0.0D)
                .add(right.scale(0.52D))
                .add(forward.scale(0.34D));
    }
    private static void spawnOrbit(StaticLevelEffect effect, Entity entity, int tick) {
        float baseAngle = (tick * ROTATION_SPEED) % Mth.TWO_PI;
        for (int i = 0; i < photonCount(ORBIT_PARTICLES_PER_TICK); i++) {
            float angle = baseAngle + ((Mth.TWO_PI / photonCount(ORBIT_PARTICLES_PER_TICK)) * i);
            emitPhotonParticle(effect, entity, angle, INNER_RADIUS, INNER_HEIGHT, 0.24F, 7, 0xFFFFFFFF, 0.0D, -0.01D, 0.0D, angle * Mth.RAD_TO_DEG, 26.0F);
            emitPhotonParticle(effect, entity, angle + (Mth.TWO_PI / photonCount(8)), OUTER_RADIUS, OUTER_HEIGHT, 0.32F, 7, 0xFFE8D8FF, 0.0D, 0.018D, 0.0D, -angle * Mth.RAD_TO_DEG, -34.0F);
        }
    }

    private static void spawnGlacierVortex(StaticLevelEffect effect, Entity entity, int tick) {
        float baseAngle = tick * GLACIER_VORTEX_ROTATION_SPEED;
        double baseX = entity.getX();
        double baseY = entity.getY() + 0.05D;
        double baseZ = entity.getZ();
        double topY = entity.getY() + entity.getBbHeight() + 0.35D;

        for (int i = 0; i < photonCount(GLACIER_VORTEX_PARTICLES_PER_TICK); i++) {
            float progress = i / (float) photonCount(GLACIER_VORTEX_PARTICLES_PER_TICK);
            double swirlHeight = progress * Math.max(1.35D, entity.getBbHeight() + 0.2D);
            double radius = Mth.lerp(progress, 1.3D, 0.12D);
            float angle = baseAngle + (progress * 3.6F) + ((Mth.TWO_PI / photonCount(GLACIER_VORTEX_PARTICLES_PER_TICK)) * i);
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

        for (int i = 0; i < photonCount(FIRE_STORM_CAST_PARTICLES_PER_TICK); i++) {
            float progress = i / (float) photonCount(FIRE_STORM_CAST_PARTICLES_PER_TICK);
            double swirlHeight = progress * Math.max(1.6D, entity.getBbHeight() + 0.55D);
            double radius = Mth.lerp(progress, 1.15D, 0.22D);
            float angle = baseAngle + (progress * 4.4F) + ((Mth.TWO_PI / photonCount(FIRE_STORM_CAST_PARTICLES_PER_TICK)) * i);
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

    private static void spawnMagicalVeilOrbit(StaticLevelEffect effect, Entity entity, int tick) {
        float angleStep = Mth.TWO_PI / photonCount(MAGICAL_VEIL_PARTICLES_PER_TICK);
        float baseAngle = tick * MAGICAL_VEIL_ROTATION_SPEED;
        double baseY = entity.getY() + Math.max(0.7D, entity.getBbHeight() * 0.52D);
        double radius = Math.max(0.95D, entity.getBbWidth() * 0.9D);

        for (int i = 0; i < photonCount(MAGICAL_VEIL_PARTICLES_PER_TICK); i++) {
            float angle = baseAngle + (angleStep * i);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = entity.getX() + (cos * radius);
            double z = entity.getZ() + (sin * radius);
            double vx = -sin * 0.012D;
            double vz = cos * 0.012D;
            float size = (i & 1) == 0 ? 0.26F : 0.22F;
            int color = (i & 1) == 0 ? 0xFFF2E7FF : 0xFFD8B8FF;
            float roll = angle * Mth.RAD_TO_DEG;
            createTrailEmitter(MAGICAL_VEIL_TEXTURE, size, 14, color, vx, 0.002D, vz, (i & 1) == 0 ? 10.0F : -10.0F, roll)
                    .emmit(effect, new Vector3f((float) x, (float) baseY, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
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

        for (int i = 0; i < photonCount(FIRE_STORM_BURST_PARTICLE_COUNT); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(FIRE_STORM_BURST_PARTICLE_COUNT);
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

    private static void emitPhotonParticle(StaticLevelEffect effect, Entity entity, float angle, float radius, float height,
                                           float size, int lifetime, int color, double vx, double vy, double vz,
                                           float startRollDegrees, float rollPerTickDegrees) {
        double x = entity.getX() + Mth.cos(angle) * radius;
        double y = entity.getY() + height;
        double z = entity.getZ() + Mth.sin(angle) * radius;
        ParticleEmitter emitter = createTrailEmitter(PORTAL_TEXTURE, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees);
        emitter.emmit(effect, new Vector3f((float) x, (float) y, (float) z), IDENTITY_ROTATION, UNIT_SCALE);
    }

    private static void spawnBurst(Entity entity) {
        if (!(entity.level() instanceof ClientLevel level)) {
            return;
        }

        StaticLevelEffect effect = new StaticLevelEffect(level);
        double baseY = entity.getY() + entity.getBbHeight() * 0.72D;
        for (int i = 0; i < photonCount(BURST_PARTICLE_COUNT); i++) {
            double angle = (Math.PI * 2.0D * i) / photonCount(BURST_PARTICLE_COUNT);
            double spread = BURST_SPEED * (0.85D + ((i & 1) * 0.2D));
            double vx = Math.cos(angle) * spread;
            double vz = Math.sin(angle) * spread;
            double vy = ((i % 4) - 1.5D) * 0.03D;
            float startRollDegrees = (float) Math.toDegrees(angle);
            float rollPerTickDegrees = ((i & 1) == 0) ? 42.0F : -42.0F;
            ParticleEmitter emitter = createTrailEmitter(PORTAL_TEXTURE, 0.34F, 11, 0xFFFFFFFF, vx, vy, vz, rollPerTickDegrees, startRollDegrees);
            emitter.emmit(effect, new Vector3f((float) entity.getX(), (float) baseY, (float) entity.getZ()), IDENTITY_ROTATION, UNIT_SCALE);
        }
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

    private static ParticleEmitter createBreathEmitter() {
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

    private static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                      double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F);
    }

    private static ParticleEmitter createTrailEmitterNoBloom(ResourceLocation texture, float size, int lifetime, int color,
                                                             double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitterNoBloom(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F);
    }

    private static ParticleEmitter createTrailEmitterNoBloom(ResourceLocation texture, float size, int lifetime, int color,
                                                             double vx, double vy, double vz, float rollPerTickDegrees,
                                                             float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, false);
    }

    private static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                      double vx, double vy, double vz, float rollPerTickDegrees,
                                                      float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, true);
    }

    private static ParticleEmitter createLingeringTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                               double vx, double vy, double vz, float rollPerTickDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, 0.0F, true, false);
    }

    private static ParticleEmitter createLingeringTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                               double vx, double vy, double vz, float rollPerTickDegrees,
                                                               float startRollDegrees) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, true, false);
    }

    private static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
                                                      double vx, double vy, double vz, float rollPerTickDegrees,
                                                      float startRollDegrees, boolean bloom) {
        return createTrailEmitter(texture, size, lifetime, color, vx, vy, vz, rollPerTickDegrees, startRollDegrees, bloom, true);
    }

    private static ParticleEmitter createTrailEmitter(ResourceLocation texture, float size, int lifetime, int color,
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
    private static void applySharedMaterial(ParticleConfig config, ResourceLocation texture, int color) {
        applySharedMaterial(config, texture, color, false);
    }

    private static void applySharedMaterial(ParticleConfig config, ResourceLocation texture, int color, boolean bloom) {
        config.material.setCull(false);
        config.material.setDepthMask(false);
        config.material.setDepthTest(true);
        TextureMaterial textureMaterial = new TextureMaterial(texture);
        textureMaterial.discardThreshold = 0.02F;
        config.material.setMaterial(textureMaterial);
        config.renderer.setBloomEffect(false);
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

    private record StaticLevelEffect(Level level) implements IEffect {
        @Override
        public Level getLevel() {
            return this.level;
        }
    }

    private static final class OrbitState {
        private final int entityId;
        private final float radiusBlocks;
        private int tick;

        private OrbitState(int entityId) {
            this(entityId, 8.0F);
        }

        private OrbitState(int entityId, float radiusBlocks) {
            this.entityId = entityId;
            this.radiusBlocks = radiusBlocks;
            this.tick = 0;
        }
    }
}




