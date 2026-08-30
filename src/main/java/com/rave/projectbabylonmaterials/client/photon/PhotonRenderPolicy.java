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

final class PhotonRenderPolicy {
    static final double PHOTON_NEAR_DISTANCE_SQR = 18.0D * 18.0D;
    static final double PHOTON_MEDIUM_DISTANCE_SQR = 32.0D * 32.0D;
    static final double PHOTON_FAR_DISTANCE_SQR = 48.0D * 48.0D;
    static final double PHOTON_MAX_DISTANCE_SQR = 64.0D * 64.0D;
    static final double PHOTON_OCCLUDED_MAX_DISTANCE_SQR = 5.0D * 5.0D;
    static boolean distanceLitePhotonEffect;

    static boolean useLitePhotonEffects() {
        return PBMClientConfig.useLitePhotonEffects() || distanceLitePhotonEffect;
    }


    static int photonCount(int baseCount) {
        if (!useLitePhotonEffects()) {
            return baseCount;
        }
        return Math.max(1, Mth.ceil(baseCount * 0.45F));
    }

    static int photonInterval(int baseInterval) {
        if (!useLitePhotonEffects()) {
            return baseInterval;
        }
        return Math.max(1, baseInterval * 2);
    }

    static int photonLifetime(int baseLifetime) {
        if (!useLitePhotonEffects()) {
            return baseLifetime;
        }
        return Math.max(6, Mth.ceil(baseLifetime * 0.6F));
    }

    static boolean shouldRenderPersistentEffect(Entity entity, int tick) {
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
    static boolean shouldRenderTransientEffect(Entity entity, int tick) {
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
    static boolean shouldRenderPointEffect(ClientLevel level, Vec3 position) {
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
    static int resolveDistanceInterval(double distanceSqr, boolean visible) {
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

    static boolean hasLineOfSight(ClientLevel level, Vec3 from, Vec3 to) {
        return level.clip(new net.minecraft.world.level.ClipContext(
                from,
                to,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                Minecraft.getInstance().player
        )).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
    }
}
