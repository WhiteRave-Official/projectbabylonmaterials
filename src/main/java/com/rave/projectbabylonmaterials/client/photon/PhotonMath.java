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

final class PhotonMath {
    static final Quaternionf IDENTITY_ROTATION = new Quaternionf();
    static final Vector3f UNIT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

    static int withAlpha(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }

    static Vec3 horizontalRight(Vec3 normalized) {
        Vec3 right = new Vec3(-normalized.z, 0.0D, normalized.x);
        return right.lengthSqr() < 1.0E-6D ? new Vec3(1.0D, 0.0D, 0.0D) : right.normalize();
    }

    static Vec3 verticalAxis(Vec3 normalized, Vec3 right) {
        Vec3 up = normalized.cross(right);
        return up.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 1.0D, 0.0D) : up.normalize();
    }


    static Quaternionf quaternionFromDirection(Vec3 direction) {
        Vector3f target = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
        return new Quaternionf().rotateTo(new Vector3f(0.0F, 1.0F, 0.0F), target);
    }

    static Vector3f toVector(Vec3 vec3) {
        return new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
    }

    record StaticLevelEffect(Level level) implements IEffect {
        @Override
        public Level getLevel() {
            return this.level;
        }
    }
}
