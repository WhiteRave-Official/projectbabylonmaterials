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

final class PhotonPersistentEffects {
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
            createTrailEmitterNoBloom(texture, size, 20, 0xE9FFFFFF, velocity.x, velocity.y, velocity.z,
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
            createTrailEmitterNoBloom(LIGHT_BIG_TEXTURE, 0.52F, 18, 0xE9FFFFFF,
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
