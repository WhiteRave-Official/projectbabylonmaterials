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

final class PhotonTextures {
    static final ResourceLocation PORTAL_TEXTURE = texture("dragon_descend_spell.png");
    static final ResourceLocation LIGHT_SMALL_TEXTURE = texture("light_particle_small.png");
    static final ResourceLocation LIGHT_MEDIUM_TEXTURE = texture("light_particle_medium.png");
    static final ResourceLocation LIGHT_BIG_TEXTURE = texture("light_particle_big.png");
    static final ResourceLocation PHANTOM_TEXTURE = texture("phantom_particle.png");
    static final ResourceLocation HOLY_TEXTURE = texture("holy_particle.png");
    static final ResourceLocation HEAL_TEXTURE = texture("heal_particle.png");
    static final ResourceLocation ABSORPTION_TEXTURE = texture("absorption_particle.png");
    static final ResourceLocation MAGICAL_VEIL_TEXTURE = texture("magical_veil_particle.png");
    static final ResourceLocation SPECTRAL_TEXTURE_1 = texture("spectral_particle_1.png");
    static final ResourceLocation SPECTRAL_TEXTURE_2 = texture("spectral_particle_2.png");
    static final ResourceLocation LEAF_TEXTURE = texture("leaf_particle.png");
    static final ResourceLocation SNOWFLAKE_TEXTURE = texture("snowflake_particle.png");
    static final ResourceLocation SNOW_TEXTURE = texture("snow_particle.png");
    static final ResourceLocation GOLDEN_TEXTURE = texture("golden_particle.png");
    static final ResourceLocation DIAMOND_TEXTURE = texture("diamond_particle.png");
    static final ResourceLocation GOLDEN_TEXTURE_2 = texture("golden_particle_2.png");
    static final ResourceLocation DIAMOND_TEXTURE_2 = texture("diamond_particle_2.png");
    static final ResourceLocation FIRE_TEXTURE_1 = texture("fire_particle_1.png");
    static final ResourceLocation FIRE_TEXTURE_2 = texture("fire_particle_2.png");
    static final ResourceLocation SMOKE_TEXTURE_1 = texture("smoke_particle_1.png");
    static final ResourceLocation SMOKE_TEXTURE_2 = texture("smoke_particle_2.png");

    static ResourceLocation texture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/particles/" + fileName);
    }
}
